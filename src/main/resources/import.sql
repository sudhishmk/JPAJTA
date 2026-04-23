-- Insert Customers
INSERT INTO customers (id, name) VALUES (1, 'Alice');
INSERT INTO customers (id, name) VALUES (2, 'Bob');

-- Insert Accounts linked to Customers
INSERT INTO accounts (id, balance, customer_id) VALUES (1, 500.00, 1); -- Alice's Checking
INSERT INTO accounts (id, balance, customer_id) VALUES (2, 1500.00, 1); -- Alice's Savings (High Value)
INSERT INTO accounts (id, balance, customer_id) VALUES (3, 300.00, 2); -- Bob's Checking

-- Reset ID generators
ALTER TABLE customers ALTER COLUMN id RESTART WITH 3;
ALTER TABLE accounts ALTER COLUMN id RESTART WITH 4;
