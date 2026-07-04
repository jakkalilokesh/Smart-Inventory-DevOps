package com.smartinventory.controller;

import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Category;
import com.smartinventory.model.User;
import com.smartinventory.service.CategoryService;
import com.smartinventory.service.impl.CategoryServiceImpl;
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
 * Servlet for handling category CRUD operations.
 * Maps to /categories/* URL pattern.
 */
@WebServlet("/categories/*")
public class CategoryServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(CategoryServlet.class);
    private CategoryService categoryService = new CategoryServiceImpl();

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
            request.getRequestDispatcher("/WEB-INF/views/categories/list.jsp").forward(request, response);
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
            if (pathInfo == null || pathInfo.equals("/create")) {
                handleCreate(request, response);
            } else if (pathInfo.startsWith("/update/")) {
                handleUpdate(request, response, pathInfo);
            } else if (pathInfo.startsWith("/delete/")) {
                handleDelete(request, response, pathInfo);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error handling POST request", e);
            request.setAttribute("error", "An error occurred");
            request.getRequestDispatcher("/WEB-INF/views/categories/list.jsp").forward(request, response);
        }
    }

    /**
     * Handles listing categories with pagination and search.
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");
        
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = pageSizeStr != null ? Integer.parseInt(pageSizeStr) : AppConstants.DEFAULT_PAGE_SIZE;
        
        SearchCriteria criteria = new SearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setStatus(status);
        criteria.setPage(page);
        criteria.setPageSize(pageSize);
        criteria.setSortBy("category_name");
        criteria.setSortOrder("ASC");
        
        PaginationDTO<Category> result = categoryService.findWithPagination(criteria);
        
        request.setAttribute("categories", result);
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        
        request.getRequestDispatcher("/WEB-INF/views/categories/list.jsp").forward(request, response);
    }

    /**
     * Handles showing the create category form.
     */
    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.getRequestDispatcher("/WEB-INF/views/categories/form.jsp").forward(request, response);
    }

    /**
     * Handles showing the edit category form.
     */
    private void handleEditForm(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int categoryId = extractId(pathInfo);
        Category category = categoryService.findById(categoryId);
        
        if (category == null) {
            request.setAttribute("error", "Category not found");
            response.sendRedirect(request.getContextPath() + "/categories/");
            return;
        }
        
        request.setAttribute("category", category);
        request.getRequestDispatcher("/WEB-INF/views/categories/form.jsp").forward(request, response);
    }

    /**
     * Handles viewing a category.
     */
    private void handleView(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int categoryId = extractId(pathInfo);
        Category category = categoryService.findById(categoryId);
        
        if (category == null) {
            request.setAttribute("error", "Category not found");
            response.sendRedirect(request.getContextPath() + "/categories/");
            return;
        }
        
        request.setAttribute("category", category);
        request.getRequestDispatcher("/WEB-INF/views/categories/view.jsp").forward(request, response);
    }

    /**
     * Handles creating a new category.
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String categoryName = request.getParameter("categoryName");
        String description = request.getParameter("description");
        String parentCategoryIdStr = request.getParameter("parentCategoryId");
        Integer parentCategoryId = parentCategoryIdStr != null && !parentCategoryIdStr.isEmpty() 
            ? Integer.parseInt(parentCategoryIdStr) : null;
        
        Category category = new Category();
        category.setCategoryName(categoryName);
        category.setDescription(description);
        category.setParentCategoryId(parentCategoryId);
        category.setStatus(AppConstants.STATUS_ACTIVE);
        
        try {
            Category created = categoryService.create(category);
            request.setAttribute("success", AppConstants.SUCCESS_CREATED);
            response.sendRedirect(request.getContextPath() + "/categories/");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("category", category);
            request.getRequestDispatcher("/WEB-INF/views/categories/form.jsp").forward(request, response);
        }
    }

    /**
     * Handles updating a category.
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int categoryId = extractId(pathInfo);
        Category existing = categoryService.findById(categoryId);
        
        if (existing == null) {
            request.setAttribute("error", "Category not found");
            response.sendRedirect(request.getContextPath() + "/categories/");
            return;
        }
        
        String categoryName = request.getParameter("categoryName");
        String description = request.getParameter("description");
        String parentCategoryIdStr = request.getParameter("parentCategoryId");
        Integer parentCategoryId = parentCategoryIdStr != null && !parentCategoryIdStr.isEmpty() 
            ? Integer.parseInt(parentCategoryIdStr) : null;
        String status = request.getParameter("status");
        
        existing.setCategoryName(categoryName);
        existing.setDescription(description);
        existing.setParentCategoryId(parentCategoryId);
        existing.setStatus(status);
        
        try {
            categoryService.update(existing);
            request.setAttribute("success", AppConstants.SUCCESS_UPDATED);
            response.sendRedirect(request.getContextPath() + "/categories/");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("category", existing);
            request.getRequestDispatcher("/WEB-INF/views/categories/form.jsp").forward(request, response);
        }
    }

    /**
     * Handles deleting a category.
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int categoryId = extractId(pathInfo);
        
        boolean deleted = categoryService.delete(categoryId);
        
        if (deleted) {
            request.setAttribute("success", AppConstants.SUCCESS_DELETED);
        } else {
            request.setAttribute("error", "Failed to delete category");
        }
        
        response.sendRedirect(request.getContextPath() + "/categories/");
    }

    /**
     * Extracts ID from path info.
     */
    private int extractId(String pathInfo) {
        String[] parts = pathInfo.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }
}
