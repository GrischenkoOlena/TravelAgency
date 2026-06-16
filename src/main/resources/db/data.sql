INSERT INTO users (id, username, password, role, phone_number, balance, account_status)
    VALUES (RANDOM_UUID(),'admin Nik', 'adminPass', 'ADMIN', null, 0.00,true);
INSERT INTO users (id, username, password, role, phone_number, balance, account_status)
    VALUES (RANDOM_UUID(),'manager Phil', 'pass', 'MANAGER', null, 2000.00,true);
INSERT INTO users (id, username, password, role, phone_number, balance, account_status)
    VALUES ('ca2198ef-3099-45ed-a148-7daeae3cb4c0','guest', 'password', 'USER', null, 5000.00, true);


INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type,
                     hotel_type, arrival_date, eviction_date, is_hot)
    VALUES ('fb56792f-2c9f-405d-b5e1-bf4b45279123','summer','good summer tour', 100.00, 1, 1,
            2, '2026-06-15', '2026-06-25',false);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type,
                     hotel_type, arrival_date, eviction_date, is_hot)
    VALUES (RANDOM_UUID(),'two country','good tour', 200.00, 3, 2,
        1,  '2026-06-16', '2026-06-27',false);
INSERT INTO vouchers (id, title, description, price, tour_type, transfer_type,
                     hotel_type, arrival_date, eviction_date, is_hot)
    VALUES (RANDOM_UUID(),'wine tour','good wine tour', 400.00, 4, 4,
        3,  '2026-06-20', '2026-06-30',false);


INSERT INTO orders (id, user_id, voucher_id, status)
    VALUES (RANDOM_UUID(), 'ca2198ef-3099-45ed-a148-7daeae3cb4c0', 'fb56792f-2c9f-405d-b5e1-bf4b45279123', 0);