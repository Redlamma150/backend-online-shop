-- Create users table
-- USERS table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- ITEMS table
CREATE TABLE items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    stock INT NOT NULL
);

-- ORDERS table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    date TIMESTAMP NOT NULL,
    address VARCHAR(255),
    total_price DOUBLE DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- FAVORITE table
CREATE TABLE favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    UNIQUE (user_id, item_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (item_id) REFERENCES items(id)
);

-- ORDER_ITEMS table
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DOUBLE NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (item_id) REFERENCES items(id)
);

INSERT INTO items (id, title, image_url, price, stock) VALUES
(1, 'T-Shirt Classic White', 'https://imgur.com/4AsZ9Ja', 19.99, 50),
(2, 'Running Shoes', 'https://imgur.com/phavlpF', 79.99, 30),
(3, 'Sunglasses Aviator', 'https://imgur.com/err9MoG', 49.99, 20),
(4, 'Backpack 25L', 'https://imgur.com/Fzdf2ZJ', 39.99, 15),
(5, 'Wireless Earbuds', 'https://imgur.com/cUappqq', 59.99, 40),
(6, 'Fitness Watch', 'https://imgur.com/pcOEu7o', 99.99, 25),
(7, 'Baseball Cap', 'https://imgur.com/3m4HNMa', 14.99, 60),
(8, 'Leather Wallet', 'https://imgur.com/WLXnLcy', 29.99, 18),
(9, 'Gaming Mouse', 'https://imgur.com/oueHwhU', 44.99, 22),
(10, 'Laptop Stand', 'https://imgur.com/VRWhGzl', 34.99, 27);


INSERT INTO users (first_name, last_name, email, phone, address, username, password) VALUES
('David', 'Cohen', 'david@example.com', '0521234567', 'Israel, Tel Aviv','David1234', '1234'),
('Sarah', 'Levi', 'sarah@example.com', '0529876543', 'Israel, Haifa','Sarah5678', '5678'),
('John', 'Smith', 'john.smith@example.com', '0501231234', 'USA, New York','JohnPassword', 'password'),
('Maya', 'Ben-Ami', 'maya@example.com', '0534567890', 'Israel, Jerusalem', 'Mayaabcd1234', 'abcd1234');








