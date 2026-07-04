package com.smartinventory.service;

import com.smartinventory.service.impl.DataExportImportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataExportImportServiceTest {

    private DataExportImportService dataExportImportService;

    @BeforeEach
    public void setUp() {
        dataExportImportService = new DataExportImportServiceImpl();
    }

    @Test
    public void testGenerateCsvTemplate_Product() {
        byte[] template = dataExportImportService.generateCsvTemplate("PRODUCT");
        assertNotNull(template);
        String csvContent = new String(template, StandardCharsets.UTF_8);
        assertTrue(csvContent.contains("SKU"));
        assertTrue(csvContent.contains("Barcode"));
        assertTrue(csvContent.contains("Buying Price"));
    }

    @Test
    public void testGenerateCsvTemplate_Supplier() {
        byte[] template = dataExportImportService.generateCsvTemplate("SUPPLIER");
        assertNotNull(template);
        String csvContent = new String(template, StandardCharsets.UTF_8);
        assertTrue(csvContent.contains("Supplier Name"));
        assertTrue(csvContent.contains("Contact Person"));
    }

    @Test
    public void testValidateCsvData_InvalidProductPrice() {
        String invalidCsv = "SKU,Barcode,Product Name,Description,Category,Supplier,Buying Price,Selling Price,Quantity,Min Stock,Status\n" +
                "SKU-1,12345,Item 1,Desc,Cat,Sup,abc,xyz,10,5,ACTIVE\n";
        ByteArrayInputStream is = new ByteArrayInputStream(invalidCsv.getBytes(StandardCharsets.UTF_8));
        List<String> errors = dataExportImportService.validateCsvData(is, "PRODUCT");
        assertNotNull(errors);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(err -> err.contains("Invalid Buying Price")));
    }

    @Test
    public void testValidateCsvData_ValidProduct() {
        String validCsv = "SKU,Barcode,Product Name,Description,Category,Supplier,Buying Price,Selling Price,Quantity,Min Stock,Status\n" +
                "SKU-1,12345,Item 1,Desc,Cat,Sup,10.00,15.00,10,5,ACTIVE\n";
        ByteArrayInputStream is = new ByteArrayInputStream(validCsv.getBytes(StandardCharsets.UTF_8));
        List<String> errors = dataExportImportService.validateCsvData(is, "PRODUCT");
        assertNotNull(errors);
        assertTrue(errors.isEmpty());
    }
}
