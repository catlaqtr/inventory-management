USE inventory_db;

INSERT INTO companies (name) VALUES ('Demo Company')
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO products (name, category, description, quantity, low_stock_level, unit_price, company_id)
SELECT 'Wireless Mouse', 'Electronics', '2.4GHz Ergonomic Optical Mouse', 45, 10, 24.99, c.id
FROM companies c WHERE c.name = 'Demo Company'
AND NOT EXISTS (
    SELECT 1 FROM products p
    WHERE p.name = 'Wireless Mouse' AND p.company_id = c.id
);

INSERT INTO products (name, category, description, quantity, low_stock_level, unit_price, company_id)
SELECT 'Mechanical Keyboard', 'Electronics', 'RGB Backlit Blue Switches', 8, 10, 79.99, c.id
FROM companies c WHERE c.name = 'Demo Company'
AND NOT EXISTS (
    SELECT 1 FROM products p
    WHERE p.name = 'Mechanical Keyboard' AND p.company_id = c.id
);

INSERT INTO products (name, category, description, quantity, low_stock_level, unit_price, company_id)
SELECT 'Desk Chair', 'Furniture', 'Ergonomic Mesh Office Chair', 3, 5, 149.50, c.id
FROM companies c WHERE c.name = 'Demo Company'
AND NOT EXISTS (
    SELECT 1 FROM products p
    WHERE p.name = 'Desk Chair' AND p.company_id = c.id
);
