package com.smartinventory.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for dashboard statistics.
 * Aggregates key metrics for the dashboard view.
 */
public class DashboardDTO {
    private int totalProducts;
    private int totalCategories;
    private int totalSuppliers;
    private int lowStockProducts;
    private BigDecimal totalInventoryValue;
    private BigDecimal totalPotentialProfit;
    private List<com.smartinventory.model.ActivityLog> recentActivities;
    private List<com.smartinventory.model.Product> lowStockList;

    public DashboardDTO() {
    }

    // Getters and Setters
    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(int totalCategories) {
        this.totalCategories = totalCategories;
    }

    public int getTotalSuppliers() {
        return totalSuppliers;
    }

    public void setTotalSuppliers(int totalSuppliers) {
        this.totalSuppliers = totalSuppliers;
    }

    public int getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(int lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public BigDecimal getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public void setTotalInventoryValue(BigDecimal totalInventoryValue) {
        this.totalInventoryValue = totalInventoryValue;
    }

    public BigDecimal getTotalPotentialProfit() {
        return totalPotentialProfit;
    }

    public void setTotalPotentialProfit(BigDecimal totalPotentialProfit) {
        this.totalPotentialProfit = totalPotentialProfit;
    }

    public List<com.smartinventory.model.ActivityLog> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<com.smartinventory.model.ActivityLog> recentActivities) {
        this.recentActivities = recentActivities;
    }

    public List<com.smartinventory.model.Product> getLowStockList() {
        return lowStockList;
    }

    public void setLowStockList(List<com.smartinventory.model.Product> lowStockList) {
        this.lowStockList = lowStockList;
    }
}
