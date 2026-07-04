package com.smartinventory.controller;

import com.smartinventory.dto.StockTransactionDTO;
import com.smartinventory.model.InventoryTransaction;
import com.smartinventory.model.Product;
import com.smartinventory.model.User;
import com.smartinventory.service.InventoryService;
import com.smartinventory.service.ProductService;
import com.smartinventory.service.impl.InventoryServiceImpl;
import com.smartinventory.service.impl.ProductServiceImpl;
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
 * Servlet for handling inventory operations (stock in/out, adjustments).
 * Maps to /inventory/* URL pattern.
 */
@WebServlet("/inventory/*")
public class InventoryServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(InventoryServlet.class);
    private InventoryService inventoryService = new InventoryServiceImpl();
    private ProductService productService = new ProductServiceImpl();

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
            } else if (pathInfo.equals("/stock-in")) {
                handleStockInForm(request, response);
            } else if (pathInfo.equals("/stock-out")) {
                handleStockOutForm(request, response);
            } else if (pathInfo.equals("/adjust")) {
                handleAdjustForm(request, response);
            } else if (pathInfo.equals("/history")) {
                handleHistory(request, response);
            } else if (pathInfo.startsWith("/product/")) {
                handleProductHistory(request, response, pathInfo);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error handling GET request", e);
            request.setAttribute("error", "An error occurred");
            request.getRequestDispatcher("/WEB-INF/views/inventory/list.jsp").forward(request, response);
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
            if (pathInfo == null || pathInfo.equals("/stock-in")) {
                handleStockIn(request, response);
            } else if (pathInfo.equals("/stock-out")) {
                handleStockOut(request, response);
            } else if (pathInfo.equals("/adjust")) {
                handleAdjust(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error handling POST request", e);
            request.setAttribute("error", "An error occurred");
            request.getRequestDispatcher("/WEB-INF/views/inventory/list.jsp").forward(request, response);
        }
    }

    /**
     * Handles listing inventory transactions.
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<InventoryTransaction> transactions = inventoryService.findRecentTransactions(50);
        List<Product> lowStockProducts = productService.findLowStock();
        
        request.setAttribute("transactions", transactions);
        request.setAttribute("lowStockProducts", lowStockProducts);
        
        request.getRequestDispatcher("/WEB-INF/views/inventory/list.jsp").forward(request, response);
    }

    /**
     * Handles showing the stock in form.
     */
    private void handleStockInForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Product> products = productService.findActive();
        request.setAttribute("products", products);
        request.getRequestDispatcher("/WEB-INF/views/inventory/stock-in.jsp").forward(request, response);
    }

    /**
     * Handles showing the stock out form.
     */
    private void handleStockOutForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Product> products = productService.findActive();
        request.setAttribute("products", products);
        request.getRequestDispatcher("/WEB-INF/views/inventory/stock-out.jsp").forward(request, response);
    }

    /**
     * Handles showing the adjustment form.
     */
    private void handleAdjustForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Product> products = productService.findActive();
        request.setAttribute("products", products);
        request.getRequestDispatcher("/WEB-INF/views/inventory/adjust.jsp").forward(request, response);
    }

    /**
     * Handles showing transaction history.
     */
    private void handleHistory(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String type = request.getParameter("type");
        
        List<InventoryTransaction> transactions;
        if (type != null && !type.isEmpty()) {
            transactions = inventoryService.findTransactionsByType(type);
        } else {
            transactions = inventoryService.findAllTransactions();
        }
        
        request.setAttribute("transactions", transactions);
        request.setAttribute("type", type);
        
        request.getRequestDispatcher("/WEB-INF/views/inventory/history.jsp").forward(request, response);
    }

    /**
     * Handles showing product transaction history.
     */
    private void handleProductHistory(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws ServletException, IOException {
        
        int productId = extractId(pathInfo);
        Product product = productService.findById(productId);
        
        if (product == null) {
            request.setAttribute("error", "Product not found");
            response.sendRedirect(request.getContextPath() + "/inventory/");
            return;
        }
        
        List<InventoryTransaction> transactions = inventoryService.findTransactionsByProduct(productId);
        
        request.setAttribute("product", product);
        request.setAttribute("transactions", transactions);
        
        request.getRequestDispatcher("/WEB-INF/views/inventory/product-history.jsp").forward(request, response);
    }

    /**
     * Handles processing stock in.
     */
    private void handleStockIn(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(AppConstants.SESSION_USER);
        
        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String unitPriceStr = request.getParameter("unitPrice");
        BigDecimal unitPrice = unitPriceStr != null && !unitPriceStr.isEmpty() 
            ? new BigDecimal(unitPriceStr) : null;
        String referenceNumber = request.getParameter("referenceNumber");
        String notes = request.getParameter("notes");
        
        StockTransactionDTO transaction = new StockTransactionDTO();
        transaction.setProductId(productId);
        transaction.setTransactionType(AppConstants.TRANSACTION_STOCK_IN);
        transaction.setQuantity(quantity);
        transaction.setUnitPrice(unitPrice);
        transaction.setReferenceNumber(referenceNumber);
        transaction.setNotes(notes);
        transaction.setPerformedBy(user.getUserId());
        
        try {
            boolean success = inventoryService.processStockIn(transaction);
            if (success) {
                request.setAttribute("success", "Stock added successfully");
            } else {
                request.setAttribute("error", "Failed to add stock");
            }
            response.sendRedirect(request.getContextPath() + "/inventory/");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            List<Product> products = productService.findActive();
            request.setAttribute("products", products);
            request.getRequestDispatcher("/WEB-INF/views/inventory/stock-in.jsp").forward(request, response);
        }
    }

    /**
     * Handles processing stock out.
     */
    private void handleStockOut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(AppConstants.SESSION_USER);
        
        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String unitPriceStr = request.getParameter("unitPrice");
        BigDecimal unitPrice = unitPriceStr != null && !unitPriceStr.isEmpty() 
            ? new BigDecimal(unitPriceStr) : null;
        String referenceNumber = request.getParameter("referenceNumber");
        String notes = request.getParameter("notes");
        
        StockTransactionDTO transaction = new StockTransactionDTO();
        transaction.setProductId(productId);
        transaction.setTransactionType(AppConstants.TRANSACTION_STOCK_OUT);
        transaction.setQuantity(quantity);
        transaction.setUnitPrice(unitPrice);
        transaction.setReferenceNumber(referenceNumber);
        transaction.setNotes(notes);
        transaction.setPerformedBy(user.getUserId());
        
        try {
            boolean success = inventoryService.processStockOut(transaction);
            if (success) {
                request.setAttribute("success", "Stock removed successfully");
            } else {
                request.setAttribute("error", "Failed to remove stock");
            }
            response.sendRedirect(request.getContextPath() + "/inventory/");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            List<Product> products = productService.findActive();
            request.setAttribute("products", products);
            request.getRequestDispatcher("/WEB-INF/views/inventory/stock-out.jsp").forward(request, response);
        }
    }

    /**
     * Handles processing stock adjustment.
     */
    private void handleAdjust(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(AppConstants.SESSION_USER);
        
        int productId = Integer.parseInt(request.getParameter("productId"));
        int newQuantity = Integer.parseInt(request.getParameter("newQuantity"));
        String unitPriceStr = request.getParameter("unitPrice");
        BigDecimal unitPrice = unitPriceStr != null && !unitPriceStr.isEmpty() 
            ? new BigDecimal(unitPriceStr) : null;
        String referenceNumber = request.getParameter("referenceNumber");
        String notes = request.getParameter("notes");
        
        StockTransactionDTO transaction = new StockTransactionDTO();
        transaction.setProductId(productId);
        transaction.setTransactionType(AppConstants.TRANSACTION_ADJUSTMENT);
        transaction.setQuantity(newQuantity);
        transaction.setUnitPrice(unitPrice);
        transaction.setReferenceNumber(referenceNumber);
        transaction.setNotes(notes);
        transaction.setPerformedBy(user.getUserId());
        
        try {
            boolean success = inventoryService.processAdjustment(transaction);
            if (success) {
                request.setAttribute("success", "Stock adjusted successfully");
            } else {
                request.setAttribute("error", "Failed to adjust stock");
            }
            response.sendRedirect(request.getContextPath() + "/inventory/");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            List<Product> products = productService.findActive();
            request.setAttribute("products", products);
            request.getRequestDispatcher("/WEB-INF/views/inventory/adjust.jsp").forward(request, response);
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
