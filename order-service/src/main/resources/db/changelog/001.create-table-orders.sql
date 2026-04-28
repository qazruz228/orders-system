--liquibase formatted sql

--changeset Sergey:001-create-table-orders
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    total_amount NUMERIC(10, 2) NOT NULL,
    delivery_address TEXT,
    uniq_id UUID NOT NULL,
    version BIGINT,
    created_at TIMESTAMP
);
