ALTER TABLE events DROP CONSTRAINT IF EXISTS events_organizer_id_fkey;
ALTER TABLE tickets DROP CONSTRAINT IF EXISTS tickets_user_id_fkey;

DROP TABLE IF EXISTS users CASCADE;

ALTER TABLE events
ADD COLUMN organizer_keycloak_id VARCHAR(255);


ALTER TABLE tickets
ADD COLUMN user_keycloak_id VARCHAR(255);
