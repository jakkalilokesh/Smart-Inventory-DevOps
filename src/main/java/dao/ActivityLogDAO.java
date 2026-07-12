package com.smartinventory.dao;

import com.smartinventory.model.ActivityLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Access Object interface for ActivityLog entity.
 * Defines all database operations for activity log management.
 */
public interface ActivityLogDAO {
    
    /**
     * Finds an activity log by ID.
     * 
     * @param logId the log ID
     * @return the ActivityLog object if found, null otherwise
     */
    ActivityLog findById(int logId);
    
    /**
     * Retrieves all activity logs.
     * 
     * @return list of all activity logs
     */
    List<ActivityLog> findAll();
    
    /**
     * Retrieves activity logs for a specific user.
     * 
     * @param userId the user ID
     * @return list of activity logs for the user
     */
    List<ActivityLog> findByUser(int userId);
    
    /**
     * Retrieves activity logs by action type.
     * 
     * @param action the action type (LOGIN, CREATE, UPDATE, etc.)
     * @return list of activity logs with the specified action
     */
    List<ActivityLog> findByAction(String action);
    
    /**
     * Retrieves activity logs by module.
     * 
     * @param module the module name (PRODUCT, CATEGORY, etc.)
     * @return list of activity logs for the module
     */
    List<ActivityLog> findByModule(String module);
    
    /**
     * Retrieves activity logs within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of activity logs within the date range
     */
    List<ActivityLog> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Retrieves recent activity logs.
     * 
     * @param limit the maximum number of logs to return
     * @return list of recent activity logs
     */
    List<ActivityLog> findRecent(int limit);
    
    /**
     * Creates a new activity log.
     * 
     * @param activityLog the activity log to create
     * @return the generated log ID
     */
    int create(ActivityLog activityLog);
    
    /**
     * Counts total number of activity logs.
     * 
     * @return total count
     */
    int count();
    
    /**
     * Deletes activity logs older than specified date.
     * 
     * @param beforeDate the cutoff date
     * @return number of logs deleted
     */
    int deleteOlderThan(LocalDateTime beforeDate);

    /**
     * Retrieves activity logs with pagination and search criteria.
     * 
     * @param criteria the search criteria
     * @return pagination DTO of activity logs
     */
    com.smartinventory.dto.PaginationDTO<ActivityLog> findWithPagination(com.smartinventory.dto.SearchCriteria criteria);

    /**
     * Counts activity logs matching the criteria.
     * 
     * @param criteria the search criteria
     * @return matching log count
     */
    int countByCriteria(com.smartinventory.dto.SearchCriteria criteria);
}
