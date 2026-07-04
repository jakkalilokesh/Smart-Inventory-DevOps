package com.smartinventory.controller;

import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Category;
import com.smartinventory.model.Product;
import com.smartinventory.model.Supplier;
import com.smartinventory.service.CategoryService;
import com.smartinventory.service.ProductService;
import com.smartinventory.service.SupplierService;
import com.smartinventory.service.impl.CategoryServiceImpl;
import com.smartinventory.service.impl.ProductServiceImpl;
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
import java.math.BigDecimal;
import java.util.List;

/**
 * Servlet for handling product CRUD operations.
 * Maps to /products/* URL pattern.
 */
@WebServlet("/products/*")
public class ProductServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(ProductServlet.class);
    private ProductService productService = new ProductServiceImpl();
    private CategoryService categoryService = new CategoryServiceImpl();
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
            request.getRequestDispatcher("/WEB-INF/views/products/list.jsp").forward(request, response);
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
            request.getRequestDispatcher("/WEB-INF/views/products/list.jsp").forward(request, response);
        }
    }

    /**
     * Handles listing products with pagination and search.
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        String category = request.getParameter("category");
        String supplier = request.getParameter("supplier");
        String status = request.getParameter("status");
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");
        
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = pageSizeStr != null ? Integer.parseInt(pageSizeStr) : AppConstants.DEFAULT_PAGE_SIZE;
        
        SearchCriteria criteria = new SearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setCategory(category);
        criteria.setSupplier(supplier);
        criteria.setStatus(status);
        criteria.setPage(page);
        criteria.setPageSize(pageSize);
        criteria.setSortBy("product_name");
        criteria.setSortOrder("ASC");
        
        PaginationDTO<Product> result = productService.findWithPagination(criteria);
        
        // Get categories and suppliers for filters
        List<Category> categories = categoryService.findActive();
        List<Supplier> suppliers = supplierService.findActive();
        
        request.setAttribute("products", result);
        request.setAttribute("categories", categories);
        request.setAttribute("suppliers", suppliers);
        request.setAttribute("keyword", keyword);
        request.setAttribute("selectedCategory", category);
        request.setAttribute("selectedSupplier", supplier);
        request.setAttribute("status", status);
        
        request.getRequestDispatcher("/WEB-INF/views/products/list.jsp").forward(request, response);
    }

    /**
     * Handles showing the create product form.
     */
    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Category> categories = categoryService.findActive();
        List<Supplier> suppliers = supplierService.findActive();
        
        request.setAttribute("categories", categories);
        request.setAttribute("suppliers", suppliers);
        request.getRequestDispatcher("/WEB-INF/views/products/form.jsp").forward(request, response);
    }

    /**
     * Handles showing the edit product form.
     */
    private void handleEditForm(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int productId = extractId(pathInfo);
        Product product = productService.findById(productId);
        
        if (product == null) {
            request.setAttribute("error", "Product not found");
            response.sendRedirect(request.getContextPath() + "/products/");
            return;
        }
        
        List<Category> categories = categoryService.findActive();
        List<Supplier> suppliers = supplierService.findActive();
        
        request.setAttribute("product", product);
        request.setAttribute("categories", categories);
        request.setAttribute("suppliers", suppliers);
        request.getRequestDispatcher("/WEB-INF/views/products/form.jsp").forward(request, response);
    }

    /**
     * Handles viewing a product.
     */
    private void handleView(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int productId = extractId(pathInfo);
        Product product = productService.findById(productId);
        
        if (product == null) {
            request.setAttribute("error", "Product not found");
            response.sendRedirect(request.getContextPath() + "/products/");
            return;
        }
        
        request.setAttribute("product", product);
        request.getRequestDispatcher("/WEB-INF/views/products/view.jsp").forward(request, response);
    }

    /**
     * Handles creating a new product.
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String sku = request.getParameter("sku");
        String barcode = request.getParameter("barcode");
        String productName = request.getParameter("productName");
        String description = request.getParameter("description");
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        String supplierIdStr = request.getParameter("supplierId");
        Integer supplierId = supplierIdStr != null && !supplierIdStr.isEmpty() 
            ? Integer.parseInt(supplierIdStr) : null;
        BigDecimal buyingPrice = new BigDecimal(request.getParameter("buyingPrice"));
        BigDecimal sellingPrice = new BigDecimal(request.getParameter("sellingPrice"));
        int stockQuantity = Integer.parseInt(request.getParameter("stockQuantity"));
        int minimumStock = Integer.parseInt(request.getParameter("minimumStock"));
        String maximumStockStr = request.getParameter("maximumStock");
        Integer maximumStock = maximumStockStr != null && !maximumStockStr.isEmpty() 
            ? Integer.parseInt(maximumStockStr) : null;
        int reorderLevel = Integer.parseInt(request.getParameter("reorderLevel"));
        String unit = request.getParameter("unit");
        String status = request.getParameter("status");
        
        Product product = new Product();
        product.setSku(sku);
        product.setBarcode(barcode);
        product.setProductName(productName);
        product.setDescription(description);
        product.setCategoryId(categoryId);
        product.setSupplierId(supplierId);
        product.setBuyingPrice(buyingPrice);
        product.setSellingPrice(sellingPrice);
        product.setStockQuantity(stockQuantity);
        product.setMinimumStock(minimumStock);
        product.setMaximumStock(maximumStock);
        product.setReorderLevel(reorderLevel);
        product.setUnit(unit);
        product.setStatus(status);
        
        try {
            Product created = productService.create(product);
            request.setAttribute("success", AppConstants.SUCCESS_CREATED);
            response.sendRedirect(request.getContextPath() + "/products/");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("product", product);
            List<Category> categories = categoryService.findActive();
            List<Supplier> suppliers = supplierService.findActive();
            request.setAttribute("categories", categories);
            request.setAttribute("suppliers", suppliers);
            request.getRequestDispatcher("/WEB-INF/views/products/form.jsp").forward(request, response);
        }
    }

    /**
     * Handles updating a product.
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int productId = extractId(pathInfo);
        Product existing = productService.findById(productId);
        
        if (existing == null) {
            request.setAttribute("error", "Product not found");
            response.sendRedirect(request.getContextPath() + "/products/");
            return;
        }
        
        String sku = request.getParameter("sku");
        String barcode = request.getParameter("barcode");
        String productName = request.getParameter("productName");
        String description = request.getParameter("description");
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        String supplierIdStr = request.getParameter("supplierId");
        Integer supplierId = supplierIdStr != null && !supplierIdStr.isEmpty() 
            ? Integer.parseInt(supplierIdStr) : null;
        BigDecimal buyingPrice = new BigDecimal(request.getParameter("buyingPrice"));
        BigDecimal sellingPrice = new BigDecimal(request.getParameter("sellingPrice"));
        int stockQuantity = Integer.parseInt(request.getParameter("stockQuantity"));
        int minimumStock = Integer.parseInt(request.getParameter("minimumStock"));
        String maximumStockStr = request.getParameter("maximumStock");
        Integer maximumStock = maximumStockStr != null && !maximumStockStr.isEmpty() 
            ? Integer.parseInt(maximumStockStr) : null;
        int reorderLevel = Integer.parseInt(request.getParameter("reorderLevel"));
        String unit = request.getParameter("unit");
        String status = request.getParameter("status");
        
        existing.setSku(sku);
        existing.setBarcode(barcode);
        existing.setProductName(productName);
        existing.setDescription(description);
        existing.setCategoryId(categoryId);
        existing.setSupplierId(supplierId);
        existing.setBuyingPrice(buyingPrice);
        existing.setSellingPrice(sellingPrice);
        existing.setStockQuantity(stockQuantity);
        existing.setMinimumStock(minimumStock);
        existing.setMaximumStock(maximumStock);
        existing.setReorderLevel(reorderLevel);
        existing.setUnit(unit);
        existing.setStatus(status);
        
        try {
            productService.update(existing);
            request.setAttribute("success", AppConstants.SUCCESS_UPDATED);
            response.sendRedirect(request.getContextPath() + "/products/");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("product", existing);
            List<Category> categories = categoryService.findActive();
            List<Supplier> suppliers = supplierService.findActive();
            request.setAttribute("categories", categories);
            request.setAttribute("suppliers", suppliers);
            request.getRequestDispatcher("/WEB-INF/views/products/form.jsp").forward(request, response);
        }
    }

    /**
     * Handles deleting a product.
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int productId = extractId(pathInfo);
        
        boolean deleted = productService.delete(productId);
        
        if (deleted) {
            request.setAttribute("success", AppConstants.SUCCESS_DELETED);
        } else {
            request.setAttribute("error", "Failed to delete product");
        }
        
        response.sendRedirect(request.getContextPath() + "/products/");
    }

    /**
     * Extracts ID from path info.
     */
    private int extractId(String pathInfo) {
        String[] parts = pathInfo.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }
}
