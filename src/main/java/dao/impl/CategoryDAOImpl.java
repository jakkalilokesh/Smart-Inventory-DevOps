package com.smartinventory.dao.impl;

import com.smartinventory.dao.CategoryDAO;
import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Category;
import com.smartinventory.util.DatabaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of CategoryDAO interface.
 * Handles all database operations for category management using JDBC.
 */
public class CategoryDAOImpl implements CategoryDAO {
    private static final Logger logger = LogManager.getLogger(CategoryDAOImpl.class);

    @Override
    public Category findById(int categoryId) {
        String sql = "SELECT c.*, pc.category_name as parent_category_name, u.username as created_by_name " +
                     "FROM categories c " +
                     "LEFT JOIN categories pc ON c.parent_category_id = pc.category_id " +
                     "LEFT JOIN users u ON c.created_by = u.user_id " +
                     "WHERE c.category_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapRowToCategory(rs);
            }
        } catch (SQLException e) {
            logger.error("Error finding category by ID: {}", categoryId, e);
        }
        return null;
    }

    @Override
    public Category findByName(String categoryName) {
        String sql = "SELECT c.*, pc.category_name as parent_category_name, u.username as created_by_name " +
                     "FROM categories c " +
                     "LEFT JOIN categories pc ON c.parent_category_id = pc.category_id " +
                     "LEFT JOIN users u ON c.created_by = u.user_id " +
                     "WHERE c.category_name = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapRowToCategory(rs);
            }
        } catch (SQLException e) {
            logger.error("Error finding category by name: {}", categoryName, e);
        }
        return null;
    }

    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.*, pc.category_name as parent_category_name, u.username as created_by_name " +
                     "FROM categories c " +
                     "LEFT JOIN categories pc ON c.parent_category_id = pc.category_id " +
                     "LEFT JOIN users u ON c.created_by = u.user_id " +
                     "ORDER BY c.category_name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapRowToCategory(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all categories", e);
        }
        return categories;
    }

    @Override
    public List<Category> findActive() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.*, pc.category_name as parent_category_name, u.username as created_by_name " +
                     "FROM categories c " +
                     "LEFT JOIN categories pc ON c.parent_category_id = pc.category_id " +
                     "LEFT JOIN users u ON c.created_by = u.user_id " +
                     "WHERE c.status = 'ACTIVE' " +
                     "ORDER BY c.category_name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapRowToCategory(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding active categories", e);
        }
        return categories;
    }

    @Override
    public PaginationDTO<Category> findWithPagination(SearchCriteria criteria) {
        List<Category> categories = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT c.*, pc.category_name as parent_category_name, u.username as created_by_name " +
            "FROM categories c " +
            "LEFT JOIN categories pc ON c.parent_category_id = pc.category_id " +
            "LEFT JOIN users u ON c.created_by = u.user_id " +
            "WHERE 1=1"
        );
        
        // Add search filter
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            sql.append(" AND (c.category_name LIKE ? OR c.description LIKE ?)");
        }
        
        // Add status filter
        if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
            sql.append(" AND c.status = ?");
        }
        
        // Add sorting
        sql.append(" ORDER BY c.").append(criteria.getSortBy()).append(" ").append(criteria.getSortOrder());
        
        // Add pagination
        sql.append(" LIMIT ? OFFSET ?");
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
                String keyword = "%" + criteria.getKeyword() + "%";
                stmt.setString(paramIndex++, keyword);
                stmt.setString(paramIndex++, keyword);
            }
            
            if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
                stmt.setString(paramIndex++, criteria.getStatus());
            }
            
            stmt.setInt(paramIndex++, criteria.getPageSize());
            stmt.setInt(paramIndex, criteria.getOffset());
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                categories.add(mapRowToCategory(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding categories with pagination", e);
        }
        
        int totalRecords = countByCriteria(criteria);
        return new PaginationDTO<>(categories, criteria.getPage(), criteria.getPageSize(), totalRecords);
    }

    @Override
    public List<Category> search(String keyword) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.*, pc.category_name as parent_category_name, u.username as created_by_name " +
                     "FROM categories c " +
                     "LEFT JOIN categories pc ON c.parent_category_id = pc.category_id " +
                     "LEFT JOIN users u ON c.created_by = u.user_id " +
                     "WHERE c.category_name LIKE ? OR c.description LIKE ? " +
                     "ORDER BY c.category_name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                categories.add(mapRowToCategory(rs));
            }
        } catch (SQLException e) {
            logger.error("Error searching categories: {}", keyword, e);
        }
        return categories;
    }

    @Override
    public int create(Category category) {
        String sql = "INSERT INTO categories (category_name, description, parent_category_id, status, created_by) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, category.getCategoryName());
            stmt.setString(2, category.getDescription());
            stmt.setObject(3, category.getParentCategoryId());
            stmt.setString(4, category.getStatus());
            stmt.setObject(5, category.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int categoryId = rs.getInt(1);
                        logger.info("Category created successfully: {}", category.getCategoryName());
                        return categoryId;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating category: {}", category.getCategoryName(), e);
        }
        return -1;
    }

    @Override
    public boolean update(Category category) {
        String sql = "UPDATE categories SET category_name = ?, description = ?, " +
                     "parent_category_id = ?, status = ? WHERE category_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category.getCategoryName());
            stmt.setString(2, category.getDescription());
            stmt.setObject(3, category.getParentCategoryId());
            stmt.setString(4, category.getStatus());
            stmt.setInt(5, category.getCategoryId());
            
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                logger.info("Category updated successfully: {}", category.getCategoryName());
            }
            return success;
        } catch (SQLException e) {
            logger.error("Error updating category: {}", category.getCategoryName(), e);
        }
        return false;
    }

    @Override
    public boolean delete(int categoryId) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryId);
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                logger.info("Category deleted successfully: {}", categoryId);
            }
            return success;
        } catch (SQLException e) {
            logger.error("Error deleting category: {}", categoryId, e);
        }
        return false;
    }

    @Override
    public boolean categoryExists(String categoryName) {
        String sql = "SELECT COUNT(*) FROM categories WHERE category_name = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking category existence: {}", categoryName, e);
        }
        return false;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM categories";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting categories", e);
        }
        return 0;
    }

    @Override
    public int countByCriteria(SearchCriteria criteria) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM categories WHERE 1=1");
        
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            sql.append(" AND (category_name LIKE ? OR description LIKE ?)");
        }
        
        if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
            sql.append(" AND status = ?");
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
                String keyword = "%" + criteria.getKeyword() + "%";
                stmt.setString(paramIndex++, keyword);
                stmt.setString(paramIndex++, keyword);
            }
            
            if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
                stmt.setString(paramIndex, criteria.getStatus());
            }
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting categories by criteria", e);
        }
        return 0;
    }

    /**
     * Maps a ResultSet row to a Category object.
     */
    private Category mapRowToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryName(rs.getString("category_name"));
        category.setDescription(rs.getString("description"));
        category.setParentCategoryId(rs.getObject("parent_category_id", Integer.class));
        category.setParentCategoryName(rs.getString("parent_category_name"));
        category.setStatus(rs.getString("status"));
        category.setCreatedBy(rs.getObject("created_by", Integer.class));
        category.setCreatedByName(rs.getString("created_by_name"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            category.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            category.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return category;
    }
}
