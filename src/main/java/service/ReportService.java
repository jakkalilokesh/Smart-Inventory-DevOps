package com.smartinventory.service;

import com.smartinventory.dto.ReportDTO;
import com.smartinventory.model.InventoryTransaction;
import com.smartinventory.model.Product;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Report generation operations.
 * Provides methods for generating various types of inventory reports
 * and exporting data in PDF and Excel formats.
 */
public interface ReportService {

    /**
     * Generates a comprehensive inventory summary report.
     *
     * @return ReportDTO containing inventory summary data
     */
    ReportDTO generateInventorySummaryReport();

    /**
     * Generates a low stock alert report.
     *
     * @return ReportDTO containing low stock products
     */
    ReportDTO generateLowStockReport();

    /**
     * Generates an inventory valuation report.
     *
     * @return ReportDTO containing valuation data
     */
    ReportDTO generateValuationReport();

    /**
     * Generates a transaction history report for a date range.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return ReportDTO containing transaction data
     */
    ReportDTO generateTransactionReport(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Generates a category-wise inventory report.
     *
     * @return ReportDTO containing category distribution data
     */
    ReportDTO generateCategoryReport();

    /**
     * Generates a supplier-wise inventory report.
     *
     * @return ReportDTO containing supplier distribution data
     */
    ReportDTO generateSupplierReport();

    /**
     * Exports products to PDF format.
     *
     * @param products the list of products to export
     * @param title    the report title
     * @return byte array containing the PDF data
     */
    byte[] exportProductsToPdf(List<Product> products, String title);

    /**
     * Exports products to Excel format.
     *
     * @param products the list of products to export
     * @param title    the report title
     * @return byte array containing the Excel data
     */
    byte[] exportProductsToExcel(List<Product> products, String title);

    /**
     * Exports transactions to PDF format.
     *
     * @param transactions the list of transactions to export
     * @param title        the report title
     * @return byte array containing the PDF data
     */
    byte[] exportTransactionsToPdf(List<InventoryTransaction> transactions, String title);

    /**
     * Exports transactions to Excel format.
     *
     * @param transactions the list of transactions to export
     * @param title        the report title
     * @return byte array containing the Excel data
     */
    byte[] exportTransactionsToExcel(List<InventoryTransaction> transactions, String title);

    /**
     * Exports full inventory valuation report to PDF.
     *
     * @param report the report data
     * @return byte array containing the PDF data
     */
    byte[] exportValuationReportToPdf(ReportDTO report);

    /**
     * Exports full inventory valuation report to Excel.
     *
     * @param report the report data
     * @return byte array containing the Excel data
     */
    byte[] exportValuationReportToExcel(ReportDTO report);
}
