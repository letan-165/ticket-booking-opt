CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    organizer_id VARCHAR(255) NOT NULL,
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