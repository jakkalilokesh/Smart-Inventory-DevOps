package com.smartinventory.service;

import com.smartinventory.dto.StockTransactionDTO;
import com.smartinventory.model.InventoryTransaction;
import com.smartinventory.model.Product;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Inventory operations.
 * Contains business logic for stock management operations.
 */
public interface InventoryService {
    
    /**
     * Processes a stock in transaction.
     * 
     * @param transaction the stock transaction details
     * @return true if transaction successful, false otherwise
     * @throws IllegalArgumentException if validation fails
     */
    boolean processStockIn(StockTransactionDTO transaction) throws IllegalArgumentException;
    
    /**
     * Processes a stock out transaction.
     * 
     * @param transaction the stock transaction details
     * @return true if transaction successful, false otherwise
     * @throws IllegalArgumentException if validation fails
     */
    boolean processStockOut(StockTransactionDTO transaction) throws IllegalArgumentException;
    
    /**
     * Processes a stock adjustment.
     * 
     * @param transaction the stock transaction details
     * @return true if transaction successful, false otherwise
     * @throws IllegalArgumentException if validation fails
     */
    boolean processAdjustment(StockTransactionDTO transaction) throws IllegalArgumentException;
    
    /**
     * Retrieves all inventory transactions.
     * 
     * @return list of all transactions
     */
    List<InventoryTransaction> findAllTransactions();
    
    /**
     * Retrieves transactions for a specific product.
     * 
     * @param productId the product ID
     * @return list of transactions for the product
     */
    List<InventoryTransaction> findTransactionsByProduct(int productId);
    
    /**
     * Retrieves transactions by type.
     * 
     * @param transactionType the transaction type
     * @return list of transactions of the specified type
     */
    List<InventoryTransaction> findTransactionsByType(String transactionType);
    
    /**
     * Retrieves transactions within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of transactions within the date range
     */
    List<InventoryTransaction> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Retrieves recent transactions.
     * 
     * @param limit the maximum number of transactions to return
     * @return list of recent transactions
     */
    List<InventoryTransaction> findRecentTransactions(int limit);
    
    /**
     * Checks if a product has sufficient stock.
     * 
     * @param productId the product ID
     * @param quantity the required quantity
     * @return true if sufficient stock, false otherwise
     */
    boolean hasSufficientStock(int productId, int quantity);
    
    /**
     * Gets current stock level for a product.
     * 
     * @param productId the product ID
     * @return current stock quantity
     */
    int getCurrentStock(int productId);
}
