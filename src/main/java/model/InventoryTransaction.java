package com.smartinventory.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * InventoryTransaction entity representing the inventory_transactions table in the database.
 * Records all stock movements (STOCK_IN, STOCK_OUT, ADJUSTMENT, TRANSFER).
 */
public class InventoryTransaction {
    private int transactionId;
    private String transactionType;
    private int productId;
    private String productName;
    private String sku;
    private int quantity;
    private int previousQuantity;
    private int newQuantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String referenceNumber;
    private String notes;
    private int performedBy;
    private String performedByName;
    private LocalDateTime transactionDate;

    public InventoryTransaction() {
    }

    public InventoryTransaction(String transactionType, int productId, int quantity, int performedBy) {
        this.transactionType = transactionType;
        this.productId = productId;
        this.quantity = quantity;
        this.performedBy = performedBy;
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPreviousQuantity() {
        return previousQuantity;
    }

    public void setPreviousQuantity(int previousQuantity) {
        this.previousQuantity = previousQuantity;
    }

    public int getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(int newQuantity) {
        this.newQuantity = newQuantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(int performedBy) {
        this.performedBy = performedBy;
    }

    public String getPerformedByName() {
        return performedByName;
    }

    public void setPerformedByName(String performedByName) {
        this.performedByName = performedByName;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionTypeDisplay() {
        switch (transactionType) {
            case "STOCK_IN":
                return "Stock In";
            case "STOCK_OUT":
                return "Stock Out";
            case "ADJUSTMENT":
                return "Adjustment";
            case "TRANSFER":
                return "Transfer";
            default:
                return transactionType;
        }
    }

    @Override
    public String toString() {
        return "InventoryTransaction{" +
                "transactionId=" + transactionId +
                ", transactionType='" + transactionType + '\'' +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", previousQuantity=" + previousQuantity +
                ", newQuantity=" + newQuantity +
                ", performedBy=" + performedBy +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
