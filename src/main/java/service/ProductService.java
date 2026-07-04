package com.smartinventory.service;

import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Product;

import java.util.List;

/**
 * Service interface for Product entity.
 * Contains business logic for product management operations.
 */
public interface ProductService {
    
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
     * Retrieves products with pagination and filtering.
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
     * @return the created Product object with generated ID
     * @throws IllegalArgumentException if validation fails
     */
    Product create(Product product) throws IllegalArgumentException;
    
    /**
     * Updates an existing product.
     * 
     * @param product the product to update
     * @return true if update successful, false otherwise
     * @throws IllegalArgumentException if validation fails
     */
    boolean update(Product product) throws IllegalArgumentException;
    
    /**
     * Deletes a product by ID.
     * 
     * @param productId the product ID
     * @return true if deletion successful, false otherwise
     */
    boolean delete(int productId);
    
    /**
     * Validates product data.
     * 
     * @param product the product to validate
     * @return true if valid, false otherwise
     */
    boolean validate(Product product);
}
