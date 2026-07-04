package com.smartinventory.service.impl;

import com.smartinventory.dao.ProductDAO;
import com.smartinventory.dao.impl.ProductDAOImpl;
import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Product;
import com.smartinventory.service.ProductService;
import com.smartinventory.util.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Implementation of ProductService interface.
 * Contains business logic for product management operations.
 */
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);
    private ProductDAO productDAO = new ProductDAOImpl();

    // Package-private setter for mock injection in tests
    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public Product findById(int productId) {
        return productDAO.findById(productId);
    }

    @Override
    public Product findBySku(String sku) {
        return productDAO.findBySku(sku);
    }

    @Override
    public Product findByBarcode(String barcode) {
        return productDAO.findByBarcode(barcode);
    }

    @Override
    public List<Product> findAll() {
        return productDAO.findAll();
    }

    @Override
    public List<Product> findActive() {
        return productDAO.findActive();
    }

    @Override
    public PaginationDTO<Product> findWithPagination(SearchCriteria criteria) {
        return productDAO.findWithPagination(criteria);
    }

    @Override
    public List<Product> search(String keyword) {
        return productDAO.search(keyword);
    }

    @Override
    public List<Product> findByCategory(int categoryId) {
        return productDAO.findByCategory(categoryId);
    }

    @Override
    public List<Product> findBySupplier(int supplierId) {
        return productDAO.findBySupplier(supplierId);
    }

    @Override
    public List<Product> findLowStock() {
        return productDAO.findLowStock();
    }

    @Override
    public Product create(Product product) throws IllegalArgumentException {
        if (!validate(product)) {
            throw new IllegalArgumentException("Invalid product data");
        }
        
        if (productDAO.skuExists(product.getSku())) {
            throw new IllegalArgumentException("SKU already exists");
        }
        
        if (product.getBarcode() != null && !product.getBarcode().isEmpty() && 
            productDAO.barcodeExists(product.getBarcode())) {
            throw new IllegalArgumentException("Barcode already exists");
        }
        
        // Validate selling price is greater than buying price
        if (product.getSellingPrice().compareTo(product.getBuyingPrice()) <= 0) {
            throw new IllegalArgumentException("Selling price must be greater than buying price");
        }
        
        int productId = productDAO.create(product);
        if (productId > 0) {
            product.setProductId(productId);
            logger.info("Product created successfully: {}", product.getProductName());
            return product;
        }
        
        throw new IllegalArgumentException("Failed to create product");
    }

    @Override
    public boolean update(Product product) throws IllegalArgumentException {
        if (!validate(product)) {
            throw new IllegalArgumentException("Invalid product data");
        }
        
        Product existingProduct = productDAO.findById(product.getProductId());
        if (existingProduct == null) {
            throw new IllegalArgumentException("Product not found");
        }
        
        // Check if SKU is being changed and if it already exists
        if (!existingProduct.getSku().equals(product.getSku()) && 
            productDAO.skuExists(product.getSku())) {
            throw new IllegalArgumentException("SKU already exists");
        }
        
        // Check if barcode is being changed and if it already exists
        if (product.getBarcode() != null && !product.getBarcode().isEmpty()) {
            if (!product.getBarcode().equals(existingProduct.getBarcode()) && 
                productDAO.barcodeExists(product.getBarcode())) {
                throw new IllegalArgumentException("Barcode already exists");
            }
        }
        
        // Validate selling price is greater than buying price
        if (product.getSellingPrice().compareTo(product.getBuyingPrice()) <= 0) {
            throw new IllegalArgumentException("Selling price must be greater than buying price");
        }
        
        return productDAO.update(product);
    }

    @Override
    public boolean delete(int productId) {
        Product product = productDAO.findById(productId);
        if (product == null) {
            logger.warn("Cannot delete non-existent product: {}", productId);
            return false;
        }
        
        // Check if product has inventory transactions
        // This would require additional DAO method, for now we'll proceed
        // In production, you should check for dependent records
        
        return productDAO.delete(productId);
    }

    @Override
    public boolean validate(Product product) {
        if (product == null) {
            return false;
        }
        
        // Validate SKU
        if (!ValidationUtil.isValidSku(product.getSku())) {
            logger.warn("Invalid SKU: {}", product.getSku());
            return false;
        }
        
        // Validate barcode if provided
        if (product.getBarcode() != null && !product.getBarcode().isEmpty()) {
            if (!ValidationUtil.isValidBarcode(product.getBarcode())) {
                logger.warn("Invalid barcode: {}", product.getBarcode());
                return false;
            }
        }
        
        // Validate product name
        if (!ValidationUtil.isNotEmpty(product.getProductName())) {
            logger.warn("Invalid product name");
            return false;
        }
        
        // Validate prices
        if (!ValidationUtil.isPositive(product.getBuyingPrice())) {
            logger.warn("Invalid buying price");
            return false;
        }
        
        if (!ValidationUtil.isPositive(product.getSellingPrice())) {
            logger.warn("Invalid selling price");
            return false;
        }
        
        // Validate stock quantities
        if (!ValidationUtil.isNonNegative(product.getStockQuantity())) {
            logger.warn("Invalid stock quantity");
            return false;
        }
        
        if (!ValidationUtil.isPositive(product.getMinimumStock())) {
            logger.warn("Invalid minimum stock");
            return false;
        }
        
        // Validate category ID
        if (product.getCategoryId() <= 0) {
            logger.warn("Invalid category ID");
            return false;
        }
        
        // Validate status
        if (product.getStatus() == null || 
            (!product.getStatus().equals("ACTIVE") && 
             !product.getStatus().equals("INACTIVE") && 
             !product.getStatus().equals("DISCONTINUED"))) {
            logger.warn("Invalid status: {}", product.getStatus());
            return false;
        }
        
        return true;
    }
}
