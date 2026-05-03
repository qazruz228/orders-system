--liquibase formatted sql

--changeset Sergey:005-create-table-outbox-events
CREATE TABLE outbox_events (
    id BIGSERIAL PRIMARY KEY,
    payload TEXT NOT NULL,
    outbox_status VARCHAR(50) NOT NULL,
    request_id UUID NOT NULL,
    retry_count INTEGER,
    created_at TIMESTAMP,
    processed_at TIMESTAMP
);
