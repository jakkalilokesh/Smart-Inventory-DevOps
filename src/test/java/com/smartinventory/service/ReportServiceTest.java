package com.smartinventory.service;

import com.smartinventory.dto.ReportDTO;
import com.smartinventory.model.Product;
import com.smartinventory.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReportServiceTest {

    private ReportService reportService;

    @BeforeEach
    public void setUp() {
        reportService = new ReportServiceImpl();
    }

    @Test
    public void testExportProductsToPdf_NotNull() {
        List<Product> products = new ArrayList<>();
        Product p = new Product();
        p.setSku("P001");
        p.setProductName("Test Item");
        p.setBuyingPrice(BigDecimal.TEN);
        p.setSellingPrice(BigDecimal.valueOf(15));
        p.setStockQuantity(5);
        products.add(p);

        byte[] pdfData = reportService.exportProductsToPdf(products, "Test PDF Report");
        assertNotNull(pdfData);
        assertTrue(pdfData.length > 0);
    }

    @Test
    public void testExportProductsToExcel_NotNull() {
        List<Product> products = new ArrayList<>();
        Product p = new Product();
        p.setSku("P002");
        p.setProductName("Another Item");
        p.setBuyingPrice(BigDecimal.TEN);
        p.setSellingPrice(BigDecimal.valueOf(20));
        p.setStockQuantity(10);
        products.add(p);

        byte[] excelData = reportService.exportProductsToExcel(products, "Test Excel Report");
        assertNotNull(excelData);
        assertTrue(excelData.length > 0);
    }

    @Test
    public void testReportDTO_ValuationCalculations() {
        ReportDTO report = new ReportDTO();
        report.setTotalInventoryValue(BigDecimal.valueOf(100));
        report.setTotalSellingValue(BigDecimal.valueOf(150));
        report.setTotalPotentialProfit(BigDecimal.valueOf(50));

        assertEquals(0, report.getTotalInventoryValue().compareTo(BigDecimal.valueOf(100)));
        assertEquals(0, report.getTotalSellingValue().compareTo(BigDecimal.valueOf(150)));
        assertEquals(0, report.getTotalPotentialProfit().compareTo(BigDecimal.valueOf(50)));
    }
}
