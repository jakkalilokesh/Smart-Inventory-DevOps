package com.smartinventory.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class NotFoundException extends AppException {
    
    public NotFoundException(String message) {
        super("NOT_FOUND", message);
    }
    
    public NotFoundException(String message, Throwable cause) {
        super("NOT_FOUND", message, cause);
    }
}
