--liquibase formatted sql

--changeset Sergey:003-create-table-products
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT

);