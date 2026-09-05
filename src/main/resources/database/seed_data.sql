-- =====================================================
-- SIREKAM - Seed Data
-- =====================================================

USE sirekam;

-- 1. Insert User (password: password123)
INSERT INTO tb_user (username, password, nama_lengkap, role) VALUES
                                                                 ('petugas1', 'password123', 'Fatur', 'pendaftaran'),
                                                                 ('dr_grace', 'password123', 'dr. Grace, Sp.PD', 'dokter'),
                                                                 ('dr_sovia', 'password123', 'dr. Sovia, Sp.KJ', 'dokter'),
                                                                 ('apoteker1', 'password123', 'apt. Maezar, S.Farm.', 'apoteker');

-- 2. Insert Dokter
INSERT INTO tb_dokter (nama_dokter, spesialisasi, id_user) VALUES
                                                               ('dr. Grace, Sp.PD', 'Penyakit Dalam', 2),
                                                               ('dr. Sovia, Sp.KJ', 'Kesehatan Jiwa', 3);

-- 3. Insert Obat
INSERT INTO tb_obat (nama_obat, satuan, stok, harga_satuan) VALUES
                                                                ('Paracetamol 500mg', 'tablet', 100, 5000),
                                                                ('Amoxicillin 500mg', 'kapsul', 50, 15000),
                                                                ('Cetirizine 10mg', 'tablet', 75, 8000),
                                                                ('Sanmol', 'tablet', 200, 3500),
                                                                ('Antimo', 'tablet', 60, 12000),
                                                                ('OBH', 'botol', 30, 25000),
                                                                ('Proris', 'tablet', 90, 7000);

-- 4. Insert Contoh Pasien
INSERT INTO tb_pasien (id_pasien, nama, tanggal_lahir, jenis_kelamin, alamat, no_hp, jenis_asuransi) VALUES
                                                                                                         ('RM-0001', 'Rohman', '1980-01-15', 'L', 'Jl. Mawar No. 10, Jakarta', '081234567890', 'REGULER'),
                                                                                                         ('RM-0002', 'Ginting', '1995-07-22', 'P', 'Jl. Melati No. 5, Bandung', '085678901234', 'BPJS'),
                                                                                                         ('RM-0003', 'Ardhanta', '2000-03-03', 'L', 'Jl. Kenanga No. 7, Surabaya', '087812345678', 'ASURANSI'),
                                                                                                         ('RM-0004', 'Putra', '1988-11-12', 'L', 'Jl. Anggrek No. 3, Yogyakarta', '089876543210', 'REGULER');