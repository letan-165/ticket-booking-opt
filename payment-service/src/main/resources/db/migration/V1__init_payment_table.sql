CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    ticket_id INT NOT NULL,
    amount INT,
    status VARCHAR(50) CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED')) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payment_transactions (
    id VARCHAR(255) PRIMARY KEY,
    txn_ref INT REFERENCES payments(id) ON DELETE SET NULL,
    gateway_type VARCHAR(100),
    amount INT,
    extra_info TEXT,
    response_code VARCHAR(20),
    bank_code VARCHAR(50),
    pay_date TIMESTAMP
);