--liquibase formatted sql

--changeset Sergey:002-create-table-transactions

CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    unique_order_number VARCHAR(100) NOT NULL,
    order_id BIGINT NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    delivery_address TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT uk_transactions_unique_order_number UNIQUE (unique_order_number)
);
