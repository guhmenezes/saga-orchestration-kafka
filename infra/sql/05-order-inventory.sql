\c inventory_db

CREATE TABLE order_inventory (
    id BIGSERIAL PRIMARY KEY,
    order_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    version BIGINT DEFAULT 0,
    CONSTRAINT uk_order_inventory_type UNIQUE (order_id, transaction_type)
);
