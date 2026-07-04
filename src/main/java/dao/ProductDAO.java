package com.smartinventory.dao;

import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Product;

import java.util.List;

/**
 * Data Access Object interface for Product entity.
 * Defines all database operations for product management.
 */
public interface ProductDAO {
    
    /**
     * Finds a product by ID.
     * 
     * @param productId the product ID
     * @return the Product object if found, null otherwise
     */
    Product findById(int productId);
    
    /**
     * Finds a product by SKU.
     * 
     * @param sku the product SKU
     * @return the Product object if found, null otherwise
     */
    Product findBySku(String sku);
    
    /**
     * Finds a product by barcode.
     * 
     * @param barcode the product barcode
     * @return the Product object if found, null otherwise
     */
    Product findByBarcode(String barcode);
    
    /**
     * Retrieves all products.
     * 
     * @return list of all products
     */
    List<Product> findAll();
    
    /**
     * Retrieves all active products.
     * 
     * @return list of active products
     */
    List<Product> findActive();
    
    /**
     * Retrieves products with pagination.
     * 
     * @param criteria the search criteria
     * @return paginated list of products
     */
    PaginationDTO<Product> findWithPagination(SearchCriteria criteria);
    
    /**
     * Searches products by keyword.
     * 
     * @param keyword the search keyword
     * @return list of matching products
     */
    List<Product> search(String keyword);
    
    /**
     * Retrieves products by category.
     * 
     * @param categoryId the category ID
     * @return list of products in the category
     */
    List<Product> findByCategory(int categoryId);
    
    /**
     * Retrieves products by supplier.
     * 
     * @param supplierId the supplier ID
     * @return list of products from the supplier
     */
    List<Product> findBySupplier(int supplierId);
    
    /**
     * Retrieves products with low stock.
     * 
     * @return list of products with stock at or below minimum level
     */
    List<Product> findLowStock();
    
    /**
     * Creates a new product.
     * 
     * @param product the product to create
     * @return the generated product ID
     */
    int create(Product product);
    
    /**
     * Updates an existing product.
     * 
     * @param product the product to update
     * @return true if update successful, false otherwise
     */
    boolean update(Product product);
    
    /**
     * Updates product stock quantity.
     * 
     * @param productId the product ID
     * @param quantity the new quantity
     * @return true if update successful, false otherwise
     */
    boolean updateStock(int productId, int quantity);
    
    /**
     * Deletes a product by ID.
     * 
     * @param productId the product ID
     * @return true if deletion successful, false otherwise
     */
    boolean delete(int productId);
    
    /**
     * Checks if a SKU already exists.
     * 
     * @param sku the SKU to check
     * @return true if SKU exists, false otherwise
     */
    boolean skuExists(String sku);
    
    /**
     * Checks if a barcode already exists.
     * 
     * @param barcode the barcode to check
     * @return true if barcode exists, false otherwise
     */
    boolean barcodeExists(String barcode);
    
    /**
     * Counts total number of products.
     * 
     * @return total count
     */
    int count();
    
    /**
     * Counts products matching search criteria.
     * 
     * @param criteria the search criteria
     * @return count of matching products
     */
    int countByCriteria(SearchCriteria criteria);
    
    /**
     * Counts products with low stock.
     * 
     * @return count of low stock products
     */
    int countLowStock();
}
