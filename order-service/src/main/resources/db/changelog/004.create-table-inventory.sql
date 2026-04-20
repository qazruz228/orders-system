-- changeset 4

CREATE TABLE inventory(
    product_id BIGINT PRIMARY KEY,
    quantity   INT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_products
        FOREIGN KEY (product_id)
            REFERENCES products (id)
            ON DELETE CASCADE
);