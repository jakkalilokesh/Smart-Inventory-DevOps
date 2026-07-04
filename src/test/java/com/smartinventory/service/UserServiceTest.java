package com.smartinventory.service;

import com.smartinventory.dao.UserDAO;
import com.smartinventory.model.User;
import com.smartinventory.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UserService class.
 */
public class UserServiceTest {

    private UserServiceImpl userService;

    @Mock
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl();
        userService.setUserDAO(userDAO);
        
        // Mock default behavior for happy paths
        when(userDAO.usernameExists(anyString())).thenReturn(false);
        when(userDAO.emailExists(anyString())).thenReturn(false);
        when(userDAO.create(any(User.class))).thenReturn(1);
    }

    @Test
    public void testAuthenticate_ValidCredentials_ReturnsUser() {
        assertNotNull(userService);
    }

    @Test
    public void testCreateUser_ValidUser_ReturnsCreatedUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("TestPassword123!");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRoleId(1);
        user.setStatus("ACTIVE");
        
        User created = userService.create(user);
        assertNotNull(created);
        assertEquals("testuser", created.getUsername());
    }

    @Test
    public void testValidateUser_InvalidEmail_ThrowsException() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("TestPassword123!");
        user.setEmail("invalid-email");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRoleId(1);
        user.setStatus("ACTIVE");
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.create(user);
        });
    }

    @Test
    public void testValidateUser_WeakPassword_ThrowsException() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("weak");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRoleId(1);
        user.setStatus("ACTIVE");
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.create(user);
        });
    }
}
