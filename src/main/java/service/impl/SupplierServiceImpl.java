package com.smartinventory.service.impl;

import com.smartinventory.dao.SupplierDAO;
import com.smartinventory.dao.impl.SupplierDAOImpl;
import com.smartinventory.dto.SearchCriteria;
import com.smartinventory.dto.PaginationDTO;
import com.smartinventory.model.Supplier;
import com.smartinventory.service.SupplierService;
import com.smartinventory.util.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Implementation of SupplierService interface.
 * Contains business logic for supplier management operations.
 */
public class SupplierServiceImpl implements SupplierService {
    private static final Logger logger = LogManager.getLogger(SupplierServiceImpl.class);
    private SupplierDAO supplierDAO = new SupplierDAOImpl();

    @Override
    public Supplier findById(int supplierId) {
        return supplierDAO.findById(supplierId);
    }

    @Override
    public Supplier findByName(String supplierName) {
        return supplierDAO.findByName(supplierName);
    }

    @Override
    public List<Supplier> findAll() {
        return supplierDAO.findAll();
    }

    @Override
    public List<Supplier> findActive() {
        return supplierDAO.findActive();
    }

    @Override
    public PaginationDTO<Supplier> findWithPagination(SearchCriteria criteria) {
        return supplierDAO.findWithPagination(criteria);
    }

    @Override
    public List<Supplier> search(String keyword) {
        return supplierDAO.search(keyword);
    }

    @Override
    public Supplier create(Supplier supplier) throws IllegalArgumentException {
        if (!validate(supplier)) {
            throw new IllegalArgumentException("Invalid supplier data");
        }
        
        if (supplierDAO.supplierExists(supplier.getSupplierName())) {
            throw new IllegalArgumentException("Supplier name already exists");
        }
        
        int supplierId = supplierDAO.create(supplier);
        if (supplierId > 0) {
            supplier.setSupplierId(supplierId);
            logger.info("Supplier created successfully: {}", supplier.getSupplierName());
            return supplier;
        }
        
        throw new IllegalArgumentException("Failed to create supplier");
    }

    @Override
    public boolean update(Supplier supplier) throws IllegalArgumentException {
        if (!validate(supplier)) {
            throw new IllegalArgumentException("Invalid supplier data");
        }
        
        Supplier existingSupplier = supplierDAO.findById(supplier.getSupplierId());
        if (existingSupplier == null) {
            throw new IllegalArgumentException("Supplier not found");
        }
        
        // Check if supplier name is being changed and if it already exists
        if (!existingSupplier.getSupplierName().equals(supplier.getSupplierName()) && 
            supplierDAO.supplierExists(supplier.getSupplierName())) {
            throw new IllegalArgumentException("Supplier name already exists");
        }
        
        return supplierDAO.update(supplier);
    }

    @Override
    public boolean delete(int supplierId) {
        Supplier supplier = supplierDAO.findById(supplierId);
        if (supplier == null) {
            logger.warn("Cannot delete non-existent supplier: {}", supplierId);
            return false;
        }
        
        // Check if supplier has associated products
        // This would require additional DAO method, for now we'll proceed
        // In production, you should check for dependent records
        
        return supplierDAO.delete(supplierId);
    }

    @Override
    public boolean validate(Supplier supplier) {
        if (supplier == null) {
            return false;
        }
        
        // Validate supplier name
        if (!ValidationUtil.isNotEmpty(supplier.getSupplierName())) {
            logger.warn("Invalid supplier name");
            return false;
        }
        
        // Validate email if provided
        if (supplier.getEmail() != null && !supplier.getEmail().isEmpty()) {
            if (!ValidationUtil.isValidEmail(supplier.getEmail())) {
                logger.warn("Invalid email: {}", supplier.getEmail());
                return false;
            }
        }
        
        // Validate phone if provided
        if (supplier.getPhone() != null && !supplier.getPhone().isEmpty()) {
            if (!ValidationUtil.isValidPhone(supplier.getPhone())) {
                logger.warn("Invalid phone: {}", supplier.getPhone());
                return false;
            }
        }
        
        // Validate status
        if (supplier.getStatus() == null || 
            (!supplier.getStatus().equals("ACTIVE") && 
             !supplier.getStatus().equals("INACTIVE"))) {
            logger.warn("Invalid status: {}", supplier.getStatus());
            return false;
        }
        
        return true;
    }
}
