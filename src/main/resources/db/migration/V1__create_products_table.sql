CREATE TABLE products (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(4096),
    price DECIMAL(10, 2) DEFAULT 0 CHECK (price >= 0),
    is_available BOOLEAN DEFAULT FALSE
);
