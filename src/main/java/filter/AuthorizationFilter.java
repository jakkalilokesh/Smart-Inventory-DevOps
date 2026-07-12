package com.smartinventory.filter;

import com.smartinventory.model.User;
import com.smartinventory.util.AppConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter for authorization based on user roles.
 * Restricts access to admin-only routes.
 */
@WebFilter(urlPatterns = {"/users/*", "/reports/*", "/audit-logs/*"})
public class AuthorizationFilter implements Filter {
    private static final Logger logger = LogManager.getLogger(AuthorizationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthorizationFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        HttpSession session = httpRequest.getSession(false);
        
        if (session == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/");
            return;
        }
        
        User user = (User) session.getAttribute(AppConstants.SESSION_USER);
        
        if (user == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/");
            return;
        }
        
        // Check if user has required role
        String path = httpRequest.getRequestURI();
        
        if ((path.contains("/users/") || path.contains("/audit-logs/")) && !AppConstants.ROLE_ADMIN.equals(user.getRoleName())) {
            logger.warn("Unauthorized access attempt by user: {} to: {}", user.getUsername(), path);
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin role required.");
            return;
        }
        
        if (path.contains("/reports/") && 
                !AppConstants.ROLE_ADMIN.equals(user.getRoleName()) && 
                !AppConstants.ROLE_MANAGER.equals(user.getRoleName())) {
            logger.warn("Unauthorized access attempt by user: {} to: {}", user.getUsername(), path);
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin or Manager role required.");
            return;
        }
        
        // User is authorized, continue
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("AuthorizationFilter destroyed");
    }

}
