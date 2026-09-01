package com.sirekam.dao;

import com.sirekam.model.Struk;
import com.sirekam.util.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StrukDAO implements GenericDAO<Struk> {

    private DatabaseManager dbManager;

    public StrukDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public boolean save(Struk struk) throws SQLException {
        String sql = "INSERT INTO tb_struk (id_resep, biaya_konsultasi, biaya_obat, total_bayar) " +
                "VALUES (?, ?, ?, ?)";
        int result = dbManager.executeUpdate(sql,
                struk.getIdResep(),
                struk.getBiayaKonsultasi(),
                struk.getBiayaObat(),
                struk.getTotalBayar()
        );
        return result > 0;
    }

    public int saveAndGetId(Struk struk) throws SQLException {
        String sql = "INSERT INTO tb_struk (id_resep, biaya_konsultasi, biaya_obat, total_bayar) " +
                "VALUES (?, ?, ?, ?)";
        return dbManager.executeUpdateWithGeneratedKey(sql,
                struk.getIdResep(),
                struk.getBiayaKonsultasi(),
                struk.getBiayaObat(),
                struk.getTotalBayar()
        );
    }

    @Override
    public Struk findById(int id) throws SQLException {
        String sql = "SELECT s.*, r.id_kunjungan, p.nama as nama_pasien, " +
                "d.nama_dokter as nama_dokter, p.jenis_asuransi " +  // ← TAMBAHKAN
                "FROM tb_struk s " +
                "JOIN tb_resep r ON s.id_resep = r.id_resep " +
                "JOIN tb_kunjungan k ON r.id_kunjungan = k.id_kunjungan " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON k.id_dokter = d.id_dokter " +
                "WHERE s.id_struk = ?";
        ResultSet rs = dbManager.executeQuery(sql, id);
        if (rs.next()) {
            Struk struk = mapResultSetToStruk(rs);
            struk.setJenisAsuransi(rs.getString("jenis_asuransi"));  // ← TAMBAHKAN
            return struk;
        }
        return null;
    }

    @Override
    public List<Struk> findAll() throws SQLException {
        List<Struk> list = new ArrayList<>();
        String sql = "SELECT s.*, r.id_kunjungan, p.nama as nama_pasien, d.nama_dokter as nama_dokter " +
                "FROM tb_struk s " +
                "JOIN tb_resep r ON s.id_resep = r.id_resep " +
                "JOIN tb_kunjungan k ON r.id_kunjungan = k.id_kunjungan " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON k.id_dokter = d.id_dokter " +
                "ORDER BY s.waktu_cetak DESC";
        ResultSet rs = dbManager.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSetToStruk(rs));
        }
        return list;
    }

    @Override
    public boolean update(Struk struk) throws SQLException {
        String sql = "UPDATE tb_struk SET id_resep = ?, biaya_konsultasi = ?, " +
                "biaya_obat = ?, total_bayar = ? WHERE id_struk = ?";
        int result = dbManager.executeUpdate(sql,
                struk.getIdResep(),
                struk.getBiayaKonsultasi(),
                struk.getBiayaObat(),
                struk.getTotalBayar(),
                struk.getIdStruk()
        );
        return result > 0;
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tb_struk WHERE id_struk = ?";
        int result = dbManager.executeUpdate(sql, id);
        return result > 0;
    }

    @Override
    public List<Struk> search(String keyword) throws SQLException {
        List<Struk> list = new ArrayList<>();
        String sql = "SELECT s.*, r.id_kunjungan, p.nama as nama_pasien, d.nama_dokter as nama_dokter " +
                "FROM tb_struk s " +
                "JOIN tb_resep r ON s.id_resep = r.id_resep " +
                "JOIN tb_kunjungan k ON r.id_kunjungan = k.id_kunjungan " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON k.id_dokter = d.id_dokter " +
                "WHERE p.nama LIKE ? " +
                "ORDER BY s.waktu_cetak DESC";
        ResultSet rs = dbManager.executeQuery(sql, "%" + keyword + "%");
        while (rs.next()) {
            list.add(mapResultSetToStruk(rs));
        }
        return list;
    }

    public List<Struk> findByResep(int idResep) throws SQLException {
        List<Struk> list = new ArrayList<>();
        String sql = "SELECT s.*, r.id_kunjungan, p.nama as nama_pasien, d.nama_dokter as nama_dokter " +
                "FROM tb_struk s " +
                "JOIN tb_resep r ON s.id_resep = r.id_resep " +
                "JOIN tb_kunjungan k ON r.id_kunjungan = k.id_kunjungan " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON k.id_dokter = d.id_dokter " +
                "WHERE s.id_resep = ?";
        ResultSet rs = dbManager.executeQuery(sql, idResep);
        while (rs.next()) {
            list.add(mapResultSetToStruk(rs));
        }
        return list;
    }

    private Struk mapResultSetToStruk(ResultSet rs) throws SQLException {
        Struk s = new Struk();
        s.setIdStruk(rs.getInt("id_struk"));
        s.setIdResep(rs.getInt("id_resep"));
        s.setBiayaKonsultasi(rs.getBigDecimal("biaya_konsultasi"));
        s.setBiayaObat(rs.getBigDecimal("biaya_obat"));
        s.setTotalBayar(rs.getBigDecimal("total_bayar"));
        s.setWaktuCetak(rs.getTimestamp("waktu_cetak").toLocalDateTime());
        s.setNamaPasien(rs.getString("nama_pasien"));
        s.setNamaDokter(rs.getString("nama_dokter"));
        return s;
    }

    public boolean deleteByResep(int idResep) throws SQLException {
        String sql = "DELETE FROM tb_struk WHERE id_resep = ?";
        int result = dbManager.executeUpdate(sql, idResep);
        return result > 0;
    }

    public boolean deleteByKunjungan(int idKunjungan) throws SQLException {
        String sql = "DELETE FROM tb_resep WHERE id_kunjungan = ?";
        int result = dbManager.executeUpdate(sql, idKunjungan);
        return result > 0;
    }

    public List<Object[]> getRiwayatTransaksi() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT " +
                "p.nama AS nama_pasien, " +
                "o.nama_obat, " +
                "rd.jumlah AS kuantitas, " +
                "s.waktu_cetak AS tanggal_transaksi " +
                "FROM tb_struk s " +
                "JOIN tb_resep r ON s.id_resep = r.id_resep " +
                "JOIN tb_kunjungan k ON r.id_kunjungan = k.id_kunjungan " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_resep_detail rd ON rd.id_resep = r.id_resep " +
                "JOIN tb_obat o ON rd.id_obat = o.id_obat " +
                "ORDER BY s.waktu_cetak DESC";
        ResultSet rs = dbManager.executeQuery(sql);
        while (rs.next()) {
            Object[] row = new Object[4];
            row[0] = rs.getString("nama_pasien");
            row[1] = rs.getString("nama_obat");
            row[2] = rs.getInt("kuantitas");
            row[3] = rs.getTimestamp("tanggal_transaksi").toLocalDateTime();
            list.add(row);
        }
        return list;
    }
}