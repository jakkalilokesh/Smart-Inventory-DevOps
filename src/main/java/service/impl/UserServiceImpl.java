package com.smartinventory.service.impl;

import com.smartinventory.dao.UserDAO;
import com.smartinventory.dao.impl.UserDAOImpl;
import com.smartinventory.model.User;
import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.service.UserService;
import com.smartinventory.util.PasswordUtil;
import com.smartinventory.util.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Implementation of UserService interface.
 * Contains business logic for user management operations.
 */
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private UserDAO userDAO = new UserDAOImpl();

    // Package-private setter for mock injection in tests
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User authenticate(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            logger.warn("Authentication failed: empty username or password");
            return null;
        }
        
        User user = userDAO.authenticate(username, password);
        
        if (user != null) {
            userDAO.updateLastLogin(user.getUserId());
            logger.info("User authenticated successfully: {}", username);
        }
        
        return user;
    }

    @Override
    public User findById(int userId) {
        return userDAO.findById(userId);
    }

    @Override
    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @Override
    public User create(User user) throws IllegalArgumentException {
        if (!validate(user)) {
            throw new IllegalArgumentException("Invalid user data");
        }
        
        if (userDAO.usernameExists(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        if (userDAO.emailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        int userId = userDAO.create(user);
        if (userId > 0) {
            user.setUserId(userId);
            logger.info("User created successfully: {}", user.getUsername());
            return user;
        }
        
        throw new IllegalArgumentException("Failed to create user");
    }

    @Override
    public boolean update(User user) throws IllegalArgumentException {
        if (!validate(user)) {
            throw new IllegalArgumentException("Invalid user data");
        }
        
        User existingUser = userDAO.findById(user.getUserId());
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Check if username is being changed and if it already exists
        if (!existingUser.getUsername().equals(user.getUsername()) && 
            userDAO.usernameExists(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Check if email is being changed and if it already exists
        if (!existingUser.getEmail().equals(user.getEmail()) && 
            userDAO.emailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        return userDAO.update(user);
    }

    @Override
    public boolean delete(int userId) {
        User user = userDAO.findById(userId);
        if (user == null) {
            logger.warn("Cannot delete non-existent user: {}", userId);
            return false;
        }
        
        // Prevent deletion of the last admin user
        if (user.getRoleName().equals("ADMIN")) {
            List<User> admins = findAll();
            long adminCount = admins.stream().filter(u -> u.getRoleName().equals("ADMIN")).count();
            if (adminCount <= 1) {
                logger.warn("Cannot delete the last admin user");
                return false;
            }
        }
        
        return userDAO.delete(userId);
    }

    @Override
    public boolean changePassword(int userId, String oldPassword, String newPassword) throws IllegalArgumentException {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Verify old password
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Validate new password strength
        if (!PasswordUtil.isPasswordStrong(newPassword)) {
            throw new IllegalArgumentException("New password does not meet strength requirements");
        }
        
        return userDAO.changePassword(userId, newPassword);
    }

    @Override
    public boolean validate(User user) {
        if (user == null) {
            return false;
        }
        
        // Validate username
        if (!ValidationUtil.isValidUsername(user.getUsername())) {
            logger.warn("Invalid username: {}", user.getUsername());
            return false;
        }
        
        // Validate email
        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            logger.warn("Invalid email: {}", user.getEmail());
            return false;
        }
        
        // Validate first name and last name
        if (!ValidationUtil.isNotEmpty(user.getFirstName()) || !ValidationUtil.isNotEmpty(user.getLastName())) {
            logger.warn("Invalid name");
            return false;
        }
        
        // Validate role ID
        if (user.getRoleId() <= 0) {
            logger.warn("Invalid role ID");
            return false;
        }
        
        // Validate status
        if (user.getStatus() == null || 
            (!user.getStatus().equals("ACTIVE") && 
             !user.getStatus().equals("INACTIVE") && 
             !user.getStatus().equals("LOCKED"))) {
            logger.warn("Invalid status: {}", user.getStatus());
            return false;
        }
        
        // Validate password strength if it's a new plain password
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$")) {
            if (!PasswordUtil.isPasswordStrong(user.getPassword())) {
                logger.warn("Password does not meet strength requirements");
                return false;
            }
        }
        
        return true;
    }

    @Override
    public PaginationDTO<User> findWithPagination(SearchCriteria criteria) {
        List<User> all = userDAO.findAll();
        
        // Filter by keyword (username, first name, last name, email)
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            String kw = criteria.getKeyword().toLowerCase();
            all = all.stream().filter(u -> 
                (u.getUsername() != null && u.getUsername().toLowerCase().contains(kw)) ||
                (u.getFirstName() != null && u.getFirstName().toLowerCase().contains(kw)) ||
                (u.getLastName() != null && u.getLastName().toLowerCase().contains(kw)) ||
                (u.getEmail() != null && u.getEmail().toLowerCase().contains(kw))
            ).collect(java.util.stream.Collectors.toList());
        }
        
        // Filter by status
        if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
            String status = criteria.getStatus();
            all = all.stream().filter(u -> status.equals(u.getStatus()))
                     .collect(java.util.stream.Collectors.toList());
        }
        
        // Filter by role (role ID as a string, e.g., "1" for ADMIN, "2" for MANAGER, etc.)
        if (criteria.getRole() != null && !criteria.getRole().isEmpty()) {
            try {
                int roleId = Integer.parseInt(criteria.getRole());
                all = all.stream().filter(u -> u.getRoleId() == roleId)
                         .collect(java.util.stream.Collectors.toList());
            } catch (NumberFormatException ignored) {}
        }
        
        // Sort in memory
        if ("username".equals(criteria.getSortBy())) {
            if ("DESC".equalsIgnoreCase(criteria.getSortOrder())) {
                all.sort((u1, u2) -> {
                    if (u1.getUsername() == null) return 1;
                    if (u2.getUsername() == null) return -1;
                    return u2.getUsername().compareToIgnoreCase(u1.getUsername());
                });
            } else {
                all.sort((u1, u2) -> {
                    if (u1.getUsername() == null) return -1;
                    if (u2.getUsername() == null) return 1;
                    return u1.getUsername().compareToIgnoreCase(u2.getUsername());
                });
            }
        }
        
        int total = all.size();
        int offset = criteria.getOffset();
        int limit = criteria.getPageSize();
        
        List<User> paginatedList;
        if (offset >= total) {
            paginatedList = new java.util.ArrayList<>();
        } else {
            paginatedList = all.subList(offset, Math.min(offset + limit, total));
        }
        
        return new PaginationDTO<>(paginatedList, criteria.getPage(), criteria.getPageSize(), total);
    }

    @Override
    public boolean resetPassword(int userId, String newPassword) {
        if (newPassword == null || !PasswordUtil.isPasswordStrong(newPassword)) {
            throw new IllegalArgumentException("Password does not meet strength requirements");
        }
        return userDAO.changePassword(userId, newPassword);
    }
}
