package com.sirekam.dao;

import com.sirekam.model.Kunjungan;
import com.sirekam.model.enums.StatusKunjungan;
import com.sirekam.util.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KunjunganDAO implements GenericDAO<Kunjungan> {

    private DatabaseManager dbManager;

    public KunjunganDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public boolean save(Kunjungan kunjungan) throws SQLException {
        String sql = "INSERT INTO tb_kunjungan (id_pasien, id_dokter, keluhan, status) " +
                "VALUES (?, ?, ?, ?)";
        int result = dbManager.executeUpdate(sql,
                kunjungan.getIdPasien(),
                kunjungan.getIdDokter(),
                kunjungan.getKeluhan(),
                kunjungan.getStatus().name()
        );
        return result > 0;
    }

    public int saveAndGetId(Kunjungan kunjungan) throws SQLException {
        String sql = "INSERT INTO tb_kunjungan (id_pasien, id_dokter, keluhan, status) " +
                "VALUES (?, ?, ?, ?)";
        return dbManager.executeUpdateWithGeneratedKey(sql,
                kunjungan.getIdPasien(),
                kunjungan.getIdDokter(),
                kunjungan.getKeluhan(),
                kunjungan.getStatus().name()
        );
    }

    @Override
    public Kunjungan findById(int id) throws SQLException {
        String sql = "SELECT k.*, p.nama as nama_pasien, d.nama_dokter as nama_dokter " +
                "FROM tb_kunjungan k " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON k.id_dokter = d.id_dokter " +
                "WHERE k.id_kunjungan = ?";
        ResultSet rs = dbManager.executeQuery(sql, id);
        if (rs.next()) {
            return mapResultSetToKunjungan(rs);
        }
        return null;
    }

    @Override
    public List<Kunjungan> findAll() throws SQLException {
        List<Kunjungan> list = new ArrayList<>();
        String sql = "SELECT k.*, p.nama as nama_pasien, d.nama_dokter as nama_dokter " +
                "FROM tb_kunjungan k " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON k.id_dokter = d.id_dokter " +
                "ORDER BY k.tanggal_kunjungan DESC";
        ResultSet rs = dbManager.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSetToKunjungan(rs));
        }
        return list;
    }

    @Override
    public boolean update(Kunjungan kunjungan) throws SQLException {
        String sql = "UPDATE tb_kunjungan SET id_pasien = ?, id_dokter = ?, " +
                "keluhan = ?, status = ? WHERE id_kunjungan = ?";
        int result = dbManager.executeUpdate(sql,
                kunjungan.getIdPasien(),
                kunjungan.getIdDokter(),
                kunjungan.getKeluhan(),
                kunjungan.getStatus().name(),
                kunjungan.getIdKunjungan()
        );
        return result > 0;
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tb_kunjungan WHERE id_kunjungan = ?";
        int result = dbManager.executeUpdate(sql, id);
        return result > 0;
    }

    @Override
    public List<Kunjungan> search(String keyword) throws SQLException {
        List<Kunjungan> list = new ArrayList<>();
        String sql = "SELECT k.*, p.nama as nama_pasien, d.nama_dokter as nama_dokter " +
                "FROM tb_kunjungan k " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON k.id_dokter = d.id_dokter " +
                "WHERE p.nama LIKE ? OR p.id_pasien LIKE ? " +
                "ORDER BY k.tanggal_kunjungan DESC";
        ResultSet rs = dbManager.executeQuery(sql, "%" + keyword + "%", "%" + keyword + "%");
        while (rs.next()) {
            list.add(mapResultSetToKunjungan(rs));
        }
        return list;
    }

    public List<Kunjungan> findByDokter(int idDokter) throws SQLException {
        List<Kunjungan> list = new ArrayList<>();
        String sql = "SELECT k.*, p.nama as nama_pasien, d.nama_dokter as nama_dokter " +
                "FROM tb_kunjungan k " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON k.id_dokter = d.id_dokter " +
                "WHERE k.id_dokter = ? " +
                "AND k.status IN ('menunggu', 'diperiksa') " +
                "ORDER BY k.tanggal_kunjungan DESC";
        ResultSet rs = dbManager.executeQuery(sql, idDokter);
        while (rs.next()) {
            list.add(mapResultSetToKunjungan(rs));
        }
        return list;
    }

    public List<Kunjungan> findByPasien(String idPasien) throws SQLException {
        List<Kunjungan> list = new ArrayList<>();
        String sql = "SELECT k.*, p.nama as nama_pasien, d.nama_dokter as nama_dokter " +
                "FROM tb_kunjungan k " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON k.id_dokter = d.id_dokter " +
                "WHERE k.id_pasien = ? " +
                "ORDER BY k.tanggal_kunjungan DESC";
        ResultSet rs = dbManager.executeQuery(sql, idPasien);
        while (rs.next()) {
            list.add(mapResultSetToKunjungan(rs));
        }
        return list;
    }

    public boolean updateStatus(int idKunjungan, StatusKunjungan status) throws SQLException {
        String sql = "UPDATE tb_kunjungan SET status = ? WHERE id_kunjungan = ?";
        int result = dbManager.executeUpdate(sql, status.name(), idKunjungan);
        return result > 0;
    }

    public List<Kunjungan> getKunjunganMenunggu() throws SQLException {
        List<Kunjungan> list = new ArrayList<>();
        String sql = "SELECT k.*, p.nama as nama_pasien, d.nama_dokter as nama_dokter " +
                "FROM tb_kunjungan k " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON k.id_dokter = d.id_dokter " +
                "WHERE k.status = 'menunggu' " +
                "ORDER BY k.tanggal_kunjungan ASC";
        ResultSet rs = dbManager.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSetToKunjungan(rs));
        }
        return list;
    }

    private Kunjungan mapResultSetToKunjungan(ResultSet rs) throws SQLException {
        Kunjungan k = new Kunjungan();
        k.setIdKunjungan(rs.getInt("id_kunjungan"));
        k.setIdPasien(rs.getString("id_pasien"));
        k.setIdDokter(rs.getInt("id_dokter"));
        k.setKeluhan(rs.getString("keluhan"));
        k.setTanggalKunjungan(rs.getTimestamp("tanggal_kunjungan").toLocalDateTime());
        k.setStatus(StatusKunjungan.valueOf(rs.getString("status").toUpperCase()));
        k.setNamaPasien(rs.getString("nama_pasien"));
        k.setNamaDokter(rs.getString("nama_dokter"));
        return k;
    }
}