INSERT INTO investors (first_name, last_name, date_of_birth, email) VALUES
('Mpho', 'Mofokeng', '1986-02-18', 'mpho.mofokeng@example.com'),
('Lerato', 'Molefe', '1992-07-09', 'lerato.molefe@example.com'),
('Thabo', 'Mokoena', '1954-11-22', 'thabo.mokoena@example.com');

INSERT INTO portfolios (portfolio_number, investor_id) VALUES
('ENV-INV-001', 1),
('ENV-INV-002', 2),
('ENV-INV-003', 3);

INSERT INTO investment_products (product_name, product_type, balance, portfolio_id) VALUES
('Retirement Annuity', 'RETIREMENT', 500000.00, 1),
('Growth Fund', 'INVESTMENT', 200000.00, 1),
('Education Reserve', 'INVESTMENT', 320000.00, 2),
('Retirement Preservation', 'RETIREMENT', 420000.00, 2),
('Balanced Growth', 'INVESTMENT', 340000.00, 3),
('Emergency Savings', 'INVESTMENT', 95000.00, 3);

INSERT INTO withdrawal_notices (investor_id, product_id, amount, previous_balance, remaining_balance, status, created_at) VALUES
(1, 1, 25000.00, 500000.00, 475000.00, 'APPROVED', '2026-08-12T10:00:00'),
(1, 2, 10000.00, 200000.00, 190000.00, 'APPROVED', '2026-08-30T14:30:00'),
(2, 4, 20000.00, 420000.00, 400000.00, 'APPROVED', '2026-09-01T11:15:00'),
(3, 5, 15000.00, 340000.00, 325000.00, 'APPROVED', '2026-08-18T15:45:00');
