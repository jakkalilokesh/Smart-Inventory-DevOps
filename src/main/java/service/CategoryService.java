package com.smartinventory.service;

import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Category;

import java.util.List;

/**
 * Service interface for Category entity.
 * Contains business logic for category management operations.
 */
public interface CategoryService {
    
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
     * Retrieves categories with pagination and filtering.
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
     * @return the created Category object with generated ID
     * @throws IllegalArgumentException if validation fails
     */
    Category create(Category category) throws IllegalArgumentException;
    
    /**
     * Updates an existing category.
     * 
     * @param category the category to update
     * @return true if update successful, false otherwise
     * @throws IllegalArgumentException if validation fails
     */
    boolean update(Category category) throws IllegalArgumentException;
    
    /**
     * Deletes a category by ID.
     * 
     * @param categoryId the category ID
     * @return true if deletion successful, false otherwise
     */
    boolean delete(int categoryId);
    
    /**
     * Validates category data.
     * 
     * @param category the category to validate
     * @return true if valid, false otherwise
     */
    boolean validate(Category category);
}
