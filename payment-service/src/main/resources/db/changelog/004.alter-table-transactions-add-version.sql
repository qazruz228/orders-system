--liquibase formatted sql

--changeset Sergey:004-alter-table-transactions-add-version
ALTER TABLE transactions
ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
