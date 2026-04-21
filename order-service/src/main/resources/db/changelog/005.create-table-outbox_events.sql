-- changeset: 5
-- author: Sergey

CREATE TABLE outbox_events(
    id            BIGSERIAL   NOT NULL,
    payload       TEXT        NOT NULL,
    outbox_status VARCHAR(50) NOT NULL,
    retry_count   INT       DEFAULT 0,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at  TIMESTAMP


);