CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    seat_id INT NOT NULL,
    booking_time TIMESTAMP,
    price INT NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('BOOKED', 'CANCELLED', 'CONFIRMED'))
);