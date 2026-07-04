package com.smartinventory.dao.impl;

import com.smartinventory.dao.InventoryTransactionDAO;
import com.smartinventory.model.InventoryTransaction;
import com.smartinventory.util.DatabaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of InventoryTransactionDAO interface.
 * Handles all database operations for inventory transaction management using JDBC.
 */
public class InventoryTransactionDAOImpl implements InventoryTransactionDAO {
    private static final Logger logger = LogManager.getLogger(InventoryTransactionDAOImpl.class);

    @Override
    public InventoryTransaction findById(int transactionId) {
        String sql = "SELECT it.*, p.product_name, p.sku, u.username as performed_by_name " +
                     "FROM inventory_transactions it " +
                     "JOIN products p ON it.product_id = p.product_id " +
                     "JOIN users u ON it.performed_by = u.user_id " +
                     "WHERE it.transaction_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapRowToTransaction(rs);
            }
        } catch (SQLException e) {
            logger.error("Error finding transaction by ID: {}", transactionId, e);
        }
        return null;
    }

    @Override
    public List<InventoryTransaction> findAll() {
        List<InventoryTransaction> transactions = new ArrayList<>();
        String sql = "SELECT it.*, p.product_name, p.sku, u.username as performed_by_name " +
                     "FROM inventory_transactions it " +
                     "JOIN products p ON it.product_id = p.product_id " +
                     "JOIN users u ON it.performed_by = u.user_id " +
                     "ORDER BY it.transaction_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all transactions", e);
        }
        return transactions;
    }

    @Override
    public List<InventoryTransaction> findByProduct(int productId) {
        List<InventoryTransaction> transactions = new ArrayList<>();
        String sql = "SELECT it.*, p.product_name, p.sku, u.username as performed_by_name " +
                     "FROM inventory_transactions it " +
                     "JOIN products p ON it.product_id = p.product_id " +
                     "JOIN users u ON it.performed_by = u.user_id " +
                     "WHERE it.product_id = ? " +
                     "ORDER BY it.transaction_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding transactions by product: {}", productId, e);
        }
        return transactions;
    }

    @Override
    public List<InventoryTransaction> findByType(String transactionType) {
        List<InventoryTransaction> transactions = new ArrayList<>();
        String sql = "SELECT it.*, p.product_name, p.sku, u.username as performed_by_name " +
                     "FROM inventory_transactions it " +
                     "JOIN products p ON it.product_id = p.product_id " +
                     "JOIN users u ON it.performed_by = u.user_id " +
                     "WHERE it.transaction_type = ? " +
                     "ORDER BY it.transaction_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, transactionType);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding transactions by type: {}", transactionType, e);
        }
        return transactions;
    }

    @Override
    public List<InventoryTransaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<InventoryTransaction> transactions = new ArrayList<>();
        String sql = "SELECT it.*, p.product_name, p.sku, u.username as performed_by_name " +
                     "FROM inventory_transactions it " +
                     "JOIN products p ON it.product_id = p.product_id " +
                     "JOIN users u ON it.performed_by = u.user_id " +
                     "WHERE it.transaction_date BETWEEN ? AND ? " +
                     "ORDER BY it.transaction_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding transactions by date range", e);
        }
        return transactions;
    }

    @Override
    public List<InventoryTransaction> findRecent(int limit) {
        List<InventoryTransaction> transactions = new ArrayList<>();
        String sql = "SELECT it.*, p.product_name, p.sku, u.username as performed_by_name " +
                     "FROM inventory_transactions it " +
                     "JOIN products p ON it.product_id = p.product_id " +
                     "JOIN users u ON it.performed_by = u.user_id " +
                     "ORDER BY it.transaction_date DESC " +
                     "LIMIT ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding recent transactions", e);
        }
        return transactions;
    }

    @Override
    public int create(InventoryTransaction transaction) {
        String sql = "INSERT INTO inventory_transactions " +
                     "(transaction_type, product_id, quantity, previous_quantity, new_quantity, " +
                     "unit_price, total_price, reference_number, notes, performed_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, transaction.getTransactionType());
            stmt.setInt(2, transaction.getProductId());
            stmt.setInt(3, transaction.getQuantity());
            stmt.setInt(4, transaction.getPreviousQuantity());
            stmt.setInt(5, transaction.getNewQuantity());
            stmt.setObject(6, transaction.getUnitPrice());
            stmt.setObject(7, transaction.getTotalPrice());
            stmt.setString(8, transaction.getReferenceNumber());
            stmt.setString(9, transaction.getNotes());
            stmt.setInt(10, transaction.getPerformedBy());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int transactionId = rs.getInt(1);
                        logger.info("Transaction created successfully: {}", transactionId);
                        return transactionId;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating transaction", e);
        }
        return -1;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM inventory_transactions";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting transactions", e);
        }
        return 0;
    }

    @Override
    public int countByType(String transactionType) {
        String sql = "SELECT COUNT(*) FROM inventory_transactions WHERE transaction_type = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, transactionType);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting transactions by type: {}", transactionType, e);
        }
        return 0;
    }

    /**
     * Maps a ResultSet row to an InventoryTransaction object.
     */
    private InventoryTransaction mapRowToTransaction(ResultSet rs) throws SQLException {
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setProductId(rs.getInt("product_id"));
        transaction.setProductName(rs.getString("product_name"));
        transaction.setSku(rs.getString("sku"));
        transaction.setQuantity(rs.getInt("quantity"));
        transaction.setPreviousQuantity(rs.getInt("previous_quantity"));
        transaction.setNewQuantity(rs.getInt("new_quantity"));
        transaction.setUnitPrice(rs.getBigDecimal("unit_price"));
        transaction.setTotalPrice(rs.getBigDecimal("total_price"));
        transaction.setReferenceNumber(rs.getString("reference_number"));
        transaction.setNotes(rs.getString("notes"));
        transaction.setPerformedBy(rs.getInt("performed_by"));
        transaction.setPerformedByName(rs.getString("performed_by_name"));
        
        Timestamp transactionDate = rs.getTimestamp("transaction_date");
        if (transactionDate != null) {
            transaction.setTransactionDate(transactionDate.toLocalDateTime());
        }
        
        return transaction;
    }
}
