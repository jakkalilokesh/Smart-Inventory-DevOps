package com.smartinventory.util;

/**
 * Application constants used throughout the application.
 * Centralizes all constant values for maintainability.
 */
public class AppConstants {
    
    // Application
    public static final String APP_NAME = "SmartInventory";
    public static final String APP_VERSION = "1.0.0";
    
    // Session attributes
    public static final String SESSION_USER = "user";
    public static final String SESSION_USER_ID = "userId";
    public static final String SESSION_USERNAME = "username";
    public static final String SESSION_ROLE = "role";
    public static final String SESSION_ROLE_ID = "roleId";
    
    // Request attributes
    public static final String REQUEST_MESSAGE = "message";
    public static final String REQUEST_ERROR = "error";
    public static final String REQUEST_SUCCESS = "success";
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    
    // File upload
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif"};
    public static final String UPLOAD_DIR = "uploads";
    
    // User roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_STAFF = "STAFF";
    
    // User status
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_LOCKED = "LOCKED";
    public static final String STATUS_DISCONTINUED = "DISCONTINUED";
    
    // Transaction types
    public static final String TRANSACTION_STOCK_IN = "STOCK_IN";
    public static final String TRANSACTION_STOCK_OUT = "STOCK_OUT";
    public static final String TRANSACTION_ADJUSTMENT = "ADJUSTMENT";
    public static final String TRANSACTION_TRANSFER = "TRANSFER";
    
    // Activity actions
    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_LOGOUT = "LOGOUT";
    public static final String ACTION_CREATE = "CREATE";
    public static final String ACTION_UPDATE = "UPDATE";
    public static final String ACTION_DELETE = "DELETE";
    public static final String ACTION_VIEW = "VIEW";
    
    // Modules
    public static final String MODULE_AUTHENTICATION = "AUTHENTICATION";
    public static final String MODULE_PRODUCT = "PRODUCT";
    public static final String MODULE_CATEGORY = "CATEGORY";
    public static final String MODULE_SUPPLIER = "SUPPLIER";
    public static final String MODULE_INVENTORY = "INVENTORY";
    public static final String MODULE_REPORT = "REPORT";
    public static final String MODULE_USER = "USER";
    
    // Date formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DISPLAY_DATE_FORMAT = "MMM dd, yyyy";
    public static final String DISPLAY_DATETIME_FORMAT = "MMM dd, yyyy HH:mm";
    
    // Error messages
    public static final String ERROR_INVALID_INPUT = "Invalid input provided";
    public static final String ERROR_DUPLICATE_ENTRY = "Duplicate entry detected";
    public static final String ERROR_NOT_FOUND = "Resource not found";
    public static final String ERROR_UNAUTHORIZED = "Unauthorized access";
    public static final String ERROR_FORBIDDEN = "Access forbidden";
    public static final String ERROR_SERVER = "Internal server error";
    
    // Success messages
    public static final String SUCCESS_CREATED = "Record created successfully";
    public static final String SUCCESS_UPDATED = "Record updated successfully";
    public static final String SUCCESS_DELETED = "Record deleted successfully";
    public static final String SUCCESS_LOGIN = "Login successful";
    public static final String SUCCESS_LOGOUT = "Logout successful";
    
    // Private constructor to prevent instantiation
    private AppConstants() {
        throw new AssertionError("Cannot instantiate utility class");
    }
}
