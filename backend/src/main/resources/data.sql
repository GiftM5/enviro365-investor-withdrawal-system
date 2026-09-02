INSERT INTO investors (first_name, last_name, date_of_birth, email) VALUES
('John', 'Smith', '1958-04-15', 'john.smith@example.com'),
('Mary', 'Jones', '1985-08-20', 'mary.jones@example.com');

INSERT INTO portfolios (portfolio_number, investor_id) VALUES
('ENV-10001', 1),
('ENV-20001', 2);

INSERT INTO investment_products (product_name, product_type, balance, portfolio_id) VALUES
('Retirement Annuity', 'RETIREMENT', 200000.00, 1),
('Growth Fund', 'INVESTMENT', 150000.00, 1),
('Education Reserve', 'INVESTMENT', 120000.00, 2);

INSERT INTO withdrawal_notices (investor_id, product_id, amount, previous_balance, remaining_balance, status, created_at) VALUES
(1, 1, 25000.00, 200000.00, 175000.00, 'APPROVED', '2026-08-01T10:00:00');
