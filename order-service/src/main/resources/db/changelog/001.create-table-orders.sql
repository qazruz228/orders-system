-- changeset: 1
-- author: Sergey

create table orders(
    id BIGSERIAL primary key,
    status VARCHAR(50) NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    delivery_address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CREATE TABLE outbox_events (
--                                id BIGSERIAL PRIMARY KEY,
--                                payload TEXT NOT NULL,
--                                outbox_status VARCHAR(50) NOT NULL, -- NEW, SENT, FAILED
--                                retry_count INT DEFAULT 0,
--                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--                                processed_at TIMESTAMP
-- );