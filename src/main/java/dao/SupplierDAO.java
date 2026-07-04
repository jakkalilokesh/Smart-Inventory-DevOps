package com.smartinventory.dao;

import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Supplier;

import java.util.List;

/**
 * Data Access Object interface for Supplier entity.
 * Defines all database operations for supplier management.
 */
public interface SupplierDAO {
    
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
     * Retrieves suppliers with pagination.
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
     * @return the generated supplier ID
     */
    int create(Supplier supplier);
    
    /**
     * Updates an existing supplier.
     * 
     * @param supplier the supplier to update
     * @return true if update successful, false otherwise
     */
    boolean update(Supplier supplier);
    
    /**
     * Deletes a supplier by ID.
     * 
     * @param supplierId the supplier ID
     * @return true if deletion successful, false otherwise
     */
    boolean delete(int supplierId);
    
    /**
     * Checks if a supplier name already exists.
     * 
     * @param supplierName the supplier name to check
     * @return true if supplier name exists, false otherwise
     */
    boolean supplierExists(String supplierName);
    
    /**
     * Counts total number of suppliers.
     * 
     * @return total count
     */
    int count();
    
    /**
     * Counts suppliers matching search criteria.
     * 
     * @param criteria the search criteria
     * @return count of matching suppliers
     */
    int countByCriteria(SearchCriteria criteria);
}
