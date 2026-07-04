package com.smartinventory.service.impl;

import com.smartinventory.dao.InventoryTransactionDAO;
import com.smartinventory.dao.ProductDAO;
import com.smartinventory.dao.impl.InventoryTransactionDAOImpl;
import com.smartinventory.dao.impl.ProductDAOImpl;
import com.smartinventory.dto.StockTransactionDTO;
import com.smartinventory.model.InventoryTransaction;
import com.smartinventory.model.Product;
import com.smartinventory.service.InventoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of InventoryService interface.
 * Contains business logic for inventory and stock management operations.
 */
public class InventoryServiceImpl implements InventoryService {
    private static final Logger logger = LogManager.getLogger(InventoryServiceImpl.class);
    private InventoryTransactionDAO transactionDAO = new InventoryTransactionDAOImpl();
    private ProductDAO productDAO = new ProductDAOImpl();

    @Override
    public boolean processStockIn(StockTransactionDTO transaction) throws IllegalArgumentException {
        if (transaction == null || transaction.getProductId() <= 0 || transaction.getQuantity() <= 0) {
            throw new IllegalArgumentException("Invalid transaction data");
        }
        
        Product product = productDAO.findById(transaction.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        
        int currentStock = product.getStockQuantity();
        int newStock = currentStock + transaction.getQuantity();
        
        // Calculate total price
        BigDecimal totalPrice = null;
        if (transaction.getUnitPrice() != null) {
            totalPrice = transaction.getUnitPrice().multiply(BigDecimal.valueOf(transaction.getQuantity()));
        }
        
        // Create transaction record
        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        inventoryTransaction.setTransactionType("STOCK_IN");
        inventoryTransaction.setProductId(transaction.getProductId());
        inventoryTransaction.setQuantity(transaction.getQuantity());
        inventoryTransaction.setPreviousQuantity(currentStock);
        inventoryTransaction.setNewQuantity(newStock);
        inventoryTransaction.setUnitPrice(transaction.getUnitPrice());
        inventoryTransaction.setTotalPrice(totalPrice);
        inventoryTransaction.setReferenceNumber(transaction.getReferenceNumber());
        inventoryTransaction.setNotes(transaction.getNotes());
        inventoryTransaction.setPerformedBy(transaction.getPerformedBy());
        
        // Update product stock
        if (!productDAO.updateStock(transaction.getProductId(), newStock)) {
            throw new IllegalArgumentException("Failed to update product stock");
        }
        
        // Record transaction
        int transactionId = transactionDAO.create(inventoryTransaction);
        if (transactionId <= 0) {
            logger.error("Failed to create transaction record");
            // Rollback stock update
            productDAO.updateStock(transaction.getProductId(), currentStock);
            return false;
        }
        
        logger.info("Stock in processed successfully for product: {}", product.getProductName());
        return true;
    }

    @Override
    public boolean processStockOut(StockTransactionDTO transaction) throws IllegalArgumentException {
        if (transaction == null || transaction.getProductId() <= 0 || transaction.getQuantity() <= 0) {
            throw new IllegalArgumentException("Invalid transaction data");
        }
        
        Product product = productDAO.findById(transaction.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        
        int currentStock = product.getStockQuantity();
        
        // Check sufficient stock
        if (currentStock < transaction.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + currentStock + ", Required: " + transaction.getQuantity());
        }
        
        int newStock = currentStock - transaction.getQuantity();
        
        // Calculate total price
        BigDecimal totalPrice = null;
        if (transaction.getUnitPrice() != null) {
            totalPrice = transaction.getUnitPrice().multiply(BigDecimal.valueOf(transaction.getQuantity()));
        }
        
        // Create transaction record
        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        inventoryTransaction.setTransactionType("STOCK_OUT");
        inventoryTransaction.setProductId(transaction.getProductId());
        inventoryTransaction.setQuantity(transaction.getQuantity());
        inventoryTransaction.setPreviousQuantity(currentStock);
        inventoryTransaction.setNewQuantity(newStock);
        inventoryTransaction.setUnitPrice(transaction.getUnitPrice());
        inventoryTransaction.setTotalPrice(totalPrice);
        inventoryTransaction.setReferenceNumber(transaction.getReferenceNumber());
        inventoryTransaction.setNotes(transaction.getNotes());
        inventoryTransaction.setPerformedBy(transaction.getPerformedBy());
        
        // Update product stock
        if (!productDAO.updateStock(transaction.getProductId(), newStock)) {
            throw new IllegalArgumentException("Failed to update product stock");
        }
        
        // Record transaction
        int transactionId = transactionDAO.create(inventoryTransaction);
        if (transactionId <= 0) {
            logger.error("Failed to create transaction record");
            // Rollback stock update
            productDAO.updateStock(transaction.getProductId(), currentStock);
            return false;
        }
        
        logger.info("Stock out processed successfully for product: {}", product.getProductName());
        return true;
    }

    @Override
    public boolean processAdjustment(StockTransactionDTO transaction) throws IllegalArgumentException {
        if (transaction == null || transaction.getProductId() <= 0) {
            throw new IllegalArgumentException("Invalid transaction data");
        }
        
        Product product = productDAO.findById(transaction.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        
        int currentStock = product.getStockQuantity();
        int newStock = transaction.getQuantity();
        
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        
        int quantityChange = newStock - currentStock;
        
        // Create transaction record
        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        inventoryTransaction.setTransactionType("ADJUSTMENT");
        inventoryTransaction.setProductId(transaction.getProductId());
        inventoryTransaction.setQuantity(Math.abs(quantityChange));
        inventoryTransaction.setPreviousQuantity(currentStock);
        inventoryTransaction.setNewQuantity(newStock);
        inventoryTransaction.setUnitPrice(transaction.getUnitPrice());
        inventoryTransaction.setTotalPrice(transaction.getUnitPrice() != null ? 
            transaction.getUnitPrice().multiply(BigDecimal.valueOf(Math.abs(quantityChange))) : null);
        inventoryTransaction.setReferenceNumber(transaction.getReferenceNumber());
        inventoryTransaction.setNotes(transaction.getNotes());
        inventoryTransaction.setPerformedBy(transaction.getPerformedBy());
        
        // Update product stock
        if (!productDAO.updateStock(transaction.getProductId(), newStock)) {
            throw new IllegalArgumentException("Failed to update product stock");
        }
        
        // Record transaction
        int transactionId = transactionDAO.create(inventoryTransaction);
        if (transactionId <= 0) {
            logger.error("Failed to create transaction record");
            // Rollback stock update
            productDAO.updateStock(transaction.getProductId(), currentStock);
            return false;
        }
        
        logger.info("Stock adjustment processed successfully for product: {}", product.getProductName());
        return true;
    }

    @Override
    public List<InventoryTransaction> findAllTransactions() {
        return transactionDAO.findAll();
    }

    @Override
    public List<InventoryTransaction> findTransactionsByProduct(int productId) {
        return transactionDAO.findByProduct(productId);
    }

    @Override
    public List<InventoryTransaction> findTransactionsByType(String transactionType) {
        return transactionDAO.findByType(transactionType);
    }

    @Override
    public List<InventoryTransaction> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionDAO.findByDateRange(startDate, endDate);
    }

    @Override
    public List<InventoryTransaction> findRecentTransactions(int limit) {
        return transactionDAO.findRecent(limit);
    }

    @Override
    public boolean hasSufficientStock(int productId, int quantity) {
        Product product = productDAO.findById(productId);
        if (product == null) {
            return false;
        }
        return product.getStockQuantity() >= quantity;
    }

    @Override
    public int getCurrentStock(int productId) {
        Product product = productDAO.findById(productId);
        if (product == null) {
            return 0;
        }
        return product.getStockQuantity();
    }
}
