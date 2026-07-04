package com.smartinventory.dao;

import com.smartinventory.model.InventoryTransaction;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Access Object interface for InventoryTransaction entity.
 * Defines all database operations for inventory transaction management.
 */
public interface InventoryTransactionDAO {
    
    /**
     * Finds a transaction by ID.
     * 
     * @param transactionId the transaction ID
     * @return the InventoryTransaction object if found, null otherwise
     */
    InventoryTransaction findById(int transactionId);
    
    /**
     * Retrieves all transactions.
     * 
     * @return list of all transactions
     */
    List<InventoryTransaction> findAll();
    
    /**
     * Retrieves transactions for a specific product.
     * 
     * @param productId the product ID
     * @return list of transactions for the product
     */
    List<InventoryTransaction> findByProduct(int productId);
    
    /**
     * Retrieves transactions by type.
     * 
     * @param transactionType the transaction type (STOCK_IN, STOCK_OUT, etc.)
     * @return list of transactions of the specified type
     */
    List<InventoryTransaction> findByType(String transactionType);
    
    /**
     * Retrieves transactions within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of transactions within the date range
     */
    List<InventoryTransaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Retrieves recent transactions.
     * 
     * @param limit the maximum number of transactions to return
     * @return list of recent transactions
     */
    List<InventoryTransaction> findRecent(int limit);
    
    /**
     * Creates a new transaction.
     * 
     * @param transaction the transaction to create
     * @return the generated transaction ID
     */
    int create(InventoryTransaction transaction);
    
    /**
     * Counts total number of transactions.
     * 
     * @return total count
     */
    int count();
    
    /**
     * Counts transactions by type.
     * 
     * @param transactionType the transaction type
     * @return count of transactions of the specified type
     */
    int countByType(String transactionType);
}
