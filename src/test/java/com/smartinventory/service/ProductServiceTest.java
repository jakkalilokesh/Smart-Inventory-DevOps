package com.smartinventory.service;

import com.smartinventory.dao.ProductDAO;
import com.smartinventory.model.Product;
import com.smartinventory.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ProductService class.
 */
public class ProductServiceTest {

    private ProductServiceImpl productService;

    @Mock
    private ProductDAO productDAO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductServiceImpl();
        productService.setProductDAO(productDAO);
        
        // Mock default behavior for happy paths
        when(productDAO.skuExists(anyString())).thenReturn(false);
        when(productDAO.barcodeExists(anyString())).thenReturn(false);
        when(productDAO.create(any(Product.class))).thenReturn(1);
    }

    @Test
    public void testCreateProduct_ValidProduct_ReturnsCreatedProduct() {
        Product product = new Product();
        product.setSku("SKU001");
        product.setProductName("Test Product");
        product.setCategoryId(1);
        product.setBuyingPrice(new BigDecimal("10.00"));
        product.setSellingPrice(new BigDecimal("15.00"));
        product.setStockQuantity(100);
        product.setMinimumStock(10);
        product.setReorderLevel(20);
        product.setStatus("ACTIVE");
        
        Product created = productService.create(product);
        assertNotNull(created);
        assertEquals("SKU001", created.getSku());
        assertEquals("Test Product", created.getProductName());
    }

    @Test
    public void testValidateProduct_InvalidPrice_ThrowsException() {
        Product product = new Product();
        product.setSku("SKU001");
        product.setProductName("Test Product");
        product.setCategoryId(1);
        product.setBuyingPrice(new BigDecimal("100.00"));
        product.setSellingPrice(new BigDecimal("50.00"));
        product.setStockQuantity(100);
        product.setMinimumStock(10);
        product.setReorderLevel(20);
        product.setStatus("ACTIVE");
        
        assertThrows(IllegalArgumentException.class, () -> {
            productService.create(product);
        });
    }

    @Test
    public void testValidateProduct_NegativeStock_ThrowsException() {
        Product product = new Product();
        product.setSku("SKU001");
        product.setProductName("Test Product");
        product.setCategoryId(1);
        product.setBuyingPrice(new BigDecimal("10.00"));
        product.setSellingPrice(new BigDecimal("15.00"));
        product.setStockQuantity(-10);
        product.setMinimumStock(10);
        product.setReorderLevel(20);
        product.setStatus("ACTIVE");
        
        assertThrows(IllegalArgumentException.class, () -> {
            productService.create(product);
        });
    }

    @Test
    public void testValidateProduct_ZeroSellingPrice_ThrowsException() {
        Product product = new Product();
        product.setSku("SKU001");
        product.setProductName("Test Product");
        product.setCategoryId(1);
        product.setBuyingPrice(new BigDecimal("10.00"));
        product.setSellingPrice(BigDecimal.ZERO);
        product.setStockQuantity(100);
        product.setMinimumStock(10);
        product.setReorderLevel(20);
        product.setStatus("ACTIVE");
        
        assertThrows(IllegalArgumentException.class, () -> {
            productService.create(product);
        });
    }
}
