INSERT INTO users (id, username, password, role, phone_number, balance, account_status)
    VALUES (RANDOM_UUID(),'adminNik', '$2a$12$DYTu11Xn8X0NjzNajOTQLeQvMD7jZuGJeNpxWH.8U3QakK02nB2vK',
            'ADMIN', null, 0.00,true);
INSERT INTO users (id, username, password, role, phone_number, balance, account_status)
    VALUES (RANDOM_UUID(),'managerPhil', '$2a$12$9cjn3Tu8Cs9EvQDds/XkCO2.ln0xZnozj8rtcH/L.9dinevjBf1dO',
            'MANAGER', null, 2000.00,true);
INSERT INTO users (id, username, password, role, phone_number, balance, account_status)
    VALUES ('ca2198ef-3099-45ed-a148-7daeae3cb4c0','guest',
            '$2a$10$hc0ust6BkOkGSCNemqWHC.zKgpCuEIN.Qx/2XTSYB11Qxot8bfDIS', 'USER', null, 5000.00, true);


INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type,
                     hotel_type, arrival_date, eviction_date, is_hot)
    VALUES ('fb56792f-2c9f-405d-b5e1-bf4b45279123','summer','good summer tour', 100.00, 1, 1,
            2, '2026-06-15', '2026-06-25',false);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type,
                     hotel_type, arrival_date, eviction_date, is_hot)
    VALUES (RANDOM_UUID(),'two country','good tour', 200.00, 3, 2,
        1,  '2026-06-16', '2026-06-27',false);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type, hotel_type, arrival_date, eviction_date, is_hot)
VALUES
    (RANDOM_UUID(), 'Alpine Escape', 'Skiing and winter sports in the Alps', 450.50, 2, 2, 4, '2026-12-01', '2026-12-10', false),
    (RANDOM_UUID(), 'Tropical Paradise', 'All-inclusive beach resort in Maldives', 1200.00, 1, 3, 4, '2026-07-05', '2026-07-15', true),
    (RANDOM_UUID(), 'City Explorer', 'Weekend cultural tour of Prague', 180.00, 3, 1, 3, '2026-09-18', '2026-09-21', false),
    (RANDOM_UUID(), 'Mediterranean Cruise', 'Luxury cruise around Greece and Italy', 899.99, 4, 3, 4, '2026-08-12', '2026-08-22', true),
    (RANDOM_UUID(), 'Safari Adventure', 'Wildlife tracking in Kenya', 750.00, 5, 2, 4, '2026-10-05', '2026-10-14', false),
    (RANDOM_UUID(), 'Autumn Romance', 'Cozy cabin weekend in the mountains', 150.00, 1, 1, 2, '2026-10-23', '2026-10-26', false),
    (RANDOM_UUID(), 'Tokyo Express', 'Fast-paced exploration of modern Japan', 950.00, 3, 3, 4, '2026-11-10', '2026-11-17', false),
    (RANDOM_UUID(), 'Last Minute Oasis', 'Quick budget get-away to Egypt', 220.00, 1, 1, 3, '2026-06-25', '2026-07-02', true),
    (RANDOM_UUID(), 'Nordic Lights', 'Chasing the Aurora Borealis in Norway', 620.00, 2, 2, 3, '2026-11-28', '2026-12-05', false),
    (RANDOM_UUID(), 'Historical Greece', 'Guided tour of Athens and ancient ruins', 340.00, 3, 1, 3, '2026-09-02', '2026-09-09', true);


INSERT INTO orders (id, user_id, voucher_id, status)
    VALUES (RANDOM_UUID(), 'ca2198ef-3099-45ed-a148-7daeae3cb4c0', 'fb56792f-2c9f-405d-b5e1-bf4b45279123', 0);