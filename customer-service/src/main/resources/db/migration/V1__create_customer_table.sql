CREATE TABLE customers
(
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone VARCHAR(20),
    birth_date DATE,
    created_at TIMESTAMPTZ NOT NULL,
    update_at TIMESTAMPTZ NOT NULL,
)