package com.smartinventory.exception;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends AppException {
    
    public AuthenticationException(String message) {
        super("AUTH_ERROR", message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super("AUTH_ERROR", message, cause);
    }
}
