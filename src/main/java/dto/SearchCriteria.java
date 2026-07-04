package com.smartinventory.dto;

/**
 * Data Transfer Object for search criteria.
 * Used to transfer search parameters between layers.
 */
public class SearchCriteria {
    private String keyword;
    private String category;
    private String supplier;
    private String status;
    private String role;
    private String sortBy;
    private String sortOrder;
    private int page;
    private int pageSize;

    public SearchCriteria() {
        this.page = 1;
        this.pageSize = 10;
        this.sortBy = "created_at";
        this.sortOrder = "DESC";
    }

    // Getters and Setters
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getOffset() {
        return (page - 1) * pageSize;
    }
}
