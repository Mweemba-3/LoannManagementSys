-- Create Database
CREATE DATABASE IF NOT EXISTS loan_management_system;
USE loan_management_system;

-- Table for Employees (Admins and Employees)
CREATE TABLE IF NOT EXISTS employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL, -- Hashed passwords
    role ENUM('admin', 'employee') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table for Clients (from AddClientScreen, EditClientScreen)
CREATE TABLE IF NOT EXISTS clients (
    client_id INT AUTO_INCREMENT PRIMARY KEY,
    branch VARCHAR(50) NOT NULL,
    title ENUM('Mr', 'Ms', 'Mrs', 'Dr', 'Prof', 'Other') NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    phone_number VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(100),
    physical_address VARCHAR(255) NOT NULL,
    province VARCHAR(50) NOT NULL,
    postal_address VARCHAR(255),
    id_type VARCHAR(50) NOT NULL,
    id_number VARCHAR(50) UNIQUE NOT NULL,
    id_place VARCHAR(100) DEFAULT 'GRZ',
    employer_name VARCHAR(100),
    employee_number VARCHAR(50),
    job_title VARCHAR(100),
    monthly_income DECIMAL(10, 2),
    employment_status ENUM('Employed', 'Self-Employed', 'Unemployed') NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    marital_status ENUM('Single', 'Married', 'Divorced', 'Widowed') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table for Next of Kin
CREATE TABLE IF NOT EXISTS next_of_kin (
    next_of_kin_id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    relationship ENUM('Spouse', 'Parent', 'Sibling', 'Other') NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    physical_address VARCHAR(255),
    id_number VARCHAR(50),
    FOREIGN KEY (client_id) REFERENCES clients(client_id) ON DELETE CASCADE
);

-- Table for Client Bank Details
CREATE TABLE IF NOT EXISTS client_bank_details (
    bank_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    account_name VARCHAR(100) NOT NULL,
    branch_code VARCHAR(50),
    branch_name VARCHAR(100),
    FOREIGN KEY (client_id) REFERENCES clients(client_id) ON DELETE CASCADE
);

-- Table for Uploaded Files (e.g., ID documents)
CREATE TABLE IF NOT EXISTS uploaded_files (
    file_id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size BIGINT NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(client_id) ON DELETE CASCADE
);

-- Table for Loan Products (from CreateLoanProductScreen)
CREATE TABLE IF NOT EXISTS loan_products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) UNIQUE NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    interest_type ENUM('Simple', 'Compound') NOT NULL,
    calculation_method ENUM('FLAT', 'REDUCING') NOT NULL DEFAULT 'REDUCING',
    loan_term_months INT NOT NULL,
    min_amount DECIMAL(10, 2) NOT NULL,
    max_amount DECIMAL(10, 2) NOT NULL,
    grace_period_months INT NOT NULL,
    installment_type ENUM('Weekly', 'Monthly', 'Quarterly', 'Annually') NOT NULL,
    loan_fee_type ENUM('Cash', 'Mobile', 'Bank') NOT NULL,
    category_1 ENUM('Personal', 'Business', 'Education') NOT NULL,
    category_2 ENUM('Short-Term', 'Long-Term', 'Microloan') NOT NULL,
    refinance ENUM('Yes', 'No') NOT NULL,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES employees(employee_id)
);

-- Table for Loans (from ApplyLoanScreen, LoansScreen)
CREATE TABLE IF NOT EXISTS loans (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    loan_number VARCHAR(50) UNIQUE NOT NULL,
    client_id INT NOT NULL,
    product_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    calculation_method ENUM('FLAT', 'REDUCING') NOT NULL DEFAULT 'REDUCING',
    loan_term_months INT NOT NULL,
    grace_period_months INT NOT NULL,
    installment_type ENUM('Weekly', 'Monthly', 'Quarterly', 'Annually') NOT NULL,
    application_date DATE NOT NULL,
    disbursement_date DATE,
    due_date DATE,
    status ENUM('Pending', 'Approved', 'Rejected', 'Active', 'Closed') NOT NULL,
    processed_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(client_id),
    FOREIGN KEY (product_id) REFERENCES loan_products(product_id),
    FOREIGN KEY (processed_by) REFERENCES employees(employee_id)
);

-- Table for loan sequence number generation
CREATE TABLE IF NOT EXISTS loan_sequence (
    id INT AUTO_INCREMENT PRIMARY KEY,
    last_loan_number INT NOT NULL DEFAULT 0
);

-- Table for Collaterals (up to 4 per loan)
CREATE TABLE IF NOT EXISTS collaterals (
    collateral_id INT AUTO_INCREMENT PRIMARY KEY,
    loan_id INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'ZMW',
    valuation_date DATE,
    FOREIGN KEY (loan_id) REFERENCES loans(loan_id) ON DELETE CASCADE
);

-- Table for Guarantors (up to 3 per loan)
CREATE TABLE IF NOT EXISTS guarantors (
    guarantor_id INT AUTO_INCREMENT PRIMARY KEY,
    loan_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    relationship ENUM('Spouse', 'Parent', 'Sibling', 'Other') NOT NULL,
    amount_guaranteed DECIMAL(10, 2) NOT NULL,
    client_id INT,
    phone_number VARCHAR(15),
    address VARCHAR(255),
    FOREIGN KEY (loan_id) REFERENCES loans(loan_id) ON DELETE CASCADE,
    FOREIGN KEY (client_id) REFERENCES clients(client_id)
);

-- Table for Loan Payments
CREATE TABLE IF NOT EXISTS loan_payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    loan_id INT NOT NULL,
    payment_number INT NOT NULL,
    scheduled_payment_date DATE NOT NULL,
    payment_amount DECIMAL(10, 2) NOT NULL,
    principal_amount DECIMAL(10, 2) NOT NULL,
    interest_amount DECIMAL(10, 2) NOT NULL,
    penalty_amount DECIMAL(10, 2) DEFAULT 0.00,
    paid_amount DECIMAL(10, 2) DEFAULT 0.00,
    paid_date DATE,
    status ENUM('Pending', 'Paid', 'Overdue') NOT NULL,
    FOREIGN KEY (loan_id) REFERENCES loans(loan_id) ON DELETE CASCADE
);

-- Table for Payment Receipts (from InitializePaymentScreen)
CREATE TABLE IF NOT EXISTS payment_receipts (
    receipt_id INT AUTO_INCREMENT PRIMARY KEY,
    payment_id INT NOT NULL,
    loan_number VARCHAR(50) NOT NULL,
    client_id INT NOT NULL,
    employee_id INT NOT NULL,
    voucher_number VARCHAR(50) NOT NULL,
    mode_of_payment ENUM('Mobile', 'Cash', 'Bank', 'Other') NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    status ENUM('Pending', 'Approved', 'Rejected') NOT NULL,
    approved_by INT,
    approval_date TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES loan_payments(payment_id),
    FOREIGN KEY (client_id) REFERENCES clients(client_id),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    FOREIGN KEY (approved_by) REFERENCES employees(employee_id)
);

-- Table for Penalty Waivers
CREATE TABLE IF NOT EXISTS penalty_waivers (
    waiver_id INT AUTO_INCREMENT PRIMARY KEY,
    payment_id INT NOT NULL,
    waiver_amount DECIMAL(10, 2) NOT NULL,
    waived_by INT NOT NULL,
    waiver_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reason VARCHAR(255),
    FOREIGN KEY (payment_id) REFERENCES loan_payments(payment_id),
    FOREIGN KEY (waived_by) REFERENCES employees(employee_id)
);

-- Table for Audit Logs (from ActivityScreen)
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    action VARCHAR(255) NOT NULL,
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details TEXT,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- Stored Procedure for Loan Schedule Calculation (Updated for Weekly)
DELIMITER //
CREATE PROCEDURE CalculateLoanSchedule(
    IN p_loan_id INT,
    IN p_amount DECIMAL(10, 2),
    IN p_interest_rate DECIMAL(5, 2),
    IN p_loan_term_months INT,
    IN p_grace_period_months INT,
    IN p_installment_type ENUM('Weekly', 'Monthly', 'Quarterly', 'Annually'),
    IN p_interest_type ENUM('Simple', 'Compound'),
    IN p_application_date DATE
)
BEGIN
    DECLARE v_installments INT;
    DECLARE v_payment_amount DECIMAL(10, 2);
    DECLARE v_principal_per_payment DECIMAL(10, 2);
    DECLARE v_interest_per_payment DECIMAL(10, 2);
    DECLARE v_payment_date DATE;
    DECLARE i INT DEFAULT 1;
    DECLARE v_total_interest DECIMAL(10, 2);

    -- Calculate number of installments based on installment type
    SET v_installments = CASE p_installment_type
        WHEN 'Weekly' THEN p_loan_term_months * 4 -- Approximate weeks per month
        WHEN 'Monthly' THEN p_loan_term_months
        WHEN 'Quarterly' THEN CEIL(p_loan_term_months / 3)
        WHEN 'Annually' THEN CEIL(p_loan_term_months / 12)
    END;

    -- Calculate interest based on type
    IF p_interest_type = 'Simple' THEN
        SET v_total_interest = p_amount * (p_interest_rate / 100) * (p_loan_term_months / 12);
        SET v_interest_per_payment = v_total_interest / v_installments;
        SET v_principal_per_payment = p_amount / v_installments;
        SET v_payment_amount = v_principal_per_payment + v_interest_per_payment;
    ELSE -- Compound Interest
        SET v_payment_amount = p_amount * (p_interest_rate / 100 / 12) * POW(1 + p_interest_rate / 100 / 12, v_installments) / (POW(1 + p_interest_rate / 100 / 12, v_installments) - 1);
        SET v_principal_per_payment = p_amount / v_installments;
        SET v_interest_per_payment = v_payment_amount - v_principal_per_payment;
    END IF;

    -- Set initial payment date after grace period
    SET v_payment_date = DATE_ADD(p_application_date, INTERVAL p_grace_period_months MONTH);

    -- Insert payment schedule
    WHILE i <= v_installments DO
        INSERT INTO loan_payments (
            loan_id, payment_number, scheduled_payment_date,
            payment_amount, principal_amount, interest_amount, status
        )
        VALUES (
            p_loan_id, i, v_payment_date,
            v_payment_amount, v_principal_per_payment, v_interest_per_payment, 'Pending'
        );

        -- Update payment date based on installment type
        SET v_payment_date = CASE p_installment_type
            WHEN 'Weekly' THEN DATE_ADD(v_payment_date, INTERVAL 1 WEEK)
            WHEN 'Monthly' THEN DATE_ADD(v_payment_date, INTERVAL 1 MONTH)
            WHEN 'Quarterly' THEN DATE_ADD(v_payment_date, INTERVAL 3 MONTH)
            WHEN 'Annually' THEN DATE_ADD(v_payment_date, INTERVAL 12 MONTH)
        END;
        SET i = i + 1;
    END WHILE;

    -- Update loan due date
    UPDATE loans
    SET due_date = DATE_ADD(p_application_date, INTERVAL p_loan_term_months + p_grace_period_months MONTH)
    WHERE loan_id = p_loan_id;
END //
DELIMITER ;

-- Trigger to Auto-Fill Loan Fields
DELIMITER //
CREATE TRIGGER before_loan_insert
BEFORE INSERT ON loans
FOR EACH ROW
BEGIN
    DECLARE v_product_name VARCHAR(100);
    DECLARE v_interest_rate DECIMAL(5, 2);
    DECLARE v_loan_term INT;
    DECLARE v_grace_period INT;
    DECLARE v_installment_type ENUM('Weekly', 'Monthly', 'Quarterly', 'Annually');
    DECLARE v_interest_type ENUM('Simple', 'Compound');
    DECLARE v_calculation_method ENUM('FLAT', 'REDUCING');

    -- Fetch loan product details
    SELECT product_name, interest_rate, loan_term_months, grace_period_months,
           installment_type, interest_type, calculation_method
    INTO v_product_name, v_interest_rate, v_loan_term, v_grace_period,
         v_installment_type, v_interest_type, v_calculation_method
    FROM loan_products
    WHERE product_id = NEW.product_id;

    -- Auto-fill loan fields
    SET NEW.interest_rate = v_interest_rate;
    SET NEW.loan_term_months = v_loan_term;
    SET NEW.grace_period_months = v_grace_period;
    SET NEW.installment_type = v_installment_type;
    SET NEW.calculation_method = v_calculation_method;

    -- Generate unique loan number using sequence table
    INSERT INTO loan_sequence (last_loan_number) VALUES (0) ON DUPLICATE KEY UPDATE last_loan_number = last_loan_number;
    UPDATE loan_sequence SET last_loan_number = last_loan_number + 1 WHERE id = 1;
    SELECT CONCAT('LN', LPAD(last_loan_number, 6, '0')) INTO NEW.loan_number FROM loan_sequence WHERE id = 1;
END //
DELIMITER ;

-- Trigger to Calculate Payment Schedule After Loan Approval
DELIMITER //
CREATE TRIGGER after_loan_approve
AFTER UPDATE ON loans
FOR EACH ROW
BEGIN
    IF NEW.status = 'Approved' AND OLD.status != 'Approved' THEN
        CALL CalculateLoanSchedule(
            NEW.loan_id, NEW.amount, NEW.interest_rate,
            NEW.loan_term_months, NEW.grace_period_months,
            NEW.installment_type, (SELECT interest_type FROM loan_products WHERE product_id = NEW.product_id),
            NEW.application_date
        );
    END IF;
END //
DELIMITER ;

-- Trigger for Penalty Calculation
DELIMITER //
CREATE TRIGGER check_overdue_payments
BEFORE UPDATE ON loan_payments
FOR EACH ROW
BEGIN
    IF NEW.status = 'Pending' AND NEW.scheduled_payment_date < CURDATE() THEN
        SET NEW.status = 'Overdue';
        SET NEW.penalty_amount = NEW.payment_amount * 0.05; -- 5% penalty
    END IF;
END //
DELIMITER ;

-- Trigger for Audit Logging
DELIMITER //
CREATE TRIGGER after_client_insert
AFTER INSERT ON clients
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (employee_id, action, details)
    VALUES (NEW.client_id, 'Client Added', CONCAT('Added client: ', NEW.first_name, ' ', NEW.last_name));
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER after_loan_insert
AFTER INSERT ON loans
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (employee_id, action, details)
    VALUES (NEW.processed_by, 'Loan Applied', CONCAT('Loan Number: ', NEW.loan_number, ' for Client ID: ', NEW.client_id));
END //
DELIMITER ;

-- Create Indexes
CREATE INDEX IF NOT EXISTS idx_loan_number ON loans(loan_number);
CREATE INDEX IF NOT EXISTS idx_client_phone ON clients(phone_number);
CREATE INDEX IF NOT EXISTS idx_payment_loan_id ON loan_payments(loan_id);
CREATE INDEX IF NOT EXISTS idx_receipt_loan_number ON payment_receipts(loan_number);