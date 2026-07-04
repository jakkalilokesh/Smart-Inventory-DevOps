package com.smartinventory.exception;

/**
 * Base application exception class.
 * Used for custom application-specific exceptions.
 */
public class AppException extends RuntimeException {
    
    private String errorCode;
    
    public AppException(String message) {
        super(message);
    }
    
    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public AppException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public AppException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
