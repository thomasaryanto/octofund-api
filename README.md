# OctoFund

Platform Investasi Reksadana berbasis website.

## Installation

```bash
npm install
```

## First Run

Untuk membuat akun admin pertama kali,
Lakukan post data ke rest api octofund di endpoint : localhost:8080/users/admin
dengan data sebagai berikut

```json
{
  "name": "Seto Lesmono",
  "phone": "08123456789",
  "username": "administrator",
  "email": "admin@gmail.com",
  "password": "passwordhere"
}
```

## Role List

Admin => Mengatur Manajer Invetasi, Report, KYC Member

Manager => Mengatur Reksadana, Paket, Konfirmasi Transaksi, Report

Member => Melakukan Jual Beli, Report.
