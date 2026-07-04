package com.smartinventory.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.smartinventory.dao.*;
import com.smartinventory.dao.impl.*;
import com.smartinventory.dto.ReportDTO;
import com.smartinventory.model.Category;
import com.smartinventory.model.InventoryTransaction;
import com.smartinventory.model.Product;
import com.smartinventory.model.Supplier;
import com.smartinventory.service.ReportService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportServiceImpl implements ReportService {
    private static final Logger logger = LogManager.getLogger(ReportServiceImpl.class);

    private final ProductDAO productDAO = new ProductDAOImpl();
    private final CategoryDAO categoryDAO = new CategoryDAOImpl();
    private final SupplierDAO supplierDAO = new SupplierDAOImpl();
    private final InventoryTransactionDAO transactionDAO = new InventoryTransactionDAOImpl();

    @Override
    public ReportDTO generateInventorySummaryReport() {
        ReportDTO report = new ReportDTO();
        report.setReportType("INVENTORY_SUMMARY");
        report.setReportTitle("Inventory Summary Report");

        List<Product> allProducts = productDAO.findAll();
        List<Product> activeProducts = allProducts.stream()
                .filter(p -> "ACTIVE".equals(p.getStatus()))
                .collect(Collectors.toList());

        report.setTotalProducts(allProducts.size());
        report.setActiveProducts(activeProducts.size());
        
        long lowStockCount = activeProducts.stream().filter(Product::isLowStock).count();
        long outOfStockCount = activeProducts.stream().filter(p -> p.getStockQuantity() <= 0).count();
        report.setLowStockProducts((int) lowStockCount);
        report.setOutOfStockProducts((int) outOfStockCount);

        BigDecimal totalVal = BigDecimal.ZERO;
        BigDecimal totalSellVal = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (Product p : activeProducts) {
            totalVal = totalVal.add(p.getTotalBuyingValue());
            totalSellVal = totalSellVal.add(p.getTotalSellingValue());
            totalProfit = totalProfit.add(p.getPotentialProfit());
        }

        report.setTotalInventoryValue(totalVal);
        report.setTotalSellingValue(totalSellVal);
        report.setTotalPotentialProfit(totalProfit);
        report.setProducts(activeProducts);
        report.setLowStockList(activeProducts.stream().filter(Product::isLowStock).collect(Collectors.toList()));

        // Distributions
        Map<String, Integer> catDist = new HashMap<>();
        Map<String, BigDecimal> catVals = new HashMap<>();
        for (Product p : activeProducts) {
            String catName = p.getCategoryName() != null ? p.getCategoryName() : "Uncategorized";
            catDist.put(catName, catDist.getOrDefault(catName, 0) + p.getStockQuantity());
            catVals.put(catName, catVals.getOrDefault(catName, BigDecimal.ZERO).add(p.getTotalBuyingValue()));
        }
        report.setCategoryDistribution(catDist);
        report.setCategoryValues(catVals);

        Map<String, Integer> supDist = new HashMap<>();
        for (Product p : activeProducts) {
            String supName = p.getSupplierName() != null ? p.getSupplierName() : "No Supplier";
            supDist.put(supName, supDist.getOrDefault(supName, 0) + p.getStockQuantity());
        }
        report.setSupplierDistribution(supDist);

        return report;
    }

    @Override
    public ReportDTO generateLowStockReport() {
        ReportDTO report = new ReportDTO();
        report.setReportType("LOW_STOCK");
        report.setReportTitle("Low Stock Alerts Report");

        List<Product> lowStock = productDAO.findLowStock();
        report.setProducts(lowStock);
        report.setLowStockList(lowStock);
        report.setLowStockProducts(lowStock.size());

        return report;
    }

    @Override
    public ReportDTO generateValuationReport() {
        ReportDTO report = new ReportDTO();
        report.setReportType("VALUATION");
        report.setReportTitle("Inventory Valuation Report");

        List<Product> activeProducts = productDAO.findActive();
        report.setProducts(activeProducts);

        BigDecimal totalVal = BigDecimal.ZERO;
        BigDecimal totalSellVal = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;

        for (Product p : activeProducts) {
            totalVal = totalVal.add(p.getTotalBuyingValue());
            totalSellVal = totalSellVal.add(p.getTotalSellingValue());
            totalProfit = totalProfit.add(p.getPotentialProfit());
        }

        report.setTotalInventoryValue(totalVal);
        report.setTotalSellingValue(totalSellVal);
        report.setTotalPotentialProfit(totalProfit);

        Map<String, Integer> catDist = new HashMap<>();
        Map<String, BigDecimal> catVals = new HashMap<>();
        for (Product p : activeProducts) {
            String catName = p.getCategoryName() != null ? p.getCategoryName() : "Uncategorized";
            catDist.put(catName, catDist.getOrDefault(catName, 0) + p.getStockQuantity());
            catVals.put(catName, catVals.getOrDefault(catName, BigDecimal.ZERO).add(p.getTotalBuyingValue()));
        }
        report.setCategoryDistribution(catDist);
        report.setCategoryValues(catVals);

        return report;
    }

    @Override
    public ReportDTO generateTransactionReport(LocalDateTime startDate, LocalDateTime endDate) {
        ReportDTO report = new ReportDTO();
        report.setReportType("TRANSACTION");
        report.setReportTitle("Inventory Transactions Report");
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        List<InventoryTransaction> txs = transactionDAO.findByDateRange(startDate, endDate);
        report.setTransactions(txs);
        report.setTotalTransactions(txs.size());

        int inCount = 0, outCount = 0, adjCount = 0;
        BigDecimal inValue = BigDecimal.ZERO;
        BigDecimal outValue = BigDecimal.ZERO;

        for (InventoryTransaction t : txs) {
            if ("STOCK_IN".equals(t.getTransactionType())) {
                inCount++;
                if (t.getTotalPrice() != null) inValue = inValue.add(t.getTotalPrice());
            } else if ("STOCK_OUT".equals(t.getTransactionType())) {
                outCount++;
                if (t.getTotalPrice() != null) outValue = outValue.add(t.getTotalPrice());
            } else if ("ADJUSTMENT".equals(t.getTransactionType())) {
                adjCount++;
            }
        }

        report.setStockInCount(inCount);
        report.setStockOutCount(outCount);
        report.setAdjustmentCount(adjCount);
        report.setTotalStockInValue(inValue);
        report.setTotalStockOutValue(outValue);

        return report;
    }

    @Override
    public ReportDTO generateCategoryReport() {
        ReportDTO report = new ReportDTO();
        report.setReportType("CATEGORY");
        report.setReportTitle("Category Breakdown Report");

        List<Product> activeProducts = productDAO.findActive();
        Map<String, Integer> catDist = new HashMap<>();
        Map<String, BigDecimal> catVals = new HashMap<>();
        for (Product p : activeProducts) {
            String catName = p.getCategoryName() != null ? p.getCategoryName() : "Uncategorized";
            catDist.put(catName, catDist.getOrDefault(catName, 0) + p.getStockQuantity());
            catVals.put(catName, catVals.getOrDefault(catName, BigDecimal.ZERO).add(p.getTotalBuyingValue()));
        }
        report.setCategoryDistribution(catDist);
        report.setCategoryValues(catVals);

        return report;
    }

    @Override
    public ReportDTO generateSupplierReport() {
        ReportDTO report = new ReportDTO();
        report.setReportType("SUPPLIER");
        report.setReportTitle("Supplier Breakdown Report");

        List<Product> activeProducts = productDAO.findActive();
        Map<String, Integer> supDist = new HashMap<>();
        for (Product p : activeProducts) {
            String supName = p.getSupplierName() != null ? p.getSupplierName() : "No Supplier";
            supDist.put(supName, supDist.getOrDefault(supName, 0) + p.getStockQuantity());
        }
        report.setSupplierDistribution(supDist);

        return report;
    }

    @Override
    public byte[] exportProductsToPdf(List<Product> products, String title) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            com.itextpdf.text.Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            com.itextpdf.text.Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
            com.itextpdf.text.Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);

            // Title
            Paragraph titlePara = new Paragraph(title, titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(10);
            document.add(titlePara);

            // Metadata
            Paragraph metaPara = new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), metaFont);
            metaPara.setAlignment(Element.ALIGN_CENTER);
            metaPara.setSpacingAfter(20);
            document.add(metaPara);

            // Table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2f, 4f, 2.5f, 2f, 2f, 2f});

            // Header Row
            String[] headers = {"SKU", "Product Name", "Category", "Buying Price", "Selling Price", "Quantity"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                cell.setBackgroundColor(new BaseColor(0, 102, 204));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }

            // Data Rows
            for (Product p : products) {
                table.addCell(new PdfPCell(new Phrase(p.getSku(), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(p.getProductName(), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(p.getCategoryName() != null ? p.getCategoryName() : "N/A", bodyFont)));
                
                PdfPCell buyingCell = new PdfPCell(new Phrase("$" + p.getBuyingPrice().toString(), bodyFont));
                buyingCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(buyingCell);

                PdfPCell sellingCell = new PdfPCell(new Phrase("$" + p.getSellingPrice().toString(), bodyFont));
                sellingCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(sellingCell);

                PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(p.getStockQuantity()), bodyFont));
                qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(qtyCell);
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            logger.error("Error creating PDF", e);
        }
        return out.toByteArray();
    }

    @Override
    public byte[] exportProductsToExcel(List<Product> products, String title) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            // Fonts & Styles
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));

            // Title Row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);
            sheet.createRow(1); // empty row

            // Header
            Row headerRow = sheet.createRow(2);
            String[] headers = {"SKU", "Barcode", "Product Name", "Category", "Supplier", "Buying Price", "Selling Price", "Quantity", "Min Stock", "Status"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Data
            int rowNum = 3;
            for (Product p : products) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getSku());
                row.createCell(1).setCellValue(p.getBarcode());
                row.createCell(2).setCellValue(p.getProductName());
                row.createCell(3).setCellValue(p.getCategoryName());
                row.createCell(4).setCellValue(p.getSupplierName());

                Cell buyingCell = row.createCell(5);
                buyingCell.setCellValue(p.getBuyingPrice().doubleValue());
                buyingCell.setCellStyle(currencyStyle);

                Cell sellingCell = row.createCell(6);
                sellingCell.setCellValue(p.getSellingPrice().doubleValue());
                sellingCell.setCellStyle(currencyStyle);

                row.createCell(7).setCellValue(p.getStockQuantity());
                row.createCell(8).setCellValue(p.getMinimumStock());
                row.createCell(9).setCellValue(p.getStatus());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        } catch (Exception e) {
            logger.error("Error creating Excel", e);
        }
        return out.toByteArray();
    }

    @Override
    public byte[] exportTransactionsToPdf(List<InventoryTransaction> transactions, String title) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            com.itextpdf.text.Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            com.itextpdf.text.Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
            com.itextpdf.text.Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);

            Paragraph titlePara = new Paragraph(title, titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(10);
            document.add(titlePara);

            Paragraph metaPara = new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), metaFont);
            metaPara.setAlignment(Element.ALIGN_CENTER);
            metaPara.setSpacingAfter(20);
            document.add(metaPara);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 2f, 3.5f, 1.5f, 2f, 2f, 2f});

            String[] headers = {"Date", "Type", "Product", "Qty", "Prev Qty", "New Qty", "Performed By"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                cell.setBackgroundColor(new BaseColor(0, 102, 204));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (InventoryTransaction t : transactions) {
                table.addCell(new PdfPCell(new Phrase(t.getTransactionDate().format(dtf), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(t.getTransactionTypeDisplay(), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(t.getProductName(), bodyFont)));
                
                PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(t.getQuantity()), bodyFont));
                qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(qtyCell);

                PdfPCell prevCell = new PdfPCell(new Phrase(String.valueOf(t.getPreviousQuantity()), bodyFont));
                prevCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(prevCell);

                PdfPCell newCell = new PdfPCell(new Phrase(String.valueOf(t.getNewQuantity()), bodyFont));
                newCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(newCell);

                table.addCell(new PdfPCell(new Phrase(t.getPerformedByName() != null ? t.getPerformedByName() : "System", bodyFont)));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            logger.error("Error creating transactions PDF", e);
        }
        return out.toByteArray();
    }

    @Override
    public byte[] exportTransactionsToExcel(List<InventoryTransaction> transactions, String title) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transactions");

            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);
            sheet.createRow(1);

            Row headerRow = sheet.createRow(2);
            String[] headers = {"Transaction ID", "Date", "Type", "Product SKU", "Product Name", "Quantity", "Prev Stock", "New Stock", "Unit Price", "Total Price", "Reference", "Notes", "Performed By"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowNum = 3;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (InventoryTransaction t : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(t.getTransactionId());
                row.createCell(1).setCellValue(t.getTransactionDate().format(dtf));
                row.createCell(2).setCellValue(t.getTransactionTypeDisplay());
                row.createCell(3).setCellValue(t.getSku());
                row.createCell(4).setCellValue(t.getProductName());
                row.createCell(5).setCellValue(t.getQuantity());
                row.createCell(6).setCellValue(t.getPreviousQuantity());
                row.createCell(7).setCellValue(t.getNewQuantity());

                Cell upCell = row.createCell(8);
                if (t.getUnitPrice() != null) {
                    upCell.setCellValue(t.getUnitPrice().doubleValue());
                    upCell.setCellStyle(currencyStyle);
                }

                Cell tpCell = row.createCell(9);
                if (t.getTotalPrice() != null) {
                    tpCell.setCellValue(t.getTotalPrice().doubleValue());
                    tpCell.setCellStyle(currencyStyle);
                }

                row.createCell(10).setCellValue(t.getReferenceNumber());
                row.createCell(11).setCellValue(t.getNotes());
                row.createCell(12).setCellValue(t.getPerformedByName());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        } catch (Exception e) {
            logger.error("Error creating transactions Excel", e);
        }
        return out.toByteArray();
    }

    @Override
    public byte[] exportValuationReportToPdf(ReportDTO report) {
        return exportProductsToPdf(report.getProducts(), "Inventory Valuation Report");
    }

    @Override
    public byte[] exportValuationReportToExcel(ReportDTO report) {
        return exportProductsToExcel(report.getProducts(), "Inventory Valuation Report");
    }
}
