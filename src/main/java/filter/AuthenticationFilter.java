package com.smartinventory.filter;

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
 * Filter for authentication.
 * Protects all routes except login page and static resources.
 * Redirects unauthenticated users to the login page.
 */
@WebFilter(urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {
    private static final Logger logger = LogManager.getLogger(AuthenticationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthenticationFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String relativePath = path.substring(contextPath.length());
        
        // Allow access to login page, static resources, and public endpoints
        if (isPublicPath(relativePath)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check if user is authenticated
        HttpSession session = httpRequest.getSession(false);
        
        if (session == null || session.getAttribute(AppConstants.SESSION_USER) == null) {
            logger.debug("Unauthenticated access attempt to: {}", relativePath);
            httpResponse.sendRedirect(contextPath + "/auth/");
            return;
        }
        
        // User is authenticated, continue
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("AuthenticationFilter destroyed");
    }

    /**
     * Checks if the path is public (doesn't require authentication).
     */
    private boolean isPublicPath(String path) {
        // Allow login page
        if (path.startsWith("/auth/")) {
            return true;
        }
        
        // Allow static resources (CSS, JS, images)
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/") || 
            path.startsWith("/assets/") || path.startsWith("/webjars/")) {
            return true;
        }
        
        // Allow favicon
        if (path.equals("/favicon.ico")) {
            return true;
        }
        
        // Allow error pages
        if (path.startsWith("/error/")) {
            return true;
        }
        
        return false;
    }
}
