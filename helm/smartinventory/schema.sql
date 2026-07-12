-- SmartInventory Database Schema
-- MySQL 8.0
-- Enterprise Inventory Management System

-- Drop database if exists
DROP DATABASE IF EXISTS smartinventory;

-- Create database
CREATE DATABASE smartinventory
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Use database
USE smartinventory;

-- ============================================
-- TABLES
-- ============================================

-- Roles table
CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Users table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role_id INT NOT NULL,
    phone VARCHAR(20),
    status ENUM('ACTIVE', 'INACTIVE', 'LOCKED') DEFAULT 'ACTIVE',
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Categories table
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_category_id INT NULL,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_category_id) REFERENCES categories(category_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_category_name (category_name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Suppliers table
CREATE TABLE suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    city VARCHAR(50),
    state VARCHAR(50),
    country VARCHAR(50),
    postal_code VARCHAR(20),
    tax_id VARCHAR(50),
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_supplier_name (supplier_name),
    INDEX idx_status (status),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Products table
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    barcode VARCHAR(50) UNIQUE,
    product_name VARCHAR(200) NOT NULL,
    description TEXT,
    category_id INT NOT NULL,
    supplier_id INT,
    buying_price DECIMAL(10, 2) NOT NULL,
    selling_price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    minimum_stock INT NOT NULL DEFAULT 10,
    maximum_stock INT,
    reorder_level INT DEFAULT 10,
    unit VARCHAR(20) DEFAULT 'PCS',
    weight DECIMAL(10, 2),
    dimensions VARCHAR(50),
    image_path VARCHAR(255),
    status ENUM('ACTIVE', 'INACTIVE', 'DISCONTINUED') DEFAULT 'ACTIVE',
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_sku (sku),
    INDEX idx_barcode (barcode),
    INDEX idx_product_name (product_name),
    INDEX idx_category_id (category_id),
    INDEX idx_supplier_id (supplier_id),
    INDEX idx_status (status),
    CONSTRAINT chk_buying_price_positive CHECK (buying_price >= 0),
    CONSTRAINT chk_selling_price_positive CHECK (selling_price >= 0),
    CONSTRAINT chk_stock_quantity_positive CHECK (stock_quantity >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Inventory Transactions table
CREATE TABLE inventory_transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_type ENUM('STOCK_IN', 'STOCK_OUT', 'ADJUSTMENT', 'TRANSFER') NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    previous_quantity INT NOT NULL,
    new_quantity INT NOT NULL,
    unit_price DECIMAL(10, 2),
    total_price DECIMAL(10, 2),
    reference_number VARCHAR(50),
    notes TEXT,
    performed_by INT NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (performed_by) REFERENCES users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_product_id (product_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_performed_by (performed_by),
    CONSTRAINT chk_quantity_positive CHECK (quantity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Activity Logs table
CREATE TABLE activity_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(100) NOT NULL,
    module VARCHAR(50) NOT NULL,
    description TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_module (module),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- SAMPLE DATA
-- ============================================

-- Insert roles
INSERT INTO roles (role_name, description) VALUES
('ADMIN', 'System Administrator with full access'),
('MANAGER', 'Inventory Manager with limited access'),
('STAFF', 'Staff member with basic access');

-- Insert users (passwords are BCrypt hashed)
-- Admin: admin123
-- Manager: manager123
-- Staff: staff123
INSERT INTO users (username, password, email, first_name, last_name, role_id, phone, status) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@smartinventory.com', 'System', 'Administrator', 1, '+1234567890', 'ACTIVE'),
('manager', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'manager@smartinventory.com', 'Inventory', 'Manager', 2, '+1234567891', 'ACTIVE'),
('staff', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'staff@smartinventory.com', 'Staff', 'Member', 3, '+1234567892', 'ACTIVE');

-- Insert categories
INSERT INTO categories (category_name, description, parent_category_id, created_by) VALUES
('Electronics', 'Electronic devices and accessories', NULL, 1),
('Computers', 'Computers and peripherals', 1, 1),
('Mobile Devices', 'Mobile phones and tablets', 1, 1),
('Clothing', 'Apparel and fashion items', NULL, 1),
('Men\'s Clothing', 'Clothing for men', 4, 1),
('Women\'s Clothing', 'Clothing for women', 4, 1),
('Food & Beverages', 'Food items and beverages', NULL, 1),
('Office Supplies', 'Office and stationery items', NULL, 1);

-- Insert suppliers
INSERT INTO suppliers (supplier_name, contact_person, email, phone, address, city, state, country, postal_code, tax_id, created_by) VALUES
('TechSupply Corp', 'John Smith', 'john@techsupply.com', '+1234567890', '123 Tech Street', 'San Francisco', 'CA', 'USA', '94102', 'TX123456', 1),
('FashionHub Inc', 'Jane Doe', 'jane@fashionhub.com', '+1234567891', '456 Fashion Ave', 'New York', 'NY', 'USA', '10001', 'TX789012', 1),
('FoodMart Ltd', 'Bob Johnson', 'bob@foodmart.com', '+1234567892', '789 Food Road', 'Chicago', 'IL', 'USA', '60601', 'TX345678', 1),
('OfficePro Systems', 'Alice Brown', 'alice@officepro.com', '+1234567893', '321 Office Blvd', 'Los Angeles', 'CA', 'USA', '90001', 'TX901234', 1);

-- Insert products
INSERT INTO products (sku, barcode, product_name, description, category_id, supplier_id, buying_price, selling_price, stock_quantity, minimum_stock, maximum_stock, reorder_level, unit, weight, dimensions, status, created_by) VALUES
('LAP-001', '1234567890123', 'Laptop Pro 15"', 'High-performance laptop with 15-inch display', 2, 1, 800.00, 1200.00, 50, 10, 100, 15, 'PCS', 2.5, '15x10x1', 'ACTIVE', 1),
('LAP-002', '1234567890124', 'Laptop Air 13"', 'Lightweight laptop with 13-inch display', 2, 1, 600.00, 900.00, 30, 10, 80, 12, 'PCS', 1.8, '13x9x0.7', 'ACTIVE', 1),
('MOB-001', '1234567890125', 'Smartphone X', 'Latest smartphone with advanced features', 3, 1, 400.00, 600.00, 100, 20, 200, 30, 'PCS', 0.2, '6x3x0.3', 'ACTIVE', 1),
('MOB-002', '1234567890126', 'Tablet Pro', 'Professional tablet with stylus support', 3, 1, 350.00, 500.00, 40, 10, 80, 15, 'PCS', 0.5, '10x7x0.4', 'ACTIVE', 1),
('CLO-M-001', '1234567890127', 'Men\'s T-Shirt', 'Cotton t-shirt for men', 5, 2, 15.00, 25.00, 200, 50, 500, 75, 'PCS', 0.3, 'M', 'ACTIVE', 1),
('CLO-M-002', '1234567890128', 'Men\'s Jeans', 'Denim jeans for men', 5, 2, 30.00, 50.00, 150, 30, 300, 45, 'PCS', 0.8, '32x32', 'ACTIVE', 1),
('CLO-W-001', '1234567890129', 'Women\'s Blouse', 'Elegant blouse for women', 6, 2, 20.00, 35.00, 180, 40, 400, 60, 'PCS', 0.25, 'M', 'ACTIVE', 1),
('CLO-W-002', '1234567890130', 'Women\'s Dress', 'Casual dress for women', 6, 2, 35.00, 60.00, 120, 25, 250, 40, 'PCS', 0.4, 'M', 'ACTIVE', 1),
('FOO-001', '1234567890131', 'Organic Coffee', 'Premium organic coffee beans', 7, 3, 10.00, 18.00, 300, 50, 600, 100, 'KG', 1.0, '1kg', 'ACTIVE', 1),
('FOO-002', '1234567890132', 'Green Tea', 'Premium green tea leaves', 7, 3, 8.00, 15.00, 250, 40, 500, 80, 'KG', 0.5, '500g', 'ACTIVE', 1),
('OFF-001', '1234567890133', 'Office Chair', 'Ergonomic office chair', 8, 4, 80.00, 150.00, 25, 5, 50, 10, 'PCS', 15.0, '60x60x100', 'ACTIVE', 1),
('OFF-002', '1234567890134', 'Desk Lamp', 'LED desk lamp', 8, 4, 15.00, 30.00, 80, 15, 150, 25, 'PCS', 1.5, '20x20x40', 'ACTIVE', 1);

-- Insert sample inventory transactions
INSERT INTO inventory_transactions (transaction_type, product_id, quantity, previous_quantity, new_quantity, unit_price, total_price, reference_number, notes, performed_by) VALUES
('STOCK_IN', 1, 50, 0, 50, 800.00, 4000.00, 'PO-001', 'Initial stock purchase', 1),
('STOCK_IN', 2, 30, 0, 30, 600.00, 18000.00, 'PO-002', 'Initial stock purchase', 1),
('STOCK_IN', 3, 100, 0, 100, 400.00, 40000.00, 'PO-003', 'Initial stock purchase', 1),
('STOCK_OUT', 1, 5, 50, 45, 1200.00, 6000.00, 'SO-001', 'Customer sale', 2),
('STOCK_OUT', 3, 10, 100, 90, 600.00, 6000.00, 'SO-002', 'Customer sale', 2);

-- Insert sample activity logs
INSERT INTO activity_logs (user_id, action, module, description, ip_address, user_agent) VALUES
(1, 'LOGIN', 'AUTHENTICATION', 'User admin logged in', '192.168.1.100', 'Mozilla/5.0'),
(1, 'CREATE', 'CATEGORY', 'Created category: Electronics', '192.168.1.100', 'Mozilla/5.0'),
(1, 'CREATE', 'PRODUCT', 'Created product: Laptop Pro 15"', '192.168.1.100', 'Mozilla/5.0'),
(2, 'LOGIN', 'AUTHENTICATION', 'User manager logged in', '192.168.1.101', 'Mozilla/5.0'),
(2, 'STOCK_OUT', 'INVENTORY', 'Stock out for product: Laptop_Pro_15"', '192.168.1.101', 'Mozilla/5.0');

-- ============================================
-- VIEWS FOR REPORTS
-- ============================================

-- View for low stock products
CREATE VIEW v_low_stock_products AS
SELECT 
    p.product_id,
    p.sku,
    p.barcode,
    p.product_name,
    c.category_name,
    s.supplier_name,
    p.stock_quantity,
    p.minimum_stock,
    p.reorder_level,
    (p.minimum_stock - p.stock_quantity) AS needed_quantity
FROM products p
LEFT JOIN categories c ON p.category_id = c.category_id
LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id
WHERE p.stock_quantity <= p.minimum_stock AND p.status = 'ACTIVE';

-- View for inventory value
CREATE VIEW v_inventory_value AS
SELECT 
    p.product_id,
    p.sku,
    p.product_name,
    c.category_name,
    p.stock_quantity,
    p.buying_price,
    p.selling_price,
    (p.stock_quantity * p.buying_price) AS total_buying_value,
    (p.stock_quantity * p.selling_price) AS total_selling_value,
    ((p.selling_price - p.buying_price) * p.stock_quantity) AS potential_profit
FROM products p
LEFT JOIN categories c ON p.category_id = c.category_id
WHERE p.status = 'ACTIVE';

-- View for recent activities
CREATE VIEW v_recent_activities AS
SELECT 
    al.log_id,
    al.action,
    al.module,
    al.description,
    u.username,
    u.first_name,
    u.last_name,
    al.ip_address,
    al.created_at
FROM activity_logs al
LEFT JOIN users u ON al.user_id = u.user_id
ORDER BY al.created_at DESC
LIMIT 50;

-- View for product transactions
CREATE VIEW v_product_transactions AS
SELECT 
    it.transaction_id,
    it.transaction_type,
    p.sku,
    p.product_name,
    it.quantity,
    it.previous_quantity,
    it.new_quantity,
    it.unit_price,
    it.total_price,
    it.reference_number,
    it.notes,
    u.username AS performed_by,
    it.transaction_date
FROM inventory_transactions it
JOIN products p ON it.product_id = p.product_id
JOIN users u ON it.performed_by = u.user_id
ORDER BY it.transaction_date DESC;

-- ============================================
-- STORED PROCEDURES
-- ============================================

DELIMITER //

-- Procedure to add stock
CREATE PROCEDURE sp_add_stock(
    IN p_product_id INT,
    IN p_quantity INT,
    IN p_unit_price DECIMAL(10,2),
    IN p_reference VARCHAR(50),
    IN p_notes TEXT,
    IN p_performed_by INT
)
BEGIN
    DECLARE v_current_quantity INT;
    DECLARE v_new_quantity INT;
    DECLARE v_total_price DECIMAL(10,2);
    
    START TRANSACTION;
    
    -- Get current quantity
    SELECT stock_quantity INTO v_current_quantity 
    FROM products 
    WHERE product_id = p_product_id
    FOR UPDATE;
    
    -- Calculate new quantity and total price
    SET v_new_quantity = v_current_quantity + p_quantity;
    SET v_total_price = p_quantity * p_unit_price;
    
    -- Update product stock
    UPDATE products 
    SET stock_quantity = v_new_quantity,
        updated_at = CURRENT_TIMESTAMP
    WHERE product_id = p_product_id;
    
    -- Record transaction
    INSERT INTO inventory_transactions (
        transaction_type, product_id, quantity, previous_quantity, 
        new_quantity, unit_price, total_price, reference_number, 
        notes, performed_by
    ) VALUES (
        'STOCK_IN', p_product_id, p_quantity, v_current_quantity, 
        v_new_quantity, p_unit_price, v_total_price, p_reference, 
        p_notes, p_performed_by
    );
    
    COMMIT;
END //

-- Procedure to remove stock
CREATE PROCEDURE sp_remove_stock(
    IN p_product_id INT,
    IN p_quantity INT,
    IN p_unit_price DECIMAL(10,2),
    IN p_reference VARCHAR(50),
    IN p_notes TEXT,
    IN p_performed_by INT
)
BEGIN
    DECLARE v_current_quantity INT;
    DECLARE v_new_quantity INT;
    DECLARE v_total_price DECIMAL(10,2);
    
    START TRANSACTION;
    
    -- Get current quantity
    SELECT stock_quantity INTO v_current_quantity 
    FROM products 
    WHERE product_id = p_product_id
    FOR UPDATE;
    
    -- Check if enough stock
    IF v_current_quantity < p_quantity THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Insufficient stock';
    END IF;
    
    -- Calculate new quantity and total price
    SET v_new_quantity = v_current_quantity - p_quantity;
    SET v_total_price = p_quantity * p_unit_price;
    
    -- Update product stock
    UPDATE products 
    SET stock_quantity = v_new_quantity,
        updated_at = CURRENT_TIMESTAMP
    WHERE product_id = p_product_id;
    
    -- Record transaction
    INSERT INTO inventory_transactions (
        transaction_type, product_id, quantity, previous_quantity, 
        new_quantity, unit_price, total_price, reference_number, 
        notes, performed_by
    ) VALUES (
        'STOCK_OUT', p_product_id, p_quantity, v_current_quantity, 
        v_new_quantity, p_unit_price, v_total_price, p_reference, 
        p_notes, p_performed_by
    );
    
    COMMIT;
END //

DELIMITER ;

-- ============================================
-- TRIGGERS
-- ============================================

DELIMITER //

-- Trigger to log activity when product is created
CREATE TRIGGER tr_product_after_insert
AFTER INSERT ON products
FOR EACH ROW
BEGIN
    INSERT INTO activity_logs (user_id, action, module, description)
    VALUES (
        NEW.created_by, 
        'CREATE', 
        'PRODUCT', 
        CONCAT('Created product: ', NEW.product_name)
    );
END //

-- Trigger to log activity when product is updated
CREATE TRIGGER tr_product_after_update
AFTER UPDATE ON products
FOR EACH ROW
BEGIN
    INSERT INTO activity_logs (action, module, description)
    VALUES (
        'UPDATE', 
        'PRODUCT', 
        CONCAT('Updated product: ', NEW.product_name)
    );
END //

-- Trigger to log activity when product is deleted
CREATE TRIGGER tr_product_after_delete
AFTER DELETE ON products
FOR EACH ROW
BEGIN
    INSERT INTO activity_logs (action, module, description)
    VALUES (
        'DELETE', 
        'PRODUCT', 
        CONCAT('Deleted product: ', OLD.product_name)
    );
END //

DELIMITER ;
