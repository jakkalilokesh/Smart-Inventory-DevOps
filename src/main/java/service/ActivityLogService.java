package com.smartinventory.service;

import com.smartinventory.model.ActivityLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Activity Log operations.
 * Contains business logic for activity logging and audit trail.
 */
public interface ActivityLogService {
    
    /**
     * Logs an activity.
     * 
     * @param userId the user ID (can be null for system activities)
     * @param action the action performed
     * @param module the module where action occurred
     * @param description the activity description
     * @return the generated log ID
     */
    int logActivity(Integer userId, String action, String module, String description);
    
    /**
     * Logs an activity with IP address and user agent.
     * 
     * @param userId the user ID (can be null for system activities)
     * @param action the action performed
     * @param module the module where action occurred
     * @param description the activity description
     * @param ipAddress the IP address of the user
     * @param userAgent the user agent string
     * @return the generated log ID
     */
    int logActivity(Integer userId, String action, String module, String description, 
                    String ipAddress, String userAgent);
    
    /**
     * Retrieves recent activity logs.
     * 
     * @param limit the maximum number of logs to return
     * @return list of recent activity logs
     */
    List<ActivityLog> getRecentActivities(int limit);
    
    /**
     * Retrieves activity logs for a specific user.
     * 
     * @param userId the user ID
     * @return list of activity logs for the user
     */
    List<ActivityLog> getActivitiesByUser(int userId);
    
    /**
     * Retrieves activity logs by action type.
     * 
     * @param action the action type
     * @return list of activity logs with the specified action
     */
    List<ActivityLog> getActivitiesByAction(String action);
    
    /**
     * Retrieves activity logs by module.
     * 
     * @param module the module name
     * @return list of activity logs for the module
     */
    List<ActivityLog> getActivitiesByModule(String module);
    
    /**
     * Retrieves activity logs within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of activity logs within the date range
     */
    List<ActivityLog> getActivitiesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Cleans up old activity logs.
     * Deletes logs older than the specified date to maintain database performance.
     * 
     * @param daysToKeep number of days of logs to keep
     * @return number of logs deleted
     */
    int cleanupOldLogs(int daysToKeep);

    /**
     * Retrieves activity logs with pagination and search criteria.
     * 
     * @param criteria the search criteria
     * @return pagination DTO of activity logs
     */
    com.smartinventory.dto.PaginationDTO<ActivityLog> findWithPagination(com.smartinventory.dto.SearchCriteria criteria);
}
