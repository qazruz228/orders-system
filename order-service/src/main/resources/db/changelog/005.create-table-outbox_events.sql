--liquibase formatted sql

--changeset Sergey:005-create-table-outbox-events
CREATE TABLE outbox_events (
    id BIGSERIAL PRIMARY KEY,
    payload TEXT NOT NULL,
    outbox_status VARCHAR(50) NOT NULL,
    order_status VARCHAR(50) NOT NULL,
    unique_order_number VARCHAR(100) NOT NULL,
    order_id BIGINT NOT NULL,
    retry_count INTEGER,
    created_at TIMESTAMP,
    processed_at TIMESTAMP
);
