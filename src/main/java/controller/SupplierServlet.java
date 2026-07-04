package com.smartinventory.controller;

import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Supplier;
import com.smartinventory.service.SupplierService;
import com.smartinventory.service.impl.SupplierServiceImpl;
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
 * Servlet for handling supplier CRUD operations.
 * Maps to /suppliers/* URL pattern.
 */
@WebServlet("/suppliers/*")
public class SupplierServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(SupplierServlet.class);
    private SupplierService supplierService = new SupplierServiceImpl();

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
            request.getRequestDispatcher("/WEB-INF/views/suppliers/list.jsp").forward(request, response);
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
            request.getRequestDispatcher("/WEB-INF/views/suppliers/list.jsp").forward(request, response);
        }
    }

    /**
     * Handles listing suppliers with pagination and search.
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
        criteria.setSortBy("supplier_name");
        criteria.setSortOrder("ASC");
        
        PaginationDTO<Supplier> result = supplierService.findWithPagination(criteria);
        
        request.setAttribute("suppliers", result);
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        
        request.getRequestDispatcher("/WEB-INF/views/suppliers/list.jsp").forward(request, response);
    }

    /**
     * Handles showing the create supplier form.
     */
    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.getRequestDispatcher("/WEB-INF/views/suppliers/form.jsp").forward(request, response);
    }

    /**
     * Handles showing the edit supplier form.
     */
    private void handleEditForm(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int supplierId = extractId(pathInfo);
        Supplier supplier = supplierService.findById(supplierId);
        
        if (supplier == null) {
            request.setAttribute("error", "Supplier not found");
            response.sendRedirect(request.getContextPath() + "/suppliers/");
            return;
        }
        
        request.setAttribute("supplier", supplier);
        request.getRequestDispatcher("/WEB-INF/views/suppliers/form.jsp").forward(request, response);
    }

    /**
     * Handles viewing a supplier.
     */
    private void handleView(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int supplierId = extractId(pathInfo);
        Supplier supplier = supplierService.findById(supplierId);
        
        if (supplier == null) {
            request.setAttribute("error", "Supplier not found");
            response.sendRedirect(request.getContextPath() + "/suppliers/");
            return;
        }
        
        request.setAttribute("supplier", supplier);
        request.getRequestDispatcher("/WEB-INF/views/suppliers/view.jsp").forward(request, response);
    }

    /**
     * Handles creating a new supplier.
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String supplierName = request.getParameter("supplierName");
        String contactPerson = request.getParameter("contactPerson");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String country = request.getParameter("country");
        String postalCode = request.getParameter("postalCode");
        String taxId = request.getParameter("taxId");
        String status = request.getParameter("status");
        
        Supplier supplier = new Supplier();
        supplier.setSupplierName(supplierName);
        supplier.setContactPerson(contactPerson);
        supplier.setEmail(email);
        supplier.setPhone(phone);
        supplier.setAddress(address);
        supplier.setCity(city);
        supplier.setState(state);
        supplier.setCountry(country);
        supplier.setPostalCode(postalCode);
        supplier.setTaxId(taxId);
        supplier.setStatus(status);
        
        try {
            Supplier created = supplierService.create(supplier);
            request.setAttribute("success", AppConstants.SUCCESS_CREATED);
            response.sendRedirect(request.getContextPath() + "/suppliers/");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("supplier", supplier);
            request.getRequestDispatcher("/WEB-INF/views/suppliers/form.jsp").forward(request, response);
        }
    }

    /**
     * Handles updating a supplier.
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int supplierId = extractId(pathInfo);
        Supplier existing = supplierService.findById(supplierId);
        
        if (existing == null) {
            request.setAttribute("error", "Supplier not found");
            response.sendRedirect(request.getContextPath() + "/suppliers/");
            return;
        }
        
        String supplierName = request.getParameter("supplierName");
        String contactPerson = request.getParameter("contactPerson");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String country = request.getParameter("country");
        String postalCode = request.getParameter("postalCode");
        String taxId = request.getParameter("taxId");
        String status = request.getParameter("status");
        
        existing.setSupplierName(supplierName);
        existing.setContactPerson(contactPerson);
        existing.setEmail(email);
        existing.setPhone(phone);
        existing.setAddress(address);
        existing.setCity(city);
        existing.setState(state);
        existing.setCountry(country);
        existing.setPostalCode(postalCode);
        existing.setTaxId(taxId);
        existing.setStatus(status);
        
        try {
            supplierService.update(existing);
            request.setAttribute("success", AppConstants.SUCCESS_UPDATED);
            response.sendRedirect(request.getContextPath() + "/suppliers/");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("supplier", existing);
            request.getRequestDispatcher("/WEB-INF/views/suppliers/form.jsp").forward(request, response);
        }
    }

    /**
     * Handles deleting a supplier.
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int supplierId = extractId(pathInfo);
        
        boolean deleted = supplierService.delete(supplierId);
        
        if (deleted) {
            request.setAttribute("success", AppConstants.SUCCESS_DELETED);
        } else {
            request.setAttribute("error", "Failed to delete supplier");
        }
        
        response.sendRedirect(request.getContextPath() + "/suppliers/");
    }

    /**
     * Extracts ID from path info.
     */
    private int extractId(String pathInfo) {
        String[] parts = pathInfo.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }
}
