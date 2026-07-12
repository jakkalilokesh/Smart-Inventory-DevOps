package com.smartinventory.controller;

import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.model.ActivityLog;
import com.smartinventory.model.User;
import com.smartinventory.service.ActivityLogService;
import com.smartinventory.service.impl.ActivityLogServiceImpl;
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
 * Servlet for managing and displaying system audit/activity logs.
 * Accessible only by administrators.
 * Maps to /audit-logs/* URL pattern.
 */
@WebServlet("/audit-logs/*")
public class AuditLogServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(AuditLogServlet.class);
    private final ActivityLogService activityLogService = new ActivityLogServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Authenticate
        if (session == null || session.getAttribute(AppConstants.SESSION_USER) == null) {
            response.sendRedirect(request.getContextPath() + "/auth/");
            return;
        }
        
        // Check admin role
        User currentUser = (User) session.getAttribute(AppConstants.SESSION_USER);
        if (!AppConstants.ROLE_ADMIN.equals(currentUser.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin role required.");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
                handleList(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error handling GET request in AuditLogServlet", e);
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/dashboard/dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Authenticate
        if (session == null || session.getAttribute(AppConstants.SESSION_USER) == null) {
            response.sendRedirect(request.getContextPath() + "/auth/");
            return;
        }
        
        // Check admin role
        User currentUser = (User) session.getAttribute(AppConstants.SESSION_USER);
        if (!AppConstants.ROLE_ADMIN.equals(currentUser.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin role required.");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo != null && pathInfo.equals("/clear")) {
                handleClearLogs(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error handling POST request in AuditLogServlet", e);
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/audit-logs/");
        }
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        String moduleFilter = request.getParameter("moduleFilter"); // maps to status field in criteria
        String actionFilter = request.getParameter("actionFilter"); // maps to role field in criteria
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");
        
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = pageSizeStr != null ? Integer.parseInt(pageSizeStr) : AppConstants.DEFAULT_PAGE_SIZE;
        
        SearchCriteria criteria = new SearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setStatus(moduleFilter); // reuse status for module
        criteria.setRole(actionFilter);   // reuse role for action
        criteria.setPage(page);
        criteria.setPageSize(pageSize);
        criteria.setSortBy("created_at");
        criteria.setSortOrder("DESC");
        
        PaginationDTO<ActivityLog> result = activityLogService.findWithPagination(criteria);
        
        request.setAttribute("logs", result);
        request.setAttribute("keyword", keyword);
        request.setAttribute("moduleFilter", moduleFilter);
        request.setAttribute("actionFilter", actionFilter);
        
        request.getRequestDispatcher("/WEB-INF/views/audit/logs.jsp").forward(request, response);
    }

    private void handleClearLogs(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String daysStr = request.getParameter("daysToKeep");
        int daysToKeep = 30; // default 30 days
        
        if (daysStr != null && !daysStr.isEmpty()) {
            try {
                daysToKeep = Integer.parseInt(daysStr);
            } catch (NumberFormatException e) {
                logger.warn("Invalid daysToKeep value: {}", daysStr);
            }
        }
        
        try {
            int deleted = activityLogService.cleanupOldLogs(daysToKeep);
            request.getSession().setAttribute("success", "Successfully cleared " + deleted + " activity logs older than " + daysToKeep + " days.");
        } catch (Exception e) {
            logger.error("Error cleaning up old logs", e);
            request.getSession().setAttribute("error", "Failed to clear activity logs: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/audit-logs/");
    }
}
