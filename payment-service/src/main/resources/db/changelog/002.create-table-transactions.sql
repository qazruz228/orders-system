--liquibase formatted sql

--changeset Sergey:002-create-table-Transaction

CREATE TABLE transactions(

    id BIGSERIAL,
    unique_order_number VARCHAR(100) NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    delivery_address TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP

);
