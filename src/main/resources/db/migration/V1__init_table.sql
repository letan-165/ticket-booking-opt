CREATE TABLE users (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('USER', 'ORGANIZER', 'ADMIN'))
);

CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    organizer_id VARCHAR(255) REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(50),
    location VARCHAR(50),
    price_ticket INT,
    time TIMESTAMP,
    total_seats INT
);

CREATE TABLE seats (
    id SERIAL PRIMARY KEY,
    event_id INT REFERENCES events(id) ON DELETE CASCADE,
    seat_number VARCHAR(50),
    status VARCHAR(50) NOT NULL CHECK (status IN ('AVAILABLE', 'BOOKED', 'LOCKED')),
    UNIQUE (event_id, seat_number)
);

CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) REFERENCES users(id) ON DELETE SET NULL,
    seat_id INT REFERENCES seats(id) ON DELETE SET NULL,
    booking_time TIMESTAMP,
    price INT not null,
    status VARCHAR(50) NOT NULL CHECK (status IN ('BOOKED', 'CANCELLED', 'CONFIRMED'))
);

CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    ticket_id INT REFERENCES tickets(id) ON DELETE SET NULL,
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