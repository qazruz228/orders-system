--liquibase formatted sql

--changeset Sergey:001-create-table-processed_events

CREATE TABLE processed_events (

    id BIGSERIAL,
    unique_order_number VARCHAR(100) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP

);