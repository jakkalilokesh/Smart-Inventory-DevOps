package com.smartinventory.service;

import com.smartinventory.model.Category;
import com.smartinventory.model.Product;
import com.smartinventory.model.Supplier;

import java.io.InputStream;
import java.util.List;

/**
 * Service interface for data export and import operations.
 * Supports CSV, JSON, and Excel formats for bulk data transfer.
 */
public interface DataExportImportService {

    /**
     * Exports all products to CSV format.
     *
     * @return byte array containing CSV data
     */
    byte[] exportProductsToCsv();

    /**
     * Exports all products to JSON format.
     *
     * @return byte array containing JSON data
     */
    byte[] exportProductsToJson();

    /**
     * Exports all suppliers to CSV format.
     *
     * @return byte array containing CSV data
     */
    byte[] exportSuppliersToCsv();

    /**
     * Exports all suppliers to JSON format.
     *
     * @return byte array containing JSON data
     */
    byte[] exportSuppliersToJson();

    /**
     * Exports all categories to CSV format.
     *
     * @return byte array containing CSV data
     */
    byte[] exportCategoriesToCsv();

    /**
     * Exports all categories to JSON format.
     *
     * @return byte array containing JSON data
     */
    byte[] exportCategoriesToJson();

    /**
     * Imports products from CSV file.
     *
     * @param inputStream the input stream containing CSV data
     * @return number of records imported
     * @throws Exception if import fails
     */
    int importProductsFromCsv(InputStream inputStream) throws Exception;

    /**
     * Imports products from JSON file.
     *
     * @param inputStream the input stream containing JSON data
     * @return number of records imported
     * @throws Exception if import fails
     */
    int importProductsFromJson(InputStream inputStream) throws Exception;

    /**
     * Imports suppliers from CSV file.
     *
     * @param inputStream the input stream containing CSV data
     * @return number of records imported
     * @throws Exception if import fails
     */
    int importSuppliersFromCsv(InputStream inputStream) throws Exception;

    /**
     * Imports categories from CSV file.
     *
     * @param inputStream the input stream containing CSV data
     * @return number of records imported
     * @throws Exception if import fails
     */
    int importCategoriesFromCsv(InputStream inputStream) throws Exception;

    /**
     * Validates CSV data before import.
     *
     * @param inputStream the input stream
     * @param entityType  the entity type (PRODUCT, SUPPLIER, CATEGORY)
     * @return list of validation error messages, empty if valid
     */
    List<String> validateCsvData(InputStream inputStream, String entityType);

    /**
     * Generates a CSV template for the given entity type.
     *
     * @param entityType the entity type
     * @return byte array containing the template CSV
     */
    byte[] generateCsvTemplate(String entityType);
}
