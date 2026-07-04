package com.smartinventory.service.impl;

import com.smartinventory.dao.ActivityLogDAO;
import com.smartinventory.dao.CategoryDAO;
import com.smartinventory.dao.ProductDAO;
import com.smartinventory.dao.SupplierDAO;
import com.smartinventory.dao.impl.ActivityLogDAOImpl;
import com.smartinventory.dao.impl.CategoryDAOImpl;
import com.smartinventory.dao.impl.ProductDAOImpl;
import com.smartinventory.dao.impl.SupplierDAOImpl;
import com.smartinventory.dto.DashboardDTO;
import com.smartinventory.model.ActivityLog;
import com.smartinventory.model.Product;
import com.smartinventory.service.DashboardService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of DashboardService interface.
 * Contains business logic for dashboard statistics and analytics.
 */
public class DashboardServiceImpl implements DashboardService {
    private static final Logger logger = LogManager.getLogger(DashboardServiceImpl.class);
    private ProductDAO productDAO = new ProductDAOImpl();
    private CategoryDAO categoryDAO = new CategoryDAOImpl();
    private SupplierDAO supplierDAO = new SupplierDAOImpl();
    private ActivityLogDAO activityLogDAO = new ActivityLogDAOImpl();

    @Override
    public DashboardDTO getDashboardData() {
        DashboardDTO dashboard = new DashboardDTO();
        
        try {
            dashboard.setTotalProducts(getTotalProducts());
            dashboard.setTotalCategories(getTotalCategories());
            dashboard.setTotalSuppliers(getTotalSuppliers());
            dashboard.setLowStockProducts(getLowStockCount());
            dashboard.setTotalInventoryValue(getTotalInventoryValue());
            dashboard.setTotalPotentialProfit(getTotalPotentialProfit());
            dashboard.setRecentActivities(getRecentActivities(10));
            dashboard.setLowStockList(getLowStockProducts());
            
            logger.info("Dashboard data retrieved successfully");
        } catch (Exception e) {
            logger.error("Error retrieving dashboard data", e);
        }
        
        return dashboard;
    }

    @Override
    public int getTotalProducts() {
        return productDAO.count();
    }

    @Override
    public int getTotalCategories() {
        return categoryDAO.count();
    }

    @Override
    public int getTotalSuppliers() {
        return supplierDAO.count();
    }

    @Override
    public int getLowStockCount() {
        return productDAO.countLowStock();
    }

    @Override
    public BigDecimal getTotalInventoryValue() {
        List<Product> products = productDAO.findActive();
        BigDecimal totalValue = BigDecimal.ZERO;
        
        for (Product product : products) {
            totalValue = totalValue.add(product.getTotalBuyingValue());
        }
        
        return totalValue;
    }

    @Override
    public BigDecimal getTotalPotentialProfit() {
        List<Product> products = productDAO.findActive();
        BigDecimal totalProfit = BigDecimal.ZERO;
        
        for (Product product : products) {
            totalProfit = totalProfit.add(product.getPotentialProfit());
        }
        
        return totalProfit;
    }
    
    /**
     * Helper method to get recent activities.
     */
    private List<ActivityLog> getRecentActivities(int limit) {
        return activityLogDAO.findRecent(limit);
    }
    
    /**
     * Helper method to get low stock products.
     */
    private List<Product> getLowStockProducts() {
        return productDAO.findLowStock();
    }
}
