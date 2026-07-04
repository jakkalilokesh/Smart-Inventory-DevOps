package com.smartinventory.service;

import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Supplier;

import java.util.List;

/**
 * Service interface for Supplier entity.
 * Contains business logic for supplier management operations.
 */
public interface SupplierService {
    
    /**
     * Finds a supplier by ID.
     * 
     * @param supplierId the supplier ID
     * @return the Supplier object if found, null otherwise
     */
    Supplier findById(int supplierId);
    
    /**
     * Finds a supplier by name.
     * 
     * @param supplierName the supplier name
     * @return the Supplier object if found, null otherwise
     */
    Supplier findByName(String supplierName);
    
    /**
     * Retrieves all suppliers.
     * 
     * @return list of all suppliers
     */
    List<Supplier> findAll();
    
    /**
     * Retrieves all active suppliers.
     * 
     * @return list of active suppliers
     */
    List<Supplier> findActive();
    
    /**
     * Retrieves suppliers with pagination and filtering.
     * 
     * @param criteria the search criteria
     * @return paginated list of suppliers
     */
    PaginationDTO<Supplier> findWithPagination(SearchCriteria criteria);
    
    /**
     * Searches suppliers by keyword.
     * 
     * @param keyword the search keyword
     * @return list of matching suppliers
     */
    List<Supplier> search(String keyword);
    
    /**
     * Creates a new supplier.
     * 
     * @param supplier the supplier to create
     * @return the created Supplier object with generated ID
     * @throws IllegalArgumentException if validation fails
     */
    Supplier create(Supplier supplier) throws IllegalArgumentException;
    
    /**
     * Updates an existing supplier.
     * 
     * @param supplier the supplier to update
     * @return true if update successful, false otherwise
     * @throws IllegalArgumentException if validation fails
     */
    boolean update(Supplier supplier) throws IllegalArgumentException;
    
    /**
     * Deletes a supplier by ID.
     * 
     * @param supplierId the supplier ID
     * @return true if deletion successful, false otherwise
     */
    boolean delete(int supplierId);
    
    /**
     * Validates supplier data.
     * 
     * @param supplier the supplier to validate
     * @return true if valid, false otherwise
     */
    boolean validate(Supplier supplier);
}
