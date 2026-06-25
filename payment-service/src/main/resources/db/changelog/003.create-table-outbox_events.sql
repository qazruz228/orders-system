--liquibase formatted sql

--changeset Sergey:003-create-table-outbox-events
CREATE TABLE IF NOT EXISTS outbox_events (
    id BIGSERIAL PRIMARY KEY,
    payload TEXT NOT NULL,
    outbox_status VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    unique_order_number VARCHAR(100) NOT NULL,
    order_id BIGINT NOT NULL,
    retry_count INTEGER,
    created_at TIMESTAMP,
    processed_at TIMESTAMP
);
