INSERT INTO role (id, name) VALUES (1, 'Admin');
INSERT INTO role (id, name) VALUES (2, 'Staff');
INSERT INTO role (id, name) VALUES (3, 'Manager');
INSERT INTO role (id, name) VALUES (4, 'Member');

INSERT INTO bank (id, name, logo, short_name) VALUES (1, 'BANK CIMB Niaga', null, 'CIMB NIAGA');

INSERT INTO mutual_fund_category (id, name) VALUES (1, 'Pasar Uang');
INSERT INTO mutual_fund_category (id, name) VALUES (2, 'Pendapatan Tetap');
INSERT INTO mutual_fund_category (id, name) VALUES (3, 'Campuran');
INSERT INTO mutual_fund_category (id, name) VALUES (4, 'Saham');

INSERT INTO mutual_fund_type (id, name) VALUES (1, 'Umum');
INSERT INTO mutual_fund_type (id, name) VALUES (2, 'Syariah');

INSERT INTO transaction_status (id, name) VALUES (1, 'Menunggu Pembayaran');
INSERT INTO transaction_status (id, name) VALUES (2, 'Menunggu Konfirmasi');
INSERT INTO transaction_status (id, name) VALUES (3, 'Pembayaran Ditolak');
INSERT INTO transaction_status (id, name) VALUES (4, 'Selesai');