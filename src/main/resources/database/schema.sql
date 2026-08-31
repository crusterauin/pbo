-- =====================================================
-- SIREKAM - Database Schema
-- =====================================================

CREATE DATABASE IF NOT EXISTS sirekam
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE sirekam;

-- 1. Tabel User
CREATE TABLE IF NOT EXISTS tb_user (
                                       id_user INT AUTO_INCREMENT PRIMARY KEY,
                                       username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    role ENUM('pendaftaran', 'dokter', 'apoteker') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB;

-- 2. Tabel Pasien
CREATE TABLE IF NOT EXISTS tb_pasien (
                                         id_pasien VARCHAR(15) PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    tanggal_lahir DATE,
    jenis_kelamin ENUM('L', 'P'),
    alamat TEXT,
    no_hp VARCHAR(20),
    jenis_asuransi ENUM('REGULER', 'BPJS', 'ASURANSI') DEFAULT 'REGULER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB;

-- 3. Tabel Dokter
CREATE TABLE IF NOT EXISTS tb_dokter (
                                         id_dokter INT AUTO_INCREMENT PRIMARY KEY,
                                         nama_dokter VARCHAR(100) NOT NULL,
    spesialisasi VARCHAR(100),
    id_user INT,
    FOREIGN KEY (id_user) REFERENCES tb_user(id_user) ON DELETE SET NULL
    ) ENGINE=InnoDB;

-- 4. Tabel Kunjungan
CREATE TABLE IF NOT EXISTS tb_kunjungan (
                                            id_kunjungan INT AUTO_INCREMENT PRIMARY KEY,
                                            id_pasien VARCHAR(15) NOT NULL,
    id_dokter INT NOT NULL,
    keluhan TEXT,
    tanggal_kunjungan DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('menunggu', 'diperiksa', 'selesai') DEFAULT 'menunggu',
    FOREIGN KEY (id_pasien) REFERENCES tb_pasien(id_pasien),
    FOREIGN KEY (id_dokter) REFERENCES tb_dokter(id_dokter)
    ) ENGINE=InnoDB;

-- 5. Tabel Obat
CREATE TABLE IF NOT EXISTS tb_obat (
                                       id_obat INT AUTO_INCREMENT PRIMARY KEY,
                                       nama_obat VARCHAR(100) NOT NULL,
    satuan VARCHAR(20),
    stok INT DEFAULT 0,
    harga_satuan DECIMAL(10,2) DEFAULT 0
    ) ENGINE=InnoDB;

-- 6. Tabel Resep
CREATE TABLE IF NOT EXISTS tb_resep (
                                        id_resep INT AUTO_INCREMENT PRIMARY KEY,
                                        id_kunjungan INT NOT NULL,
                                        id_dokter INT NOT NULL,
                                        obat_dan_perlakuan TEXT,
                                        status_resep ENUM('menunggu', 'diproses_apoteker', 'selesai') DEFAULT 'menunggu',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_kunjungan) REFERENCES tb_kunjungan(id_kunjungan),
    FOREIGN KEY (id_dokter) REFERENCES tb_dokter(id_dokter)
    ) ENGINE=InnoDB;

-- 7. Tabel Resep Detail
CREATE TABLE IF NOT EXISTS tb_resep_detail (
                                               id_resep_detail INT AUTO_INCREMENT PRIMARY KEY,
                                               id_resep INT NOT NULL,
                                               id_obat INT NOT NULL,
                                               jumlah INT DEFAULT 0,
                                               FOREIGN KEY (id_resep) REFERENCES tb_resep(id_resep),
    FOREIGN KEY (id_obat) REFERENCES tb_obat(id_obat)
    ) ENGINE=InnoDB;

-- 8. Tabel Struk
CREATE TABLE IF NOT EXISTS tb_struk (
                                        id_struk INT AUTO_INCREMENT PRIMARY KEY,
                                        id_resep INT NOT NULL,
                                        biaya_konsultasi DECIMAL(10,2) DEFAULT 0,
    biaya_obat DECIMAL(10,2) DEFAULT 0,
    total_bayar DECIMAL(10,2) DEFAULT 0,
    waktu_cetak DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_resep) REFERENCES tb_resep(id_resep)
    ) ENGINE=InnoDB;

-- 9. Tabel Chat
CREATE TABLE IF NOT EXISTS tb_chat (
                                       id_chat INT AUTO_INCREMENT PRIMARY KEY,
                                       jenis_chat ENUM('petugas_dokter', 'dokter_apoteker') NOT NULL,
    id_kunjungan INT NULL,
    id_resep INT NULL,
    id_pengirim INT NOT NULL,
    id_penerima INT NOT NULL,
    isi_pesan TEXT,
    waktu_kirim DATETIME DEFAULT CURRENT_TIMESTAMP,
    status_baca ENUM('terkirim', 'dibaca') DEFAULT 'terkirim',
    FOREIGN KEY (id_kunjungan) REFERENCES tb_kunjungan(id_kunjungan),
    FOREIGN KEY (id_resep) REFERENCES tb_resep(id_resep),
    FOREIGN KEY (id_pengirim) REFERENCES tb_user(id_user),
    FOREIGN KEY (id_penerima) REFERENCES tb_user(id_user)
    ) ENGINE=InnoDB;