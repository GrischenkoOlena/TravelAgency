CREATE TABLE IF NOT EXISTS users
(
    id          uuid PRIMARY KEY,
    username   VARCHAR(255),
    password    VARCHAR(255),
    role        VARCHAR(20),
    phone_number VARCHAR(10),
    balance     DOUBLE,
    account_status BOOLEAN
);

CREATE TABLE IF NOT EXISTS vouchers
(
    id          uuid PRIMARY KEY,
    title       VARCHAR(100),
    description VARCHAR(255),
    price       DOUBLE,
    tour_type    INTEGER,
    transfer_type INTEGER,
    hotel_type   INTEGER,
    arrival_date DATE,
    eviction_date DATE,
    is_hot       BOOLEAN
);

CREATE TABLE IF NOT EXISTS orders
(
    id          uuid PRIMARY KEY,
    user_id     uuid,
    voucher_id  uuid,
    status      INTEGER,
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id),
    CONSTRAINT fk_voucher FOREIGN KEY(voucher_id) REFERENCES vouchers(id)
);

