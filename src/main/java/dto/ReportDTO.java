package com.smartinventory.dto;

import com.smartinventory.model.InventoryTransaction;
import com.smartinventory.model.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for report generation.
 * Aggregates data for various report types including inventory,
 * transactions, valuation, and audit trail reports.
 */
public class ReportDTO {
    
    // Report metadata
    private String reportType;
    private String reportTitle;
    private String generatedBy;
    private LocalDateTime generatedAt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    // Inventory summary
    private int totalProducts;
    private int activeProducts;
    private int lowStockProducts;
    private int outOfStockProducts;
    private BigDecimal totalInventoryValue;
    private BigDecimal totalSellingValue;
    private BigDecimal totalPotentialProfit;
    
    // Transaction summary
    private int totalTransactions;
    private int stockInCount;
    private int stockOutCount;
    private int adjustmentCount;
    private BigDecimal totalStockInValue;
    private BigDecimal totalStockOutValue;
    
    // Data lists
    private List<Product> products;
    private List<Product> lowStockList;
    private List<InventoryTransaction> transactions;
    
    // Category breakdown
    private Map<String, Integer> categoryDistribution;
    private Map<String, BigDecimal> categoryValues;
    
    // Supplier breakdown
    private Map<String, Integer> supplierDistribution;
    
    public ReportDTO() {
        this.generatedAt = LocalDateTime.now();
        this.totalInventoryValue = BigDecimal.ZERO;
        this.totalSellingValue = BigDecimal.ZERO;
        this.totalPotentialProfit = BigDecimal.ZERO;
        this.totalStockInValue = BigDecimal.ZERO;
        this.totalStockOutValue = BigDecimal.ZERO;
    }

    // Getters and Setters
    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getActiveProducts() {
        return activeProducts;
    }

    public void setActiveProducts(int activeProducts) {
        this.activeProducts = activeProducts;
    }

    public int getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(int lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public int getOutOfStockProducts() {
        return outOfStockProducts;
    }

    public void setOutOfStockProducts(int outOfStockProducts) {
        this.outOfStockProducts = outOfStockProducts;
    }

    public BigDecimal getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public void setTotalInventoryValue(BigDecimal totalInventoryValue) {
        this.totalInventoryValue = totalInventoryValue;
    }

    public BigDecimal getTotalSellingValue() {
        return totalSellingValue;
    }

    public void setTotalSellingValue(BigDecimal totalSellingValue) {
        this.totalSellingValue = totalSellingValue;
    }

    public BigDecimal getTotalPotentialProfit() {
        return totalPotentialProfit;
    }

    public void setTotalPotentialProfit(BigDecimal totalPotentialProfit) {
        this.totalPotentialProfit = totalPotentialProfit;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public int getStockInCount() {
        return stockInCount;
    }

    public void setStockInCount(int stockInCount) {
        this.stockInCount = stockInCount;
    }

    public int getStockOutCount() {
        return stockOutCount;
    }

    public void setStockOutCount(int stockOutCount) {
        this.stockOutCount = stockOutCount;
    }

    public int getAdjustmentCount() {
        return adjustmentCount;
    }

    public void setAdjustmentCount(int adjustmentCount) {
        this.adjustmentCount = adjustmentCount;
    }

    public BigDecimal getTotalStockInValue() {
        return totalStockInValue;
    }

    public void setTotalStockInValue(BigDecimal totalStockInValue) {
        this.totalStockInValue = totalStockInValue;
    }

    public BigDecimal getTotalStockOutValue() {
        return totalStockOutValue;
    }

    public void setTotalStockOutValue(BigDecimal totalStockOutValue) {
        this.totalStockOutValue = totalStockOutValue;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Product> getLowStockList() {
        return lowStockList;
    }

    public void setLowStockList(List<Product> lowStockList) {
        this.lowStockList = lowStockList;
    }

    public List<InventoryTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<InventoryTransaction> transactions) {
        this.transactions = transactions;
    }

    public Map<String, Integer> getCategoryDistribution() {
        return categoryDistribution;
    }

    public void setCategoryDistribution(Map<String, Integer> categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }

    public Map<String, BigDecimal> getCategoryValues() {
        return categoryValues;
    }

    public void setCategoryValues(Map<String, BigDecimal> categoryValues) {
        this.categoryValues = categoryValues;
    }

    public Map<String, Integer> getSupplierDistribution() {
        return supplierDistribution;
    }

    public void setSupplierDistribution(Map<String, Integer> supplierDistribution) {
        this.supplierDistribution = supplierDistribution;
    }
}
