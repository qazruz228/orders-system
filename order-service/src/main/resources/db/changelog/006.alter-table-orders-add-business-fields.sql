--liquibase formatted sql

--changeset Codex:006-alter-table-orders-add-business-fields
ALTER TABLE orders
    ADD COLUMN unique_order_number VARCHAR(100),
    ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'CREATED',
    ADD COLUMN updated_at TIMESTAMP;

ALTER TABLE orders
    ADD CONSTRAINT uk_orders_unique_order_number UNIQUE (unique_order_number);
