package com.smartinventory.dto;

/**
 * Generic Data Transfer Object for API responses.
 * Provides a consistent structure for all responses.
 */
public class ResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;
    private String error;

    public ResponseDTO() {
    }

    public ResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseDTO<T> success(String message) {
        return new ResponseDTO<>(true, message);
    }

    public static <T> ResponseDTO<T> success(String message, T data) {
        return new ResponseDTO<>(true, message, data);
    }

    public static <T> ResponseDTO<T> error(String message) {
        return new ResponseDTO<>(false, message);
    }

    public static <T> ResponseDTO<T> error(String message, String error) {
        ResponseDTO<T> response = new ResponseDTO<>(false, message);
        response.setError(error);
        return response;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
