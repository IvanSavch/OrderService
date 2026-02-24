--liquibase formatted sql

--changeset Sauchanka:1
ALTER TABLE items
    ALTER COLUMN created_at SET DEFAULT current_timestamp;

ALTER TABLE items
    ALTER COLUMN updated_at SET DEFAULT current_timestamp;


