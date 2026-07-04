package com.smartinventory.service.impl;

import com.smartinventory.dao.ActivityLogDAO;
import com.smartinventory.dao.impl.ActivityLogDAOImpl;
import com.smartinventory.model.ActivityLog;
import com.smartinventory.service.ActivityLogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of ActivityLogService interface.
 * Contains business logic for activity logging and audit trail.
 */
public class ActivityLogServiceImpl implements ActivityLogService {
    private static final Logger logger = LogManager.getLogger(ActivityLogServiceImpl.class);
    private ActivityLogDAO activityLogDAO = new ActivityLogDAOImpl();

    @Override
    public int logActivity(Integer userId, String action, String module, String description) {
        return logActivity(userId, action, module, description, null, null);
    }

    @Override
    public int logActivity(Integer userId, String action, String module, String description, 
                          String ipAddress, String userAgent) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setModule(module);
        log.setDescription(description);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        
        int logId = activityLogDAO.create(log);
        if (logId > 0) {
            logger.debug("Activity logged: {} - {}", action, description);
        }
        
        return logId;
    }

    @Override
    public List<ActivityLog> getRecentActivities(int limit) {
        return activityLogDAO.findRecent(limit);
    }

    @Override
    public List<ActivityLog> getActivitiesByUser(int userId) {
        return activityLogDAO.findByUser(userId);
    }

    @Override
    public List<ActivityLog> getActivitiesByAction(String action) {
        return activityLogDAO.findByAction(action);
    }

    @Override
    public List<ActivityLog> getActivitiesByModule(String module) {
        return activityLogDAO.findByModule(module);
    }

    @Override
    public List<ActivityLog> getActivitiesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return activityLogDAO.findByDateRange(startDate, endDate);
    }

    @Override
    public int cleanupOldLogs(int daysToKeep) {
        if (daysToKeep < 30) {
            logger.warn("Cannot delete logs less than 30 days old");
            return 0;
        }
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        int deletedCount = activityLogDAO.deleteOlderThan(cutoffDate);
        
        logger.info("Cleaned up {} old activity logs", deletedCount);
        return deletedCount;
    }
    
    /**
     * Helper method to extract IP address from HttpServletRequest.
     */
    public String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        
        // Handle multiple IPs in X-Forwarded-For
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        
        return ipAddress;
    }
    
    /**
     * Helper method to extract user agent from HttpServletRequest.
     */
    public String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
