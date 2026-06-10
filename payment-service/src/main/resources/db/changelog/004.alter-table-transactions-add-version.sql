--liquibase formatted sql

--changeset Codex:004-alter-table-transactions-add-version
ALTER TABLE transactions
ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
