package com.smartinventory.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordUtil class.
 */
public class PasswordUtilTest {

    @Test
    public void testHashPassword_ValidPassword_ReturnsHash() {
        String password = "TestPassword123";
        String hash = PasswordUtil.hashPassword(password);
        
        assertNotNull(hash);
        assertNotEquals(password, hash);
        assertTrue(hash.length() > 50);
    }

    @Test
    public void testHashPassword_SamePassword_DifferentHashes() {
        String password = "TestPassword123";
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);
        
        assertNotEquals(hash1, hash2);
    }

    @Test
    public void testVerifyPassword_CorrectPassword_ReturnsTrue() {
        String password = "TestPassword123";
        String hash = PasswordUtil.hashPassword(password);
        
        assertTrue(PasswordUtil.verifyPassword(password, hash));
    }

    @Test
    public void testVerifyPassword_IncorrectPassword_ReturnsFalse() {
        String password = "TestPassword123";
        String wrongPassword = "WrongPassword123";
        String hash = PasswordUtil.hashPassword(password);
        
        assertFalse(PasswordUtil.verifyPassword(wrongPassword, hash));
    }

    @Test
    public void testVerifyPassword_NullPassword_ReturnsFalse() {
        String hash = PasswordUtil.hashPassword("TestPassword123");
        
        assertFalse(PasswordUtil.verifyPassword(null, hash));
        assertFalse(PasswordUtil.verifyPassword("TestPassword123", null));
    }

    @Test
    public void testGenerateRandomToken_ReturnsUniqueTokens() {
        String token1 = PasswordUtil.generateRandomToken();
        String token2 = PasswordUtil.generateRandomToken();
        
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
        assertTrue(token1.length() > 20);
    }
}
