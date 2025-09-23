INSERT INTO users (id, name, email, password, role)
SELECT 
    'tan' || i AS id,
    'tan' || i AS name,
    'tan' || i || '@gmail.com' AS email,
    '$2a$10$Fal3pEdS1EsTbv0pUgfei.ks3heQkl5i0aTy5DIj6DfZ.U9tCrASa' AS password,
    CASE
        WHEN i = 1 THEN 'ADMIN'
        WHEN i <= 1000 THEN 'ORGANIZER'
        ELSE 'USER'
    END AS role
FROM generate_series(1, 100000) AS s(i);


INSERT INTO events (organizer_id, name, location, price_ticket, time, total_seats)
SELECT
    'tan' || (2 + floor(random() * 999)::int),
    'Event ' || i,
    'Location ' || (1 + floor(random() * 50)::int),
    (10 + floor(random() * 90)::int) * 1000,
    NOW() + (i || ' days')::interval,
    100 + floor(random() * 50)::int
FROM generate_series(1, 5000) AS s(i);


INSERT INTO seats (event_id, seat_number, status)
SELECT
    e.id,
    'S' || s.i,
    'AVAILABLE'
FROM events e
JOIN generate_series(1, 150) AS s(i) ON s.i <= e.total_seats;
