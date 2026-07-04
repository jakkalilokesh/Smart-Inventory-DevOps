package com.smartinventory.dao;

import com.smartinventory.model.User;

import java.util.List;

/**
 * Data Access Object interface for User entity.
 * Defines all database operations for user management.
 * This interface follows the DAO pattern for separation of concerns.
 */
public interface UserDAO {
    
    /**
     * Authenticates a user by username and password.
     * 
     * @param username the username
     * @param password the plain text password (will be hashed for verification)
     * @return the User object if authentication successful, null otherwise
     */
    User authenticate(String username, String password);
    
    /**
     * Finds a user by ID.
     * 
     * @param userId the user ID
     * @return the User object if found, null otherwise
     */
    User findById(int userId);
    
    /**
     * Finds a user by username.
     * 
     * @param username the username
     * @return the User object if found, null otherwise
     */
    User findByUsername(String username);
    
    /**
     * Finds a user by email.
     * 
     * @param email the email
     * @return the User object if found, null otherwise
     */
    User findByEmail(String email);
    
    /**
     * Retrieves all users.
     * 
     * @return list of all users
     */
    List<User> findAll();
    
    /**
     * Creates a new user.
     * 
     * @param user the user to create
     * @return the generated user ID
     */
    int create(User user);
    
    /**
     * Updates an existing user.
     * 
     * @param user the user to update
     * @return true if update successful, false otherwise
     */
    boolean update(User user);
    
    /**
     * Deletes a user by ID.
     * 
     * @param userId the user ID
     * @return true if deletion successful, false otherwise
     */
    boolean delete(int userId);
    
    /**
     * Updates the last login timestamp for a user.
     * 
     * @param userId the user ID
     * @return true if update successful, false otherwise
     */
    boolean updateLastLogin(int userId);
    
    /**
     * Changes user password.
     * 
     * @param userId the user ID
     * @param newPassword the new plain text password (will be hashed)
     * @return true if password change successful, false otherwise
     */
    boolean changePassword(int userId, String newPassword);
    
    /**
     * Checks if a username already exists.
     * 
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean usernameExists(String username);
    
    /**
     * Checks if an email already exists.
     * 
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean emailExists(String email);
}
