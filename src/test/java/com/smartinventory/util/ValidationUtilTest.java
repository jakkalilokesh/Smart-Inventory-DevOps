package com.smartinventory.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationUtil class.
 */
public class ValidationUtilTest {

    @Test
    public void testIsValidEmail_ValidEmail_ReturnsTrue() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name+tag@domain.co.uk"));
    }

    @Test
    public void testIsValidEmail_InvalidEmail_ReturnsFalse() {
        assertFalse(ValidationUtil.isValidEmail("invalid"));
        assertFalse(ValidationUtil.isValidEmail("@example.com"));
        assertFalse(ValidationUtil.isValidEmail("test@"));
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    public void testIsValidPhone_ValidPhone_ReturnsTrue() {
        assertTrue(ValidationUtil.isValidPhone("1234567890"));
        assertTrue(ValidationUtil.isValidPhone("+1234567890"));
        assertTrue(ValidationUtil.isValidPhone("+12345678901"));
    }

    @Test
    public void testIsValidPhone_InvalidPhone_ReturnsFalse() {
        assertFalse(ValidationUtil.isValidPhone("123"));
        assertFalse(ValidationUtil.isValidPhone(""));
        assertFalse(ValidationUtil.isValidPhone(null));
    }

    @Test
    public void testSanitizeInput_RemovesHtmlTags() {
        String input = "<script>alert('xss')</script>Hello";
        String result = ValidationUtil.sanitizeInput(input);
        assertFalse(result.contains("<script>"));
        assertTrue(result.contains("Hello"));
    }

    @Test
    public void testSanitizeInput_NullInput_ReturnsNull() {
        assertNull(ValidationUtil.sanitizeInput(null));
    }

    @Test
    public void testIsValidUsername_ValidUsername_ReturnsTrue() {
        assertTrue(ValidationUtil.isValidUsername("user123"));
        assertTrue(ValidationUtil.isValidUsername("admin"));
    }

    @Test
    public void testIsValidUsername_InvalidUsername_ReturnsFalse() {
        assertFalse(ValidationUtil.isValidUsername(""));
        assertFalse(ValidationUtil.isValidUsername(null));
        assertFalse(ValidationUtil.isValidUsername("us"));
        assertFalse(ValidationUtil.isValidUsername("user@123"));
    }

    @Test
    public void testIsStrongPassword_StrongPassword_ReturnsTrue() {
        assertTrue(ValidationUtil.isStrongPassword("StrongPass123!"));
        assertTrue(ValidationUtil.isStrongPassword("MyP@ssw0rd"));
    }

    @Test
    public void testIsStrongPassword_WeakPassword_ReturnsFalse() {
        assertFalse(ValidationUtil.isStrongPassword("weak"));
        assertFalse(ValidationUtil.isStrongPassword("password"));
        assertFalse(ValidationUtil.isStrongPassword("12345678"));
        assertFalse(ValidationUtil.isStrongPassword(""));
        assertFalse(ValidationUtil.isStrongPassword(null));
    }
}
