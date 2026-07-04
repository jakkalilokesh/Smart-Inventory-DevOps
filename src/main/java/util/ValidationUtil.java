package com.smartinventory.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Validation utility class for input validation.
 * Provides methods to validate various types of input data.
 * Follows validation best practices to prevent security issues.
 */
public class ValidationUtil {
    private static final Logger logger = LogManager.getLogger(ValidationUtil.class);
    private static final EmailValidator emailValidator = EmailValidator.getInstance();
    
    // Regular expressions for validation
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,20}$");
    private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z0-9-]{3,50}$");
    private static final Pattern BARCODE_PATTERN = Pattern.compile("^[0-9]{8,20}$");

    /**
     * Validates if a string is not null or empty.
     * 
     * @param value the string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isNotEmpty(String value) {
        return StringUtils.isNotBlank(value);
    }

    /**
     * Validates if a string is within specified length bounds.
     * 
     * @param value the string to validate
     * @param minLength minimum length
     * @param maxLength maximum length
     * @return true if valid, false otherwise
     */
    public static boolean isValidLength(String value, int minLength, int maxLength) {
        if (value == null) {
            return false;
        }
        int length = value.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validates an email address.
     * 
     * @param email the email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return emailValidator.isValid(email);
    }

    /**
     * Validates a username.
     * Username must be 3-50 characters and contain only alphanumeric characters and underscores.
     * 
     * @param username the username to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Validates a phone number.
     * Phone number must be 10-20 digits, optionally starting with +.
     * 
     * @param phone the phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validates a SKU (Stock Keeping Unit).
     * SKU must be 3-50 characters, uppercase alphanumeric with hyphens allowed.
     * 
     * @param sku the SKU to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidSku(String sku) {
        if (sku == null || sku.isEmpty()) {
            return false;
        }
        return SKU_PATTERN.matcher(sku).matches();
    }

    /**
     * Validates a barcode.
     * Barcode must be 8-20 digits.
     * 
     * @param barcode the barcode to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidBarcode(String barcode) {
        if (barcode == null || barcode.isEmpty()) {
            return true; // Barcode is optional
        }
        return BARCODE_PATTERN.matcher(barcode).matches();
    }

    /**
     * Validates if a number is positive.
     * 
     * @param value the number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isPositive(Integer value) {
        return value != null && value > 0;
    }

    /**
     * Validates if a number is non-negative.
     * 
     * @param value the number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isNonNegative(Integer value) {
        return value != null && value >= 0;
    }

    /**
     * Validates if a BigDecimal is positive.
     * 
     * @param value the BigDecimal to validate
     * @return true if valid, false otherwise
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Validates if a BigDecimal is non-negative.
     * 
     * @param value the BigDecimal to validate
     * @return true if valid, false otherwise
     */
    public static boolean isNonNegative(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Sanitizes input to prevent XSS attacks.
     * Removes potentially dangerous HTML/JavaScript characters.
     * 
     * @param input the input to sanitize
     * @return the sanitized input
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("<", "&lt;")
                     .replaceAll(">", "&gt;")
                     .replaceAll("\"", "&quot;")
                     .replaceAll("'", "&#x27;")
                     .replaceAll("/", "&#x2F;");
    }

    /**
     * Validates if a string contains only safe characters.
     * Prevents SQL injection by checking for dangerous patterns.
     * 
     * @param input the input to validate
     * @return true if safe, false otherwise
     */
    public static boolean isSafeInput(String input) {
        if (input == null) {
            return true;
        }
        // Check for common SQL injection patterns
        String[] dangerousPatterns = {
            "' OR '",
            "' OR 1=1",
            "DROP TABLE",
            "DELETE FROM",
            "INSERT INTO",
            "UPDATE",
            "--",
            "/*",
            "*/",
            "xp_",
            "exec(",
            "eval(",
            "script:"
        };
        
        String lowerInput = input.toLowerCase();
        for (String pattern : dangerousPatterns) {
            if (lowerInput.contains(pattern.toLowerCase())) {
                logger.warn("Potentially dangerous input detected: {}", pattern);
                return false;
            }
        }
        return true;
    }

    /**
     * Validates product data.
     * 
     * @param sku the product SKU
     * @param productName the product name
     * @param buyingPrice the buying price
     * @param sellingPrice the selling price
     * @param stockQuantity the stock quantity
     * @return true if all fields are valid, false otherwise
     */
    public static boolean isValidProduct(String sku, String productName, 
                                         BigDecimal buyingPrice, BigDecimal sellingPrice, 
                                         Integer stockQuantity) {
        return isValidSku(sku) &&
               isValidLength(productName, 3, 200) &&
               isPositive(buyingPrice) &&
               isPositive(sellingPrice) &&
               isNonNegative(stockQuantity);
    }

    /**
     * Validates category data.
     * 
     * @param categoryName the category name
     * @return true if valid, false otherwise
     */
    public static boolean isValidCategory(String categoryName) {
        return isValidLength(categoryName, 3, 100);
    }

    /**
     * Validates supplier data.
     * 
     * @param supplierName the supplier name
     * @param email the supplier email
     * @param phone the supplier phone
     * @return true if all fields are valid, false otherwise
     */
    public static boolean isValidSupplier(String supplierName, String email, String phone) {
        boolean validName = isValidLength(supplierName, 3, 100);
        boolean validEmail = email == null || email.isEmpty() || isValidEmail(email);
        boolean validPhone = phone == null || phone.isEmpty() || isValidPhone(phone);
        return validName && validEmail && validPhone;
    }

    /**
     * Validates if a password is strong.
     */
    public static boolean isStrongPassword(String password) {
        return PasswordUtil.isPasswordStrong(password);
    }
}
