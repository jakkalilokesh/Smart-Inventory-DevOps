package com.smartinventory.dao.impl;

import com.smartinventory.dao.ProductDAO;
import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Product;
import com.smartinventory.util.DatabaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ProductDAO interface.
 * Handles all database operations for product management using JDBC.
 */
public class ProductDAOImpl implements ProductDAO {
    private static final Logger logger = LogManager.getLogger(ProductDAOImpl.class);

    @Override
    public Product findById(int productId) {
        String sql = "SELECT p.*, c.category_name, s.supplier_name, u.username as created_by_name " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                     "LEFT JOIN users u ON p.created_by = u.user_id " +
                     "WHERE p.product_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapRowToProduct(rs);
            }
        } catch (SQLException e) {
            logger.error("Error finding product by ID: {}", productId, e);
        }
        return null;
    }

    @Override
    public Product findBySku(String sku) {
        String sql = "SELECT p.*, c.category_name, s.supplier_name, u.username as created_by_name " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                     "LEFT JOIN users u ON p.created_by = u.user_id " +
                     "WHERE p.sku = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sku);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapRowToProduct(rs);
            }
        } catch (SQLException e) {
            logger.error("Error finding product by SKU: {}", sku, e);
        }
        return null;
    }

    @Override
    public Product findByBarcode(String barcode) {
        String sql = "SELECT p.*, c.category_name, s.supplier_name, u.username as created_by_name " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                     "LEFT JOIN users u ON p.created_by = u.user_id " +
                     "WHERE p.barcode = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, barcode);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapRowToProduct(rs);
            }
        } catch (SQLException e) {
            logger.error("Error finding product by barcode: {}", barcode, e);
        }
        return null;
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, s.supplier_name, u.username as created_by_name " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                     "LEFT JOIN users u ON p.created_by = u.user_id " +
                     "ORDER BY p.product_name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all products", e);
        }
        return products;
    }

    @Override
    public List<Product> findActive() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, s.supplier_name, u.username as created_by_name " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                     "LEFT JOIN users u ON p.created_by = u.user_id " +
                     "WHERE p.status = 'ACTIVE' " +
                     "ORDER BY p.product_name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding active products", e);
        }
        return products;
    }

    @Override
    public PaginationDTO<Product> findWithPagination(SearchCriteria criteria) {
        List<Product> products = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, c.category_name, s.supplier_name, u.username as created_by_name " +
            "FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.category_id " +
            "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
            "LEFT JOIN users u ON p.created_by = u.user_id " +
            "WHERE 1=1"
        );
        
        // Add search filter
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            sql.append(" AND (p.product_name LIKE ? OR p.sku LIKE ? OR p.barcode LIKE ?)");
        }
        
        // Add category filter
        if (criteria.getCategory() != null && !criteria.getCategory().isEmpty()) {
            sql.append(" AND c.category_name = ?");
        }
        
        // Add supplier filter
        if (criteria.getSupplier() != null && !criteria.getSupplier().isEmpty()) {
            sql.append(" AND s.supplier_name = ?");
        }
        
        // Add status filter
        if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
            sql.append(" AND p.status = ?");
        }
        
        // Add sorting
        sql.append(" ORDER BY p.").append(criteria.getSortBy()).append(" ").append(criteria.getSortOrder());
        
        // Add pagination
        sql.append(" LIMIT ? OFFSET ?");
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
                String keyword = "%" + criteria.getKeyword() + "%";
                stmt.setString(paramIndex++, keyword);
                stmt.setString(paramIndex++, keyword);
                stmt.setString(paramIndex++, keyword);
            }
            
            if (criteria.getCategory() != null && !criteria.getCategory().isEmpty()) {
                stmt.setString(paramIndex++, criteria.getCategory());
            }
            
            if (criteria.getSupplier() != null && !criteria.getSupplier().isEmpty()) {
                stmt.setString(paramIndex++, criteria.getSupplier());
            }
            
            if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
                stmt.setString(paramIndex++, criteria.getStatus());
            }
            
            stmt.setInt(paramIndex++, criteria.getPageSize());
            stmt.setInt(paramIndex, criteria.getOffset());
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding products with pagination", e);
        }
        
        int totalRecords = countByCriteria(criteria);
        return new PaginationDTO<>(products, criteria.getPage(), criteria.getPageSize(), totalRecords);
    }

    @Override
    public List<Product> search(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, s.supplier_name, u.username as created_by_name " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                     "LEFT JOIN users u ON p.created_by = u.user_id " +
                     "WHERE p.product_name LIKE ? OR p.sku LIKE ? OR p.barcode LIKE ? " +
                     "ORDER BY p.product_name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error searching products: {}", keyword, e);
        }
        return products;
    }

    @Override
    public List<Product> findByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, s.supplier_name, u.username as created_by_name " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                     "LEFT JOIN users u ON p.created_by = u.user_id " +
                     "WHERE p.category_id = ? " +
                     "ORDER BY p.product_name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding products by category: {}", categoryId, e);
        }
        return products;
    }

    @Override
    public List<Product> findBySupplier(int supplierId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, s.supplier_name, u.username as created_by_name " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                     "LEFT JOIN users u ON p.created_by = u.user_id " +
                     "WHERE p.supplier_id = ? " +
                     "ORDER BY p.product_name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, supplierId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding products by supplier: {}", supplierId, e);
        }
        return products;
    }

    @Override
    public List<Product> findLowStock() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, s.supplier_name, u.username as created_by_name " +
                     "FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.category_id " +
                     "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                     "LEFT JOIN users u ON p.created_by = u.user_id " +
                     "WHERE p.stock_quantity <= p.minimum_stock AND p.status = 'ACTIVE' " +
                     "ORDER BY p.stock_quantity ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding low stock products", e);
        }
        return products;
    }

    @Override
    public int create(Product product) {
        String sql = "INSERT INTO products (sku, barcode, product_name, description, category_id, supplier_id, " +
                     "buying_price, selling_price, stock_quantity, minimum_stock, maximum_stock, reorder_level, " +
                     "unit, weight, dimensions, image_path, status, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, product.getSku());
            stmt.setString(2, product.getBarcode());
            stmt.setString(3, product.getProductName());
            stmt.setString(4, product.getDescription());
            stmt.setInt(5, product.getCategoryId());
            stmt.setObject(6, product.getSupplierId());
            stmt.setBigDecimal(7, product.getBuyingPrice());
            stmt.setBigDecimal(8, product.getSellingPrice());
            stmt.setInt(9, product.getStockQuantity());
            stmt.setInt(10, product.getMinimumStock());
            stmt.setObject(11, product.getMaximumStock());
            stmt.setInt(12, product.getReorderLevel());
            stmt.setString(13, product.getUnit());
            stmt.setObject(14, product.getWeight());
            stmt.setString(15, product.getDimensions());
            stmt.setString(16, product.getImagePath());
            stmt.setString(17, product.getStatus());
            stmt.setObject(18, product.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int productId = rs.getInt(1);
                        logger.info("Product created successfully: {}", product.getProductName());
                        return productId;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating product: {}", product.getProductName(), e);
        }
        return -1;
    }

    @Override
    public boolean update(Product product) {
        String sql = "UPDATE products SET sku = ?, barcode = ?, product_name = ?, description = ?, " +
                     "category_id = ?, supplier_id = ?, buying_price = ?, selling_price = ?, " +
                     "stock_quantity = ?, minimum_stock = ?, maximum_stock = ?, reorder_level = ?, " +
                     "unit = ?, weight = ?, dimensions = ?, image_path = ?, status = ? " +
                     "WHERE product_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getSku());
            stmt.setString(2, product.getBarcode());
            stmt.setString(3, product.getProductName());
            stmt.setString(4, product.getDescription());
            stmt.setInt(5, product.getCategoryId());
            stmt.setObject(6, product.getSupplierId());
            stmt.setBigDecimal(7, product.getBuyingPrice());
            stmt.setBigDecimal(8, product.getSellingPrice());
            stmt.setInt(9, product.getStockQuantity());
            stmt.setInt(10, product.getMinimumStock());
            stmt.setObject(11, product.getMaximumStock());
            stmt.setInt(12, product.getReorderLevel());
            stmt.setString(13, product.getUnit());
            stmt.setObject(14, product.getWeight());
            stmt.setString(15, product.getDimensions());
            stmt.setString(16, product.getImagePath());
            stmt.setString(17, product.getStatus());
            stmt.setInt(18, product.getProductId());
            
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                logger.info("Product updated successfully: {}", product.getProductName());
            }
            return success;
        } catch (SQLException e) {
            logger.error("Error updating product: {}", product.getProductName(), e);
        }
        return false;
    }

    @Override
    public boolean updateStock(int productId, int quantity) {
        String sql = "UPDATE products SET stock_quantity = ? WHERE product_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating stock for product: {}", productId, e);
        }
        return false;
    }

    @Override
    public boolean delete(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productId);
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                logger.info("Product deleted successfully: {}", productId);
            }
            return success;
        } catch (SQLException e) {
            logger.error("Error deleting product: {}", productId, e);
        }
        return false;
    }

    @Override
    public boolean skuExists(String sku) {
        String sql = "SELECT COUNT(*) FROM products WHERE sku = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sku);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking SKU existence: {}", sku, e);
        }
        return false;
    }

    @Override
    public boolean barcodeExists(String barcode) {
        String sql = "SELECT COUNT(*) FROM products WHERE barcode = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, barcode);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking barcode existence: {}", barcode, e);
        }
        return false;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM products";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting products", e);
        }
        return 0;
    }

    @Override
    public int countByCriteria(SearchCriteria criteria) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.category_id " +
            "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
            "WHERE 1=1"
        );
        
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            sql.append(" AND (p.product_name LIKE ? OR p.sku LIKE ? OR p.barcode LIKE ?)");
        }
        
        if (criteria.getCategory() != null && !criteria.getCategory().isEmpty()) {
            sql.append(" AND c.category_name = ?");
        }
        
        if (criteria.getSupplier() != null && !criteria.getSupplier().isEmpty()) {
            sql.append(" AND s.supplier_name = ?");
        }
        
        if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
            sql.append(" AND p.status = ?");
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
                String keyword = "%" + criteria.getKeyword() + "%";
                stmt.setString(paramIndex++, keyword);
                stmt.setString(paramIndex++, keyword);
                stmt.setString(paramIndex++, keyword);
            }
            
            if (criteria.getCategory() != null && !criteria.getCategory().isEmpty()) {
                stmt.setString(paramIndex++, criteria.getCategory());
            }
            
            if (criteria.getSupplier() != null && !criteria.getSupplier().isEmpty()) {
                stmt.setString(paramIndex++, criteria.getSupplier());
            }
            
            if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
                stmt.setString(paramIndex, criteria.getStatus());
            }
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting products by criteria", e);
        }
        return 0;
    }

    @Override
    public int countLowStock() {
        String sql = "SELECT COUNT(*) FROM products WHERE stock_quantity <= minimum_stock AND status = 'ACTIVE'";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting low stock products", e);
        }
        return 0;
    }

    /**
     * Maps a ResultSet row to a Product object.
     */
    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setSku(rs.getString("sku"));
        product.setBarcode(rs.getString("barcode"));
        product.setProductName(rs.getString("product_name"));
        product.setDescription(rs.getString("description"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setCategoryName(rs.getString("category_name"));
        product.setSupplierId(rs.getObject("supplier_id", Integer.class));
        product.setSupplierName(rs.getString("supplier_name"));
        product.setBuyingPrice(rs.getBigDecimal("buying_price"));
        product.setSellingPrice(rs.getBigDecimal("selling_price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setMinimumStock(rs.getInt("minimum_stock"));
        product.setMaximumStock(rs.getObject("maximum_stock", Integer.class));
        product.setReorderLevel(rs.getInt("reorder_level"));
        product.setUnit(rs.getString("unit"));
        product.setWeight(rs.getBigDecimal("weight"));
        product.setDimensions(rs.getString("dimensions"));
        product.setImagePath(rs.getString("image_path"));
        product.setStatus(rs.getString("status"));
        product.setCreatedBy(rs.getObject("created_by", Integer.class));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            product.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            product.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return product;
    }
}
