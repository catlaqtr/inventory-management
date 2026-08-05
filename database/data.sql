USE inventory_db;

-- Insert default admin and staff accounts (Note: passwords will be hashed by Spring Security later)
INSERT INTO users (username, password, role) VALUES
                                                 ('admin', 'admin123', 'ROLE_ADMIN'),
                                                 ('staff', 'staff123', 'ROLE_STAFF')
    ON DUPLICATE KEY UPDATE id=id;

-- Insert sample products
INSERT INTO products (name, category, description, quantity, low_stock_level, unit_price) VALUES
                                                                                              ('Wireless Mouse', 'Electronics', '2.4GHz Ergonomic Optical Mouse', 45, 10, 24.99),
                                                                                              ('Mechanical Keyboard', 'Electronics', 'RGB Backlit Blue Switches', 8, 10, 79.99),
                                                                                              ('Desk Chair', 'Furniture', 'Ergonomic Mesh Office Chair', 3, 5, 149.50)
    ON DUPLICATE KEY UPDATE id=id;