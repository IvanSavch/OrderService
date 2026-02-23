--liquibase formatted sql

--changeset Sauchanka:3

ALTER TABLE orders
    ALTER COLUMN created_at SET DEFAULT current_timestamp;

ALTER TABLE orders
    ALTER COLUMN updated_at SET DEFAULT current_timestamp;

ALTER TABLE order_items
    ALTER COLUMN created_at SET DEFAULT current_timestamp;

ALTER TABLE order_items
    ALTER COLUMN updated_at SET DEFAULT current_timestamp;

create index orders_user_id_index on orders(user_id);

create index orders_status_index on orders(status);