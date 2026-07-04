package com.smartinventory.controller;

import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Role;
import com.smartinventory.model.User;
import com.smartinventory.service.UserService;
import com.smartinventory.service.impl.UserServiceImpl;
import com.smartinventory.util.AppConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet for handling user management operations (Admin only).
 * Maps to /users/* URL pattern.
 */
@WebServlet("/users/*")
public class UserServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(UserServlet.class);
    private UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check if user is authenticated
        if (session == null || session.getAttribute(AppConstants.SESSION_USER) == null) {
            response.sendRedirect(request.getContextPath() + "/auth/");
            return;
        }
        
        // Check if user has admin role
        User currentUser = (User) session.getAttribute(AppConstants.SESSION_USER);
        if (!AppConstants.ROLE_ADMIN.equals(currentUser.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin role required.");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
                handleList(request, response);
            } else if (pathInfo.equals("/create")) {
                handleCreateForm(request, response);
            } else if (pathInfo.startsWith("/edit/")) {
                handleEditForm(request, response, pathInfo);
            } else if (pathInfo.startsWith("/view/")) {
                handleView(request, response, pathInfo);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error handling GET request", e);
            request.setAttribute("error", "An error occurred");
            request.getRequestDispatcher("/WEB-INF/views/users/list.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check if user is authenticated
        if (session == null || session.getAttribute(AppConstants.SESSION_USER) == null) {
            response.sendRedirect(request.getContextPath() + "/auth/");
            return;
        }
        
        // Check if user has admin role
        User currentUser = (User) session.getAttribute(AppConstants.SESSION_USER);
        if (!AppConstants.ROLE_ADMIN.equals(currentUser.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin role required.");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/create")) {
                handleCreate(request, response);
            } else if (pathInfo.startsWith("/update/")) {
                handleUpdate(request, response, pathInfo);
            } else if (pathInfo.startsWith("/delete/")) {
                handleDelete(request, response, pathInfo);
            } else if (pathInfo.startsWith("/reset-password/")) {
                handleResetPassword(request, response, pathInfo);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error handling POST request", e);
            request.setAttribute("error", "An error occurred");
            request.getRequestDispatcher("/WEB-INF/views/users/list.jsp").forward(request, response);
        }
    }

    /**
     * Handles listing users with pagination and search.
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        String role = request.getParameter("role");
        String status = request.getParameter("status");
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");
        
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = pageSizeStr != null ? Integer.parseInt(pageSizeStr) : AppConstants.DEFAULT_PAGE_SIZE;
        
        SearchCriteria criteria = new SearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setRole(role);
        criteria.setStatus(status);
        criteria.setPage(page);
        criteria.setPageSize(pageSize);
        criteria.setSortBy("username");
        criteria.setSortOrder("ASC");
        
        PaginationDTO<User> result = userService.findWithPagination(criteria);
        
        request.setAttribute("users", result);
        request.setAttribute("keyword", keyword);
        request.setAttribute("role", role);
        request.setAttribute("status", status);
        
        request.getRequestDispatcher("/WEB-INF/views/users/list.jsp").forward(request, response);
    }

    /**
     * Handles showing the create user form.
     */
    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.getRequestDispatcher("/WEB-INF/views/users/form.jsp").forward(request, response);
    }

    /**
     * Handles showing the edit user form.
     */
    private void handleEditForm(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int userId = extractId(pathInfo);
        User user = userService.findById(userId);
        
        if (user == null) {
            request.setAttribute("error", "User not found");
            response.sendRedirect(request.getContextPath() + "/users/");
            return;
        }
        
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/users/form.jsp").forward(request, response);
    }

    /**
     * Handles viewing a user.
     */
    private void handleView(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int userId = extractId(pathInfo);
        User user = userService.findById(userId);
        
        if (user == null) {
            request.setAttribute("error", "User not found");
            response.sendRedirect(request.getContextPath() + "/users/");
            return;
        }
        
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/users/view.jsp").forward(request, response);
    }

    /**
     * Handles creating a new user.
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");
        int roleId = Integer.parseInt(request.getParameter("roleId"));
        String status = request.getParameter("status");
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setRoleId(roleId);
        user.setStatus(status);
        
        try {
            User created = userService.create(user);
            request.setAttribute("success", AppConstants.SUCCESS_CREATED);
            response.sendRedirect(request.getContextPath() + "/users/");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/users/form.jsp").forward(request, response);
        }
    }

    /**
     * Handles updating a user.
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int userId = extractId(pathInfo);
        User existing = userService.findById(userId);
        
        if (existing == null) {
            request.setAttribute("error", "User not found");
            response.sendRedirect(request.getContextPath() + "/users/");
            return;
        }
        
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");
        int roleId = Integer.parseInt(request.getParameter("roleId"));
        String status = request.getParameter("status");
        
        existing.setEmail(email);
        existing.setFirstName(firstName);
        existing.setLastName(lastName);
        existing.setPhone(phone);
        existing.setRoleId(roleId);
        existing.setStatus(status);
        
        try {
            boolean updated = userService.update(existing);
            if (updated) {
                request.setAttribute("success", AppConstants.SUCCESS_UPDATED);
            } else {
                request.setAttribute("error", "Failed to update user");
            }
            response.sendRedirect(request.getContextPath() + "/users/");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("user", existing);
            request.getRequestDispatcher("/WEB-INF/views/users/form.jsp").forward(request, response);
        }
    }

    /**
     * Handles deleting a user.
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int userId = extractId(pathInfo);
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute(AppConstants.SESSION_USER);
        
        // Prevent self-deletion
        if (userId == currentUser.getUserId()) {
            request.setAttribute("error", "You cannot delete your own account");
            response.sendRedirect(request.getContextPath() + "/users/");
            return;
        }
        
        boolean deleted = userService.delete(userId);
        
        if (deleted) {
            request.setAttribute("success", AppConstants.SUCCESS_DELETED);
        } else {
            request.setAttribute("error", "Failed to delete user");
        }
        
        response.sendRedirect(request.getContextPath() + "/users/");
    }

    /**
     * Handles resetting user password.
     */
    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int userId = extractId(pathInfo);
        String newPassword = request.getParameter("newPassword");
        
        try {
            boolean reset = userService.resetPassword(userId, newPassword);
            if (reset) {
                request.setAttribute("success", "Password reset successfully");
            } else {
                request.setAttribute("error", "Failed to reset password");
            }
            response.sendRedirect(request.getContextPath() + "/users/");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/users/");
        }
    }

    /**
     * Extracts ID from path info.
     */
    private int extractId(String pathInfo) {
        String[] parts = pathInfo.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }
}
