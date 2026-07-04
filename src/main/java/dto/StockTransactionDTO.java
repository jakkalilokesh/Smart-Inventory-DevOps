package com.smartinventory.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object for stock transactions.
 * Used to transfer stock movement data between layers.
 */
public class StockTransactionDTO {
    private int productId;
    private String transactionType;
    private int quantity;
    private BigDecimal unitPrice;
    private String referenceNumber;
    private String notes;
    private int performedBy;

    public StockTransactionDTO() {
    }

    public StockTransactionDTO(int productId, String transactionType, int quantity, int performedBy) {
        this.productId = productId;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.performedBy = performedBy;
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
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
}
