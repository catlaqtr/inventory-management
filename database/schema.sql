CREATE DATABASE IF NOT EXISTS inventory_db;

USE inventory_db;

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

CREATE TABLE IF NOT EXISTS stock_transactions (
                                                  id INT NOT NULL AUTO_INCREMENT,
                                                  product_id INT NOT NULL,
                                                  transaction_type VARCHAR(20) NOT NULL,
    quantity INT NOT NULL,
    note VARCHAR(255),
    transaction_date DATETIME NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_transaction_product
    FOREIGN KEY (product_id)
    REFERENCES products(id)
    ON DELETE CASCADE
    );