--liquibase formatted sql

--changeset Sergey:002-create-table-order-item
CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id) REFERENCES orders (id)
);


