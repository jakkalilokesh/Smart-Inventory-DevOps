package com.smartinventory.dao.impl;

import com.smartinventory.dao.SupplierDAO;
import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Supplier;
import com.smartinventory.util.DatabaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of SupplierDAO interface.
 * Handles all database operations for supplier management using JDBC.
 */
public class SupplierDAOImpl implements SupplierDAO {
    private static final Logger logger = LogManager.getLogger(SupplierDAOImpl.class);

    @Override
    public Supplier findById(int supplierId) {
        String sql = "SELECT s.*, u.username as created_by_name FROM suppliers s " +
                     "LEFT JOIN users u ON s.created_by = u.user_id " +
                     "WHERE s.supplier_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, supplierId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapRowToSupplier(rs);
            }
        } catch (SQLException e) {
            logger.error("Error finding supplier by ID: {}", supplierId, e);
        }
        return null;
    }

    @Override
    public Supplier findByName(String supplierName) {
        String sql = "SELECT s.*, u.username as created_by_name FROM suppliers s " +
                     "LEFT JOIN users u ON s.created_by = u.user_id " +
                     "WHERE s.supplier_name = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, supplierName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapRowToSupplier(rs);
            }
        } catch (SQLException e) {
            logger.error("Error finding supplier by name: {}", supplierName, e);
        }
        return null;
    }

    @Override
    public List<Supplier> findAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT s.*, u.username as created_by_name FROM suppliers s " +
                     "LEFT JOIN users u ON s.created_by = u.user_id " +
                     "ORDER BY s.supplier_name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                suppliers.add(mapRowToSupplier(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all suppliers", e);
        }
        return suppliers;
    }

    @Override
    public List<Supplier> findActive() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT s.*, u.username as created_by_name FROM suppliers s " +
                     "LEFT JOIN users u ON s.created_by = u.user_id " +
                     "WHERE s.status = 'ACTIVE' " +
                     "ORDER BY s.supplier_name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                suppliers.add(mapRowToSupplier(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding active suppliers", e);
        }
        return suppliers;
    }

    @Override
    public PaginationDTO<Supplier> findWithPagination(SearchCriteria criteria) {
        List<Supplier> suppliers = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT s.*, u.username as created_by_name FROM suppliers s " +
            "LEFT JOIN users u ON s.created_by = u.user_id " +
            "WHERE 1=1"
        );
        
        // Add search filter
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            sql.append(" AND (s.supplier_name LIKE ? OR s.contact_person LIKE ? OR s.email LIKE ?)");
        }
        
        // Add status filter
        if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
            sql.append(" AND s.status = ?");
        }
        
        // Add sorting
        sql.append(" ORDER BY s.").append(criteria.getSortBy()).append(" ").append(criteria.getSortOrder());
        
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
            
            if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
                stmt.setString(paramIndex++, criteria.getStatus());
            }
            
            stmt.setInt(paramIndex++, criteria.getPageSize());
            stmt.setInt(paramIndex, criteria.getOffset());
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                suppliers.add(mapRowToSupplier(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding suppliers with pagination", e);
        }
        
        int totalRecords = countByCriteria(criteria);
        return new PaginationDTO<>(suppliers, criteria.getPage(), criteria.getPageSize(), totalRecords);
    }

    @Override
    public List<Supplier> search(String keyword) {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT s.*, u.username as created_by_name FROM suppliers s " +
                     "LEFT JOIN users u ON s.created_by = u.user_id " +
                     "WHERE s.supplier_name LIKE ? OR s.contact_person LIKE ? OR s.email LIKE ? " +
                     "ORDER BY s.supplier_name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                suppliers.add(mapRowToSupplier(rs));
            }
        } catch (SQLException e) {
            logger.error("Error searching suppliers: {}", keyword, e);
        }
        return suppliers;
    }

    @Override
    public int create(Supplier supplier) {
        String sql = "INSERT INTO suppliers (supplier_name, contact_person, email, phone, address, " +
                     "city, state, country, postal_code, tax_id, status, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, supplier.getSupplierName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getEmail());
            stmt.setString(4, supplier.getPhone());
            stmt.setString(5, supplier.getAddress());
            stmt.setString(6, supplier.getCity());
            stmt.setString(7, supplier.getState());
            stmt.setString(8, supplier.getCountry());
            stmt.setString(9, supplier.getPostalCode());
            stmt.setString(10, supplier.getTaxId());
            stmt.setString(11, supplier.getStatus());
            stmt.setObject(12, supplier.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int supplierId = rs.getInt(1);
                        logger.info("Supplier created successfully: {}", supplier.getSupplierName());
                        return supplierId;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating supplier: {}", supplier.getSupplierName(), e);
        }
        return -1;
    }

    @Override
    public boolean update(Supplier supplier) {
        String sql = "UPDATE suppliers SET supplier_name = ?, contact_person = ?, email = ?, " +
                     "phone = ?, address = ?, city = ?, state = ?, country = ?, postal_code = ?, " +
                     "tax_id = ?, status = ? WHERE supplier_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, supplier.getSupplierName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getEmail());
            stmt.setString(4, supplier.getPhone());
            stmt.setString(5, supplier.getAddress());
            stmt.setString(6, supplier.getCity());
            stmt.setString(7, supplier.getState());
            stmt.setString(8, supplier.getCountry());
            stmt.setString(9, supplier.getPostalCode());
            stmt.setString(10, supplier.getTaxId());
            stmt.setString(11, supplier.getStatus());
            stmt.setInt(12, supplier.getSupplierId());
            
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                logger.info("Supplier updated successfully: {}", supplier.getSupplierName());
            }
            return success;
        } catch (SQLException e) {
            logger.error("Error updating supplier: {}", supplier.getSupplierName(), e);
        }
        return false;
    }

    @Override
    public boolean delete(int supplierId) {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, supplierId);
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                logger.info("Supplier deleted successfully: {}", supplierId);
            }
            return success;
        } catch (SQLException e) {
            logger.error("Error deleting supplier: {}", supplierId, e);
        }
        return false;
    }

    @Override
    public boolean supplierExists(String supplierName) {
        String sql = "SELECT COUNT(*) FROM suppliers WHERE supplier_name = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, supplierName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking supplier existence: {}", supplierName, e);
        }
        return false;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM suppliers";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting suppliers", e);
        }
        return 0;
    }

    @Override
    public int countByCriteria(SearchCriteria criteria) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM suppliers WHERE 1=1");
        
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            sql.append(" AND (supplier_name LIKE ? OR contact_person LIKE ? OR email LIKE ?)");
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
            logger.error("Error counting suppliers by criteria", e);
        }
        return 0;
    }

    /**
     * Maps a ResultSet row to a Supplier object.
     */
    private Supplier mapRowToSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(rs.getInt("supplier_id"));
        supplier.setSupplierName(rs.getString("supplier_name"));
        supplier.setContactPerson(rs.getString("contact_person"));
        supplier.setEmail(rs.getString("email"));
        supplier.setPhone(rs.getString("phone"));
        supplier.setAddress(rs.getString("address"));
        supplier.setCity(rs.getString("city"));
        supplier.setState(rs.getString("state"));
        supplier.setCountry(rs.getString("country"));
        supplier.setPostalCode(rs.getString("postal_code"));
        supplier.setTaxId(rs.getString("tax_id"));
        supplier.setStatus(rs.getString("status"));
        supplier.setCreatedBy(rs.getObject("created_by", Integer.class));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            supplier.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            supplier.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return supplier;
    }
}
