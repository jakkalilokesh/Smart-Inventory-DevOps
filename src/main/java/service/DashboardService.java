package com.smartinventory.service;

import com.smartinventory.dto.DashboardDTO;

/**
 * Service interface for Dashboard operations.
 * Contains business logic for dashboard statistics and analytics.
 */
public interface DashboardService {
    
    /**
     * Retrieves dashboard statistics and data.
     * 
     * @return DashboardDTO containing all dashboard information
     */
    DashboardDTO getDashboardData();
    
    /**
     * Gets total number of products.
     * 
     * @return total product count
     */
    int getTotalProducts();
    
    /**
     * Gets total number of categories.
     * 
     * @return total category count
     */
    int getTotalCategories();
    
    /**
     * Gets total number of suppliers.
     * 
     * @return total supplier count
     */
    int getTotalSuppliers();
    
    /**
     * Gets number of products with low stock.
     * 
     * @return low stock product count
     */
    int getLowStockCount();
    
    /**
     * Gets total inventory value (based on buying price).
     * 
     * @return total inventory value
     */
    java.math.BigDecimal getTotalInventoryValue();
    
    /**
     * Gets total potential profit (selling price - buying price).
     * 
     * @return total potential profit
     */
    java.math.BigDecimal getTotalPotentialProfit();
}
