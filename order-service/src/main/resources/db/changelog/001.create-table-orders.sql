--liquibase formatted sql

--changeset Sergey:001-create-table-orders
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    delivery_address TEXT,
    unique_order_id UUID NOT NULL,
    version BIGINT,
    created_at TIMESTAMP
);
