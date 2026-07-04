package com.smartinventory.service;

import com.smartinventory.model.User;
import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;

import java.util.List;

/**
 * Service interface for User entity.
 * Contains business logic for user management operations.
 */
public interface UserService {
    
    /**
     * Authenticates a user with username and password.
     * 
     * @param username the username
     * @param password the plain text password
     * @return the authenticated User object, or null if authentication fails
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
     * Retrieves all users.
     * 
     * @return list of all users
     */
    List<User> findAll();
    
    /**
     * Creates a new user.
     * 
     * @param user the user to create
     * @return the created User object with generated ID
     * @throws IllegalArgumentException if validation fails
     */
    User create(User user) throws IllegalArgumentException;
    
    /**
     * Updates an existing user.
     * 
     * @param user the user to update
     * @return true if update successful, false otherwise
     * @throws IllegalArgumentException if validation fails
     */
    boolean update(User user) throws IllegalArgumentException;
    
    /**
     * Deletes a user by ID.
     * 
     * @param userId the user ID
     * @return true if deletion successful, false otherwise
     */
    boolean delete(int userId);
    
    /**
     * Changes user password.
     * 
     * @param userId the user ID
     * @param oldPassword the current password
     * @param newPassword the new password
     * @return true if password change successful, false otherwise
     * @throws IllegalArgumentException if validation fails
     */
    boolean changePassword(int userId, String oldPassword, String newPassword) throws IllegalArgumentException;
    
    /**
     * Validates user data.
     * 
     * @param user the user to validate
     * @return true if valid, false otherwise
     */
    boolean validate(User user);

    /**
     * Finds users with pagination.
     */
    PaginationDTO<User> findWithPagination(SearchCriteria criteria);

    /**
     * Resets user password.
     */
    boolean resetPassword(int userId, String newPassword);
}
