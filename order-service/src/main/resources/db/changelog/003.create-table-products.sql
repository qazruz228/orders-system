-- changeset: 3
-- author: Sergey

CREATE TABLE products(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    price NUMERIC(10, 2) NOT NULL
);