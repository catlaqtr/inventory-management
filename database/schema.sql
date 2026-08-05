-- 1. Create and select database
CREATE DATABASE IF NOT EXISTS inventory_db;
USE inventory_db;

-- 2. Users Table (For Login / Signup & Roles)
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_STAFF', -- 'ROLE_ADMIN' or 'ROLE_STAFF'
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 3. Products Table
CREATE TABLE IF NOT EXISTS products (
                                        id INT NOT NULL AUTO_INCREMENT,
                                        name VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    quantity INT NOT NULL DEFAULT 0,
    low_stock_level INT NOT NULL DEFAULT 5,
    unit_price DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (id)
    );

-- 4. Stock Transactions Table
CREATE TABLE IF NOT EXISTS stock_transactions (
                                                  id INT NOT NULL AUTO_INCREMENT,
                                                  product_id INT NOT NULL,
                                                  transaction_type VARCHAR(20) NOT NULL, -- 'STOCK_IN' or 'STOCK_OUT'
    quantity INT NOT NULL,
    note VARCHAR(255),
    transaction_date DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_transaction_product
    FOREIGN KEY (product_id)
    REFERENCES products(id)
    ON DELETE CASCADE
    );