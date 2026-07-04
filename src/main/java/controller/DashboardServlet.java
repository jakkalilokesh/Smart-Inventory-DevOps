package com.smartinventory.controller;

import com.smartinventory.dto.DashboardDTO;
import com.smartinventory.model.User;
import com.smartinventory.service.DashboardService;
import com.smartinventory.service.impl.DashboardServiceImpl;
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
 * Servlet for handling dashboard operations.
 * Maps to /dashboard URL pattern.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(DashboardServlet.class);
    private DashboardService dashboardService = new DashboardServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check if user is authenticated
        if (session == null || session.getAttribute(AppConstants.SESSION_USER) == null) {
            response.sendRedirect(request.getContextPath() + "/auth/");
            return;
        }
        
        try {
            // Get dashboard data
            DashboardDTO dashboard = dashboardService.getDashboardData();
            
            // Set request attributes
            request.setAttribute("dashboard", dashboard);
            request.setAttribute("user", session.getAttribute(AppConstants.SESSION_USER));
            
            // Forward to dashboard JSP
            request.getRequestDispatcher("/WEB-INF/views/dashboard/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.error("Error loading dashboard", e);
            request.setAttribute("error", "Failed to load dashboard data");
            request.getRequestDispatcher("/WEB-INF/views/dashboard/dashboard.jsp").forward(request, response);
        }
    }
}
