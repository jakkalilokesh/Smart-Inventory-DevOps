package com.smartinventory.service.impl;

import com.smartinventory.dao.CategoryDAO;
import com.smartinventory.dao.impl.CategoryDAOImpl;
import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Category;
import com.smartinventory.service.CategoryService;
import com.smartinventory.util.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Implementation of CategoryService interface.
 * Contains business logic for category management operations.
 */
public class CategoryServiceImpl implements CategoryService {
    private static final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);
    private CategoryDAO categoryDAO = new CategoryDAOImpl();

    @Override
    public Category findById(int categoryId) {
        return categoryDAO.findById(categoryId);
    }

    @Override
    public Category findByName(String categoryName) {
        return categoryDAO.findByName(categoryName);
    }

    @Override
    public List<Category> findAll() {
        return categoryDAO.findAll();
    }

    @Override
    public List<Category> findActive() {
        return categoryDAO.findActive();
    }

    @Override
    public PaginationDTO<Category> findWithPagination(SearchCriteria criteria) {
        return categoryDAO.findWithPagination(criteria);
    }

    @Override
    public List<Category> search(String keyword) {
        return categoryDAO.search(keyword);
    }

    @Override
    public Category create(Category category) throws IllegalArgumentException {
        if (!validate(category)) {
            throw new IllegalArgumentException("Invalid category data");
        }
        
        if (categoryDAO.categoryExists(category.getCategoryName())) {
            throw new IllegalArgumentException("Category name already exists");
        }
        
        // Validate parent category if specified
        if (category.getParentCategoryId() != null && category.getParentCategoryId() > 0) {
            Category parentCategory = categoryDAO.findById(category.getParentCategoryId());
            if (parentCategory == null) {
                throw new IllegalArgumentException("Parent category not found");
            }
        }
        
        int categoryId = categoryDAO.create(category);
        if (categoryId > 0) {
            category.setCategoryId(categoryId);
            logger.info("Category created successfully: {}", category.getCategoryName());
            return category;
        }
        
        throw new IllegalArgumentException("Failed to create category");
    }

    @Override
    public boolean update(Category category) throws IllegalArgumentException {
        if (!validate(category)) {
            throw new IllegalArgumentException("Invalid category data");
        }
        
        Category existingCategory = categoryDAO.findById(category.getCategoryId());
        if (existingCategory == null) {
            throw new IllegalArgumentException("Category not found");
        }
        
        // Check if category name is being changed and if it already exists
        if (!existingCategory.getCategoryName().equals(category.getCategoryName()) && 
            categoryDAO.categoryExists(category.getCategoryName())) {
            throw new IllegalArgumentException("Category name already exists");
        }
        
        // Validate parent category if specified
        if (category.getParentCategoryId() != null && category.getParentCategoryId() > 0) {
            // Prevent circular reference
            if (category.getParentCategoryId().equals(category.getCategoryId())) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }
            
            Category parentCategory = categoryDAO.findById(category.getParentCategoryId());
            if (parentCategory == null) {
                throw new IllegalArgumentException("Parent category not found");
            }
        }
        
        return categoryDAO.update(category);
    }

    @Override
    public boolean delete(int categoryId) {
        Category category = categoryDAO.findById(categoryId);
        if (category == null) {
            logger.warn("Cannot delete non-existent category: {}", categoryId);
            return false;
        }
        
        // Check if category has child categories
        // This would require additional DAO method, for now we'll proceed
        // In production, you should check for dependent records
        
        return categoryDAO.delete(categoryId);
    }

    @Override
    public boolean validate(Category category) {
        if (category == null) {
            return false;
        }
        
        // Validate category name
        if (!ValidationUtil.isValidCategory(category.getCategoryName())) {
            logger.warn("Invalid category name: {}", category.getCategoryName());
            return false;
        }
        
        // Validate status
        if (category.getStatus() == null || 
            (!category.getStatus().equals("ACTIVE") && 
             !category.getStatus().equals("INACTIVE"))) {
            logger.warn("Invalid status: {}", category.getStatus());
            return false;
        }
        
        return true;
    }
}
