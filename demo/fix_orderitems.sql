-- Check if subtotal column exists and drop it
USE ecommerce_db;

-- Check current table structure
SHOW COLUMNS FROM OrderItems;

-- Drop the subtotal column if it exists
ALTER TABLE OrderItems DROP COLUMN subtotal;
