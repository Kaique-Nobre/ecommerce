CREATE TABLE orders
    (
        id UUID PRIMARY KEY,
        customer_id UUID NOT NULL,
        status VARCHAR(30) NOT NULL,
        created_at TIMESTAMPTZ NOT NULL,
        updated_at TIMESTAMPTZ NOT NULL,
        total_amount NUMERIC(12,2) NOT NULL CHECK ( total_amount >=0 )
    );

CREATE TABLE order_items
    (
        id UUID PRIMARY KEY,
        order_id UUID NOT NULL,
        product_id UUID NOT NULL,
        product_name VARCHAR(255) NOT NULL,
        quantity INTEGER NOT NULL CHECK ( quantity > 0 ),
        unit_price NUMERIC(12,2) NOT NULL CHECK ( unit_price >= 0 ),
        CONSTRAINT fk_order
            FOREIGN KEY(order_id)
            references orders(id)
            ON DELETE CASCADE
    );

CREATE INDEX idx_orders_customer
    ON orders(customer_id);

CREATE INDEX idx_order_items_order
    ON order_items(order_id);