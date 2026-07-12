package com.smartinventory.controller;

import com.smartinventory.dto.LoginDTO;
import com.smartinventory.model.User;
import com.smartinventory.service.ActivityLogService;
import com.smartinventory.service.UserService;
import com.smartinventory.service.impl.ActivityLogServiceImpl;
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
 * Servlet for handling authentication operations (login/logout).
 * Maps to /auth/* URL pattern.
 */
@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(AuthServlet.class);
    private UserService userService = new UserServiceImpl();
    private ActivityLogService activityLogService = new ActivityLogServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Show login page
            request.getRequestDispatcher("/WEB-INF/views/login/login.jsp").forward(request, response);
        } else if (pathInfo.equals("/register")) {
            // Show register page
            request.getRequestDispatcher("/WEB-INF/views/login/register.jsp").forward(request, response);
        } else if (pathInfo.equals("/logout")) {
            handleLogout(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/login")) {
            handleLogin(request, response);
        } else if (pathInfo.equals("/register")) {
            handleRegister(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Handles user registration.
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");
        
        logger.info("Registration attempt for username: {}, email: {}", username, email);
        
        try {
            // Check if username already exists
            if (userService.findByUsername(username) != null) {
                request.setAttribute("error", "Username already exists");
                request.getRequestDispatcher("/WEB-INF/views/login/register.jsp").forward(request, response);
                return;
            }
            
            // Create user object
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhone(phone);
            user.setRoleId(3); // Default role is STAFF
            user.setStatus("ACTIVE");
            
            User created = userService.create(user);
            if (created != null) {
                request.setAttribute("success", "Registration successful! Please login.");
                request.getRequestDispatcher("/WEB-INF/views/login/login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Failed to register user. Please try again.");
                request.getRequestDispatcher("/WEB-INF/views/login/register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            logger.error("Error during registration", e);
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login/register.jsp").forward(request, response);
        }
    }

    /**
     * Handles user login.
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");
        
        logger.info("Login attempt for user: {}", username);
        
        try {
            // Authenticate user
            User user = userService.authenticate(username, password);
            
            if (user != null) {
                // Check if user is active
                if (!AppConstants.STATUS_ACTIVE.equals(user.getStatus())) {
                    request.setAttribute("error", "Your account is " + user.getStatus().toLowerCase() + ". Please contact administrator.");
                    request.getRequestDispatcher("/WEB-INF/views/login/login.jsp").forward(request, response);
                    return;
                }
                
                // Create session
                HttpSession session = request.getSession();
                session.setAttribute(AppConstants.SESSION_USER, user);
                session.setAttribute(AppConstants.SESSION_USER_ID, user.getUserId());
                session.setAttribute(AppConstants.SESSION_USERNAME, user.getUsername());
                session.setAttribute(AppConstants.SESSION_ROLE, user.getRoleName());
                session.setAttribute(AppConstants.SESSION_ROLE_ID, user.getRoleId());
                
                // Set session timeout (30 minutes)
                session.setMaxInactiveInterval(30 * 60);
                
                // Log activity
                ActivityLogServiceImpl logService = (ActivityLogServiceImpl) activityLogService;
                String ipAddress = logService.getClientIpAddress(request);
                String userAgent = logService.getUserAgent(request);
                activityLogService.logActivity(user.getUserId(), AppConstants.ACTION_LOGIN, 
                    AppConstants.MODULE_AUTHENTICATION, "User logged in", ipAddress, userAgent);
                
                logger.info("User logged in successfully: {}", username);
                
                // Redirect to dashboard
                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                // Authentication failed
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("/WEB-INF/views/login/login.jsp").forward(request, response);
                logger.warn("Login failed for user: {}", username);
            }
        } catch (Exception e) {
            logger.error("Error during login", e);
            request.setAttribute("error", "An error occurred during login. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/login/login.jsp").forward(request, response);
        }
    }

    /**
     * Handles user logout.
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            User user = (User) session.getAttribute(AppConstants.SESSION_USER);
            
            // Log activity
            if (user != null) {
                ActivityLogServiceImpl logService = (ActivityLogServiceImpl) activityLogService;
                String ipAddress = logService.getClientIpAddress(request);
                String userAgent = logService.getUserAgent(request);
                activityLogService.logActivity(user.getUserId(), AppConstants.ACTION_LOGOUT, 
                    AppConstants.MODULE_AUTHENTICATION, "User logged out", ipAddress, userAgent);
                logger.info("User logged out: {}", user.getUsername());
            }
            
            // Invalidate session
            session.invalidate();
        }
        
        // Redirect to login page
        response.sendRedirect(request.getContextPath() + "/auth/");
    }
}
