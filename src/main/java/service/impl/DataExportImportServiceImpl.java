package com.smartinventory.service.impl;

import com.smartinventory.dao.*;
import com.smartinventory.dao.impl.*;
import com.smartinventory.model.Category;
import com.smartinventory.model.Product;
import com.smartinventory.model.Supplier;
import com.smartinventory.service.DataExportImportService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataExportImportServiceImpl implements DataExportImportService {
    private static final Logger logger = LogManager.getLogger(DataExportImportServiceImpl.class);

    private final ProductDAO productDAO = new ProductDAOImpl();
    private final SupplierDAO supplierDAO = new SupplierDAOImpl();
    private final CategoryDAO categoryDAO = new CategoryDAOImpl();

    @Override
    public byte[] exportProductsToCsv() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            writer.println("SKU,Barcode,Product Name,Description,Category,Supplier,Buying Price,Selling Price,Quantity,Min Stock,Status");
            List<Product> products = productDAO.findAll();
            for (Product p : products) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%s,%s,%d,%d,\"%s\"%n",
                        escapeCsv(p.getSku()),
                        escapeCsv(p.getBarcode()),
                        escapeCsv(p.getProductName()),
                        escapeCsv(p.getDescription()),
                        escapeCsv(p.getCategoryName()),
                        escapeCsv(p.getSupplierName()),
                        p.getBuyingPrice().toString(),
                        p.getSellingPrice().toString(),
                        p.getStockQuantity(),
                        p.getMinimumStock(),
                        p.getStatus()
                );
            }
            writer.flush();
        } catch (Exception e) {
            logger.error("Error exporting products to CSV", e);
        }
        return out.toByteArray();
    }

    @Override
    public byte[] exportProductsToJson() {
        JSONArray arr = new JSONArray();
        List<Product> products = productDAO.findAll();
        for (Product p : products) {
            JSONObject obj = new JSONObject();
            obj.put("sku", p.getSku());
            obj.put("barcode", p.getBarcode());
            obj.put("productName", p.getProductName());
            obj.put("description", p.getDescription());
            obj.put("categoryName", p.getCategoryName());
            obj.put("supplierName", p.getSupplierName());
            obj.put("buyingPrice", p.getBuyingPrice());
            obj.put("sellingPrice", p.getSellingPrice());
            obj.put("stockQuantity", p.getStockQuantity());
            obj.put("minimumStock", p.getMinimumStock());
            obj.put("status", p.getStatus());
            arr.put(obj);
        }
        return arr.toString(4).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] exportSuppliersToCsv() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            writer.println("Supplier Name,Contact Person,Email,Phone,Address,City,State,Country,Postal Code,Tax ID,Status");
            List<Supplier> suppliers = supplierDAO.findAll();
            for (Supplier s : suppliers) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        escapeCsv(s.getSupplierName()),
                        escapeCsv(s.getContactPerson()),
                        escapeCsv(s.getEmail()),
                        escapeCsv(s.getPhone()),
                        escapeCsv(s.getAddress()),
                        escapeCsv(s.getCity()),
                        escapeCsv(s.getState()),
                        escapeCsv(s.getCountry()),
                        escapeCsv(s.getPostalCode()),
                        escapeCsv(s.getTaxId()),
                        s.getStatus()
                );
            }
            writer.flush();
        } catch (Exception e) {
            logger.error("Error exporting suppliers to CSV", e);
        }
        return out.toByteArray();
    }

    @Override
    public byte[] exportSuppliersToJson() {
        JSONArray arr = new JSONArray();
        List<Supplier> suppliers = supplierDAO.findAll();
        for (Supplier s : suppliers) {
            JSONObject obj = new JSONObject();
            obj.put("supplierName", s.getSupplierName());
            obj.put("contactPerson", s.getContactPerson());
            obj.put("email", s.getEmail());
            obj.put("phone", s.getPhone());
            obj.put("address", s.getAddress());
            obj.put("city", s.getCity());
            obj.put("state", s.getState());
            obj.put("country", s.getCountry());
            obj.put("postalCode", s.getPostalCode());
            obj.put("taxId", s.getTaxId());
            obj.put("status", s.getStatus());
            arr.put(obj);
        }
        return arr.toString(4).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] exportCategoriesToCsv() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            writer.println("Category Name,Description,Parent Category,Status");
            List<Category> categories = categoryDAO.findAll();
            for (Category c : categories) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        escapeCsv(c.getCategoryName()),
                        escapeCsv(c.getDescription()),
                        escapeCsv(c.getParentCategoryName()),
                        c.getStatus()
                );
            }
            writer.flush();
        } catch (Exception e) {
            logger.error("Error exporting categories to CSV", e);
        }
        return out.toByteArray();
    }

    @Override
    public byte[] exportCategoriesToJson() {
        JSONArray arr = new JSONArray();
        List<Category> categories = categoryDAO.findAll();
        for (Category c : categories) {
            JSONObject obj = new JSONObject();
            obj.put("categoryName", c.getCategoryName());
            obj.put("description", c.getDescription());
            obj.put("parentCategoryName", c.getParentCategoryName());
            obj.put("status", c.getStatus());
            arr.put(obj);
        }
        return arr.toString(4).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public int importProductsFromCsv(InputStream inputStream) throws Exception {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String header = reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = parseCsvLine(line);
                if (fields.length < 8) {
                    logger.warn("Skipping invalid CSV line: {}", line);
                    continue;
                }

                Product p = new Product();
                p.setSku(fields[0].trim());
                p.setBarcode(fields[1].trim().isEmpty() ? null : fields[1].trim());
                p.setProductName(fields[2].trim());
                p.setDescription(fields[3].trim());
                
                String catName = fields[4].trim();
                Category cat = categoryDAO.findByName(catName);
                if (cat == null && !catName.isEmpty()) {
                    cat = new Category(catName, "Auto-created during import", null);
                    int catId = categoryDAO.create(cat);
                    p.setCategoryId(catId);
                } else if (cat != null) {
                    p.setCategoryId(cat.getCategoryId());
                } else {
                    // Default to first active category or throw error
                    List<Category> cats = categoryDAO.findActive();
                    if (!cats.isEmpty()) {
                        p.setCategoryId(cats.get(0).getCategoryId());
                    } else {
                        throw new Exception("No active category found and none specified in CSV.");
                    }
                }

                String supName = fields[5].trim();
                Supplier sup = supplierDAO.findByName(supName);
                if (sup == null && !supName.isEmpty()) {
                    sup = new Supplier(supName, "Auto-created during import", "", "");
                    int supId = supplierDAO.create(sup);
                    p.setSupplierId(supId);
                } else if (sup != null) {
                    p.setSupplierId(sup.getSupplierId());
                }

                p.setBuyingPrice(new BigDecimal(fields[6].trim()));
                p.setSellingPrice(new BigDecimal(fields[7].trim()));
                p.setStockQuantity(fields.length > 8 && !fields[8].trim().isEmpty() ? Integer.parseInt(fields[8].trim()) : 0);
                p.setMinimumStock(fields.length > 9 && !fields[9].trim().isEmpty() ? Integer.parseInt(fields[9].trim()) : 10);
                p.setReorderLevel(p.getMinimumStock());
                p.setUnit("PCS");
                p.setStatus(fields.length > 10 && !fields[10].trim().isEmpty() ? fields[10].trim() : "ACTIVE");
                
                // If product already exists with SKU, update it, otherwise create
                Product existing = productDAO.findBySku(p.getSku());
                if (existing != null) {
                    p.setProductId(existing.getProductId());
                    productDAO.update(p);
                } else {
                    productDAO.create(p);
                }
                count++;
            }
        }
        return count;
    }

    @Override
    public int importProductsFromJson(InputStream inputStream) throws Exception {
        int count = 0;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        String jsonContent = buffer.toString(StandardCharsets.UTF_8.name());
        JSONArray arr = new JSONArray(jsonContent);

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            Product p = new Product();
            p.setSku(obj.getString("sku"));
            p.setBarcode(obj.has("barcode") && !obj.isNull("barcode") ? obj.getString("barcode") : null);
            p.setProductName(obj.getString("productName"));
            p.setDescription(obj.has("description") ? obj.getString("description") : "");
            
            String catName = obj.has("categoryName") ? obj.getString("categoryName") : "";
            Category cat = categoryDAO.findByName(catName);
            if (cat == null && !catName.isEmpty()) {
                cat = new Category(catName, "Auto-created during import", null);
                int catId = categoryDAO.create(cat);
                p.setCategoryId(catId);
            } else if (cat != null) {
                p.setCategoryId(cat.getCategoryId());
            } else {
                List<Category> cats = categoryDAO.findActive();
                if (!cats.isEmpty()) {
                    p.setCategoryId(cats.get(0).getCategoryId());
                } else {
                    throw new Exception("No active category found.");
                }
            }

            String supName = obj.has("supplierName") ? obj.getString("supplierName") : "";
            Supplier sup = supplierDAO.findByName(supName);
            if (sup == null && !supName.isEmpty()) {
                sup = new Supplier(supName, "Auto-created during import", "", "");
                int supId = supplierDAO.create(sup);
                p.setSupplierId(supId);
            } else if (sup != null) {
                p.setSupplierId(sup.getSupplierId());
            }

            p.setBuyingPrice(obj.getBigDecimal("buyingPrice"));
            p.setSellingPrice(obj.getBigDecimal("sellingPrice"));
            p.setStockQuantity(obj.has("stockQuantity") ? obj.getInt("stockQuantity") : 0);
            p.setMinimumStock(obj.has("minimumStock") ? obj.getInt("minimumStock") : 10);
            p.setReorderLevel(p.getMinimumStock());
            p.setUnit("PCS");
            p.setStatus(obj.has("status") ? obj.getString("status") : "ACTIVE");

            Product existing = productDAO.findBySku(p.getSku());
            if (existing != null) {
                p.setProductId(existing.getProductId());
                productDAO.update(p);
            } else {
                productDAO.create(p);
            }
            count++;
        }
        return count;
    }

    @Override
    public int importSuppliersFromCsv(InputStream inputStream) throws Exception {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = parseCsvLine(line);
                if (fields.length < 4) continue;

                Supplier s = new Supplier();
                s.setSupplierName(fields[0].trim());
                s.setContactPerson(fields[1].trim());
                s.setEmail(fields[2].trim());
                s.setPhone(fields[3].trim());
                s.setAddress(fields.length > 4 ? fields[4].trim() : "");
                s.setCity(fields.length > 5 ? fields[5].trim() : "");
                s.setState(fields.length > 6 ? fields[6].trim() : "");
                s.setCountry(fields.length > 7 ? fields[7].trim() : "");
                s.setPostalCode(fields.length > 8 ? fields[8].trim() : "");
                s.setTaxId(fields.length > 9 ? fields[9].trim() : "");
                s.setStatus(fields.length > 10 ? fields[10].trim() : "ACTIVE");

                Supplier existing = supplierDAO.findByName(s.getSupplierName());
                if (existing != null) {
                    s.setSupplierId(existing.getSupplierId());
                    supplierDAO.update(s);
                } else {
                    supplierDAO.create(s);
                }
                count++;
            }
        }
        return count;
    }

    @Override
    public int importCategoriesFromCsv(InputStream inputStream) throws Exception {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = parseCsvLine(line);
                if (fields.length < 1) continue;

                Category c = new Category();
                c.setCategoryName(fields[0].trim());
                c.setDescription(fields.length > 1 ? fields[1].trim() : "");
                String parentName = fields.length > 2 ? fields[2].trim() : "";
                if (!parentName.isEmpty()) {
                    Category parent = categoryDAO.findByName(parentName);
                    if (parent != null) {
                        c.setParentCategoryId(parent.getCategoryId());
                    }
                }
                c.setStatus(fields.length > 3 ? fields[3].trim() : "ACTIVE");

                Category existing = categoryDAO.findByName(c.getCategoryName());
                if (existing != null) {
                    c.setCategoryId(existing.getCategoryId());
                    categoryDAO.update(c);
                } else {
                    categoryDAO.create(c);
                }
                count++;
            }
        }
        return count;
    }

    @Override
    public List<String> validateCsvData(InputStream inputStream, String entityType) {
        List<String> errors = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (header == null) {
                errors.add("CSV file is empty.");
                return errors;
            }

            int lineNum = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (line.trim().isEmpty()) continue;
                String[] fields = parseCsvLine(line);

                if ("PRODUCT".equals(entityType)) {
                    if (fields.length < 8) {
                        errors.add("Line " + lineNum + ": Product requires at least SKU, Barcode, Name, Description, Category, Supplier, Buying Price, Selling Price.");
                        continue;
                    }
                    if (fields[0].trim().isEmpty()) {
                        errors.add("Line " + lineNum + ": SKU is required.");
                    }
                    if (fields[2].trim().isEmpty()) {
                        errors.add("Line " + lineNum + ": Product Name is required.");
                    }
                    try {
                        new BigDecimal(fields[6].trim());
                    } catch (Exception e) {
                        errors.add("Line " + lineNum + ": Invalid Buying Price.");
                    }
                    try {
                        new BigDecimal(fields[7].trim());
                    } catch (Exception e) {
                        errors.add("Line " + lineNum + ": Invalid Selling Price.");
                    }
                } else if ("SUPPLIER".equals(entityType)) {
                    if (fields.length < 1 || fields[0].trim().isEmpty()) {
                        errors.add("Line " + lineNum + ": Supplier Name is required.");
                    }
                } else if ("CATEGORY".equals(entityType)) {
                    if (fields.length < 1 || fields[0].trim().isEmpty()) {
                        errors.add("Line " + lineNum + ": Category Name is required.");
                    }
                }
            }
        } catch (Exception e) {
            errors.add("Error reading CSV file: " + e.getMessage());
        }
        return errors;
    }

    @Override
    public byte[] generateCsvTemplate(String entityType) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            if ("PRODUCT".equals(entityType)) {
                writer.println("SKU,Barcode,Product Name,Description,Category,Supplier,Buying Price,Selling Price,Quantity,Min Stock,Status");
                writer.println("LAP-999,1234567890,Example Laptop,High-end device,Computers,TechSupply Corp,800.00,1200.00,50,10,ACTIVE");
            } else if ("SUPPLIER".equals(entityType)) {
                writer.println("Supplier Name,Contact Person,Email,Phone,Address,City,State,Country,Postal Code,Tax ID,Status");
                writer.println("TechSupply Corp,John Doe,john@tech.com,+1234567,123 Main St,San Jose,CA,USA,95112,TX-99,ACTIVE");
            } else if ("CATEGORY".equals(entityType)) {
                writer.println("Category Name,Description,Parent Category,Status");
                writer.println("Computers,Laptops and desktops,Electronics,ACTIVE");
            }
            writer.flush();
        } catch (Exception e) {
            logger.error("Error creating CSV template", e);
        }
        return out.toByteArray();
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        return val.replace("\"", "\"\"");
    }

    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        values.add(sb.toString());
        return values.toArray(new String[0]);
    }
}
