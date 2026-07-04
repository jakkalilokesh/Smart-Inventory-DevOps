package com.smartinventory.controller;

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
 * Servlet for handling user profile operations.
 * Maps to /profile/* URL pattern.
 */
@WebServlet("/profile/*")
public class ProfileServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(ProfileServlet.class);
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
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/view")) {
                handleView(request, response);
            } else if (pathInfo.equals("/edit")) {
                handleEditForm(request, response);
            } else if (pathInfo.equals("/change-password")) {
                handleChangePasswordForm(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error handling GET request", e);
            request.setAttribute("error", "An error occurred");
            request.getRequestDispatcher("/WEB-INF/views/profile/view.jsp").forward(request, response);
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
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/update")) {
                handleUpdate(request, response);
            } else if (pathInfo.equals("/change-password")) {
                handleChangePassword(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error handling POST request", e);
            request.setAttribute("error", "An error occurred");
            request.getRequestDispatcher("/WEB-INF/views/profile/view.jsp").forward(request, response);
        }
    }

    /**
     * Handles viewing user profile.
     */
    private void handleView(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(AppConstants.SESSION_USER);
        
        // Refresh user data from database
        User freshUser = userService.findById(user.getUserId());
        session.setAttribute(AppConstants.SESSION_USER, freshUser);
        
        request.setAttribute("user", freshUser);
        request.getRequestDispatcher("/WEB-INF/views/profile/view.jsp").forward(request, response);
    }

    /**
     * Handles showing the edit profile form.
     */
    private void handleEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(AppConstants.SESSION_USER);
        
        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/profile/edit.jsp").forward(request, response);
    }

    /**
     * Handles showing the change password form.
     */
    private void handleChangePasswordForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.getRequestDispatcher("/WEB-INF/views/profile/change-password.jsp").forward(request, response);
    }

    /**
     * Handles updating user profile.
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(AppConstants.SESSION_USER);
        
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");
        
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        
        try {
            boolean updated = userService.update(user);
            if (updated) {
                // Update session with fresh data
                User freshUser = userService.findById(user.getUserId());
                session.setAttribute(AppConstants.SESSION_USER, freshUser);
                request.setAttribute("success", "Profile updated successfully");
            } else {
                request.setAttribute("error", "Failed to update profile");
            }
            response.sendRedirect(request.getContextPath() + "/profile/");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/profile/edit.jsp").forward(request, response);
        }
    }

    /**
     * Handles changing password.
     */
    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(AppConstants.SESSION_USER);
        
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate passwords match
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "New passwords do not match");
            request.getRequestDispatcher("/WEB-INF/views/profile/change-password.jsp").forward(request, response);
            return;
        }
        
        try {
            boolean changed = userService.changePassword(user.getUserId(), currentPassword, newPassword);
            if (changed) {
                request.setAttribute("success", "Password changed successfully");
                response.sendRedirect(request.getContextPath() + "/profile/");
            } else {
                request.setAttribute("error", "Failed to change password");
                request.getRequestDispatcher("/WEB-INF/views/profile/change-password.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/profile/change-password.jsp").forward(request, response);
        }
    }
}
