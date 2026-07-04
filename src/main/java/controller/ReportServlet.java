package com.smartinventory.controller;

import com.smartinventory.dto.ReportDTO;
import com.smartinventory.model.InventoryTransaction;
import com.smartinventory.model.Product;
import com.smartinventory.model.User;
import com.smartinventory.service.DataExportImportService;
import com.smartinventory.service.ReportService;
import com.smartinventory.service.impl.DataExportImportServiceImpl;
import com.smartinventory.service.impl.ReportServiceImpl;
import com.smartinventory.util.AppConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/reports/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50   // 50MB
)
public class ReportServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(ReportServlet.class);

    private final ReportService reportService = new ReportServiceImpl();
    private final DataExportImportService exportImportService = new DataExportImportServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(AppConstants.SESSION_USER) == null) {
            response.sendRedirect(request.getContextPath() + "/auth/");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/dashboard")) {
            handleDashboard(request, response);
        } else if (pathInfo.equals("/export")) {
            handleExport(request, response);
        } else if (pathInfo.equals("/template")) {
            handleTemplate(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(AppConstants.SESSION_USER) == null) {
            response.sendRedirect(request.getContextPath() + "/auth/");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.equals("/import")) {
            handleImport(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            ReportDTO inventorySummary = reportService.generateInventorySummaryReport();
            ReportDTO lowStockSummary = reportService.generateLowStockReport();
            
            request.setAttribute("inventorySummary", inventorySummary);
            request.setAttribute("lowStockSummary", lowStockSummary);
            request.getRequestDispatcher("/WEB-INF/views/reports/reports.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error loading reports dashboard", e);
            request.setAttribute("error", "Error loading reports dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/reports/reports.jsp").forward(request, response);
        }
    }

    private void handleExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String module = request.getParameter("module"); // products, transactions, valuation, suppliers, categories
        String format = request.getParameter("format"); // pdf, excel, csv, json

        if (module == null || format == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing module or format parameters.");
            return;
        }

        byte[] fileData = null;
        String contentType = "";
        String fileExtension = "";

        try {
            if ("products".equalsIgnoreCase(module)) {
                List<Product> products = reportService.generateInventorySummaryReport().getProducts();
                if ("pdf".equalsIgnoreCase(format)) {
                    fileData = reportService.exportProductsToPdf(products, "Active Products Report");
                    contentType = "application/pdf";
                    fileExtension = "pdf";
                } else if ("excel".equalsIgnoreCase(format)) {
                    fileData = reportService.exportProductsToExcel(products, "Active Products Report");
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    fileExtension = "xlsx";
                } else if ("csv".equalsIgnoreCase(format)) {
                    fileData = exportImportService.exportProductsToCsv();
                    contentType = "text/csv";
                    fileExtension = "csv";
                } else if ("json".equalsIgnoreCase(format)) {
                    fileData = exportImportService.exportProductsToJson();
                    contentType = "application/json";
                    fileExtension = "json";
                }
            } else if ("transactions".equalsIgnoreCase(module)) {
                LocalDateTime now = LocalDateTime.now();
                // Last 30 days
                List<InventoryTransaction> txs = reportService.generateTransactionReport(now.minusDays(30), now).getTransactions();
                if ("pdf".equalsIgnoreCase(format)) {
                    fileData = reportService.exportTransactionsToPdf(txs, "Inventory Transactions Report (Last 30 Days)");
                    contentType = "application/pdf";
                    fileExtension = "pdf";
                } else if ("excel".equalsIgnoreCase(format)) {
                    fileData = reportService.exportTransactionsToExcel(txs, "Inventory Transactions Report (Last 30 Days)");
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    fileExtension = "xlsx";
                }
            } else if ("valuation".equalsIgnoreCase(module)) {
                ReportDTO valReport = reportService.generateValuationReport();
                if ("pdf".equalsIgnoreCase(format)) {
                    fileData = reportService.exportValuationReportToPdf(valReport);
                    contentType = "application/pdf";
                    fileExtension = "pdf";
                } else if ("excel".equalsIgnoreCase(format)) {
                    fileData = reportService.exportValuationReportToExcel(valReport);
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    fileExtension = "xlsx";
                }
            } else if ("suppliers".equalsIgnoreCase(module)) {
                if ("csv".equalsIgnoreCase(format)) {
                    fileData = exportImportService.exportSuppliersToCsv();
                    contentType = "text/csv";
                    fileExtension = "csv";
                } else if ("json".equalsIgnoreCase(format)) {
                    fileData = exportImportService.exportSuppliersToJson();
                    contentType = "application/json";
                    fileExtension = "json";
                }
            } else if ("categories".equalsIgnoreCase(module)) {
                if ("csv".equalsIgnoreCase(format)) {
                    fileData = exportImportService.exportCategoriesToCsv();
                    contentType = "text/csv";
                    fileExtension = "csv";
                } else if ("json".equalsIgnoreCase(format)) {
                    fileData = exportImportService.exportCategoriesToJson();
                    contentType = "application/json";
                    fileExtension = "json";
                }
            }

            if (fileData != null) {
                String filename = String.format("%s_report_%s.%s", module,
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")),
                        fileExtension);
                response.setContentType(contentType);
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                response.setContentLength(fileData.length);
                response.getOutputStream().write(fileData);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported module or format combination.");
            }
        } catch (Exception e) {
            logger.error("Error generating export file", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Export error: " + e.getMessage());
        }
    }

    private void handleTemplate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String entityType = request.getParameter("entityType"); // PRODUCT, SUPPLIER, CATEGORY
        if (entityType == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing entityType parameter.");
            return;
        }

        try {
            byte[] fileData = exportImportService.generateCsvTemplate(entityType);
            String filename = entityType.toLowerCase() + "_import_template.csv";
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setContentLength(fileData.length);
            response.getOutputStream().write(fileData);
        } catch (Exception e) {
            logger.error("Error creating template", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Template creation error: " + e.getMessage());
        }
    }

    private void handleImport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String entityType = request.getParameter("entityType"); // PRODUCT, SUPPLIER, CATEGORY
        Part filePart = request.getPart("file");

        if (entityType == null || filePart == null || filePart.getSize() == 0) {
            request.setAttribute("error", "Please select a valid entity type and non-empty CSV/JSON file to import.");
            handleDashboard(request, response);
            return;
        }

        String filename = filePart.getSubmittedFileName().toLowerCase();
        try (InputStream inputStream = filePart.getInputStream()) {
            int importedCount = 0;
            if (filename.endsWith(".csv")) {
                if ("PRODUCT".equals(entityType)) {
                    importedCount = exportImportService.importProductsFromCsv(inputStream);
                } else if ("SUPPLIER".equals(entityType)) {
                    importedCount = exportImportService.importSuppliersFromCsv(inputStream);
                } else if ("CATEGORY".equals(entityType)) {
                    importedCount = exportImportService.importCategoriesFromCsv(inputStream);
                }
            } else if (filename.endsWith(".json") && "PRODUCT".equals(entityType)) {
                importedCount = exportImportService.importProductsFromJson(inputStream);
            } else {
                request.setAttribute("error", "Unsupported file format. Please upload CSV or JSON (JSON supported for Products only).");
                handleDashboard(request, response);
                return;
            }

            request.setAttribute("success", String.format("Successfully imported %d records of type %s.", importedCount, entityType));
        } catch (Exception e) {
            logger.error("Import failed", e);
            request.setAttribute("error", "Import failed: " + e.getMessage());
        }

        handleDashboard(request, response);
    }
}
