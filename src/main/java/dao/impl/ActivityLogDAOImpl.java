package com.smartinventory.dao.impl;

import com.smartinventory.dao.ActivityLogDAO;
import com.smartinventory.model.ActivityLog;
import com.smartinventory.util.DatabaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ActivityLogDAO interface.
 * Handles all database operations for activity log management using JDBC.
 */
public class ActivityLogDAOImpl implements ActivityLogDAO {
    private static final Logger logger = LogManager.getLogger(ActivityLogDAOImpl.class);

    @Override
    public ActivityLog findById(int logId) {
        String sql = "SELECT al.*, u.username FROM activity_logs al " +
                     "LEFT JOIN users u ON al.user_id = u.user_id " +
                     "WHERE al.log_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, logId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapRowToActivityLog(rs);
            }
        } catch (SQLException e) {
            logger.error("Error finding activity log by ID: {}", logId, e);
        }
        return null;
    }

    @Override
    public List<ActivityLog> findAll() {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT al.*, u.username FROM activity_logs al " +
                     "LEFT JOIN users u ON al.user_id = u.user_id " +
                     "ORDER BY al.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                logs.add(mapRowToActivityLog(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all activity logs", e);
        }
        return logs;
    }

    @Override
    public List<ActivityLog> findByUser(int userId) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT al.*, u.username FROM activity_logs al " +
                     "LEFT JOIN users u ON al.user_id = u.user_id " +
                     "WHERE al.user_id = ? " +
                     "ORDER BY al.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                logs.add(mapRowToActivityLog(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding activity logs by user: {}", userId, e);
        }
        return logs;
    }

    @Override
    public List<ActivityLog> findByAction(String action) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT al.*, u.username FROM activity_logs al " +
                     "LEFT JOIN users u ON al.user_id = u.user_id " +
                     "WHERE al.action = ? " +
                     "ORDER BY al.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, action);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                logs.add(mapRowToActivityLog(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding activity logs by action: {}", action, e);
        }
        return logs;
    }

    @Override
    public List<ActivityLog> findByModule(String module) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT al.*, u.username FROM activity_logs al " +
                     "LEFT JOIN users u ON al.user_id = u.user_id " +
                     "WHERE al.module = ? " +
                     "ORDER BY al.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, module);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                logs.add(mapRowToActivityLog(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding activity logs by module: {}", module, e);
        }
        return logs;
    }

    @Override
    public List<ActivityLog> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT al.*, u.username FROM activity_logs al " +
                     "LEFT JOIN users u ON al.user_id = u.user_id " +
                     "WHERE al.created_at BETWEEN ? AND ? " +
                     "ORDER BY al.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                logs.add(mapRowToActivityLog(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding activity logs by date range", e);
        }
        return logs;
    }

    @Override
    public List<ActivityLog> findRecent(int limit) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT al.*, u.username FROM activity_logs al " +
                     "LEFT JOIN users u ON al.user_id = u.user_id " +
                     "ORDER BY al.created_at DESC " +
                     "LIMIT ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                logs.add(mapRowToActivityLog(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding recent activity logs", e);
        }
        return logs;
    }

    @Override
    public int create(ActivityLog activityLog) {
        String sql = "INSERT INTO activity_logs (user_id, action, module, description, ip_address, user_agent) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setObject(1, activityLog.getUserId());
            stmt.setString(2, activityLog.getAction());
            stmt.setString(3, activityLog.getModule());
            stmt.setString(4, activityLog.getDescription());
            stmt.setString(5, activityLog.getIpAddress());
            stmt.setString(6, activityLog.getUserAgent());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int logId = rs.getInt(1);
                        logger.debug("Activity log created: {}", logId);
                        return logId;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating activity log", e);
        }
        return -1;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM activity_logs";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting activity logs", e);
        }
        return 0;
    }

    @Override
    public int deleteOlderThan(LocalDateTime beforeDate) {
        String sql = "DELETE FROM activity_logs WHERE created_at < ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(beforeDate));
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Deleted {} activity logs older than {}", affectedRows, beforeDate);
            }
            return affectedRows;
        } catch (SQLException e) {
            logger.error("Error deleting old activity logs", e);
        }
        return 0;
    }

    /**
     * Maps a ResultSet row to an ActivityLog object.
     */
    private ActivityLog mapRowToActivityLog(ResultSet rs) throws SQLException {
        ActivityLog log = new ActivityLog();
        log.setLogId(rs.getInt("log_id"));
        log.setUserId(rs.getObject("user_id", Integer.class));
        log.setUsername(rs.getString("username"));
        log.setAction(rs.getString("action"));
        log.setModule(rs.getString("module"));
        log.setDescription(rs.getString("description"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setUserAgent(rs.getString("user_agent"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            log.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return log;
    }
}
