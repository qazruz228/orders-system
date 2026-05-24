--liquibase formatted sql

--changeset Sergey:001-create-table-processed_events

CREATE TABLE processed_events (
    id BIGSERIAL PRIMARY KEY,
    unique_order_number VARCHAR(100) NOT NULL,
    order_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT uk_processed_events_unique_order_number_order_status
        UNIQUE (unique_order_number, order_status)
);
