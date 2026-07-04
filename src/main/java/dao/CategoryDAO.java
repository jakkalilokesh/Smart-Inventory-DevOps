package com.smartinventory.dao;

import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Category;

import java.util.List;

/**
 * Data Access Object interface for Category entity.
 * Defines all database operations for category management.
 */
public interface CategoryDAO {
    
    /**
     * Finds a category by ID.
     * 
     * @param categoryId the category ID
     * @return the Category object if found, null otherwise
     */
    Category findById(int categoryId);
    
    /**
     * Finds a category by name.
     * 
     * @param categoryName the category name
     * @return the Category object if found, null otherwise
     */
    Category findByName(String categoryName);
    
    /**
     * Retrieves all categories.
     * 
     * @return list of all categories
     */
    List<Category> findAll();
    
    /**
     * Retrieves all active categories.
     * 
     * @return list of active categories
     */
    List<Category> findActive();
    
    /**
     * Retrieves categories with pagination.
     * 
     * @param criteria the search criteria
     * @return paginated list of categories
     */
    PaginationDTO<Category> findWithPagination(SearchCriteria criteria);
    
    /**
     * Searches categories by keyword.
     * 
     * @param keyword the search keyword
     * @return list of matching categories
     */
    List<Category> search(String keyword);
    
    /**
     * Creates a new category.
     * 
     * @param category the category to create
     * @return the generated category ID
     */
    int create(Category category);
    
    /**
     * Updates an existing category.
     * 
     * @param category the category to update
     * @return true if update successful, false otherwise
     */
    boolean update(Category category);
    
    /**
     * Deletes a category by ID.
     * 
     * @param categoryId the category ID
     * @return true if deletion successful, false otherwise
     */
    boolean delete(int categoryId);
    
    /**
     * Checks if a category name already exists.
     * 
     * @param categoryName the category name to check
     * @return true if category name exists, false otherwise
     */
    boolean categoryExists(String categoryName);
    
    /**
     * Counts total number of categories.
     * 
     * @return total count
     */
    int count();
    
    /**
     * Counts categories matching search criteria.
     * 
     * @param criteria the search criteria
     * @return count of matching categories
     */
    int countByCriteria(SearchCriteria criteria);
}
