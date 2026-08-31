package com.sirekam.dao;

import com.sirekam.model.Resep;
import com.sirekam.model.enums.StatusResep;
import com.sirekam.util.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResepDAO implements GenericDAO<Resep> {

    private DatabaseManager dbManager;

    public ResepDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public boolean save(Resep resep) throws SQLException {
        String sql = "INSERT INTO tb_resep (id_kunjungan, id_dokter, obat_dan_perlakuan, status_resep) " +
                "VALUES (?, ?, ?, ?)";
        int result = dbManager.executeUpdate(sql,
                resep.getIdKunjungan(),
                resep.getIdDokter(),
                resep.getObatDanPerlakuan(),
                resep.getStatusResep().name()
        );
        return result > 0;
    }

    public int saveAndGetId(Resep resep) throws SQLException {
        String sql = "INSERT INTO tb_resep (id_kunjungan, id_dokter, obat_dan_perlakuan, status_resep) " +
                "VALUES (?, ?, ?, ?)";
        return dbManager.executeUpdateWithGeneratedKey(sql,
                resep.getIdKunjungan(),
                resep.getIdDokter(),
                resep.getObatDanPerlakuan(),
                resep.getStatusResep().name()
        );
    }

    @Override
    public Resep findById(int id) throws SQLException {
        String sql = "SELECT r.*, p.nama as nama_pasien, d.nama_dokter as nama_dokter " +
                "FROM tb_resep r " +
                "JOIN tb_kunjungan k ON r.id_kunjungan = k.id_kunjungan " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON r.id_dokter = d.id_dokter " +
                "WHERE r.id_resep = ?";
        ResultSet rs = dbManager.executeQuery(sql, id);
        if (rs.next()) {
            return mapResultSetToResep(rs);
        }
        return null;
    }

    @Override
    public List<Resep> findAll() throws SQLException {
        List<Resep> list = new ArrayList<>();
        String sql = "SELECT r.*, p.nama as nama_pasien, d.nama_dokter as nama_dokter " +
                "FROM tb_resep r " +
                "JOIN tb_kunjungan k ON r.id_kunjungan = k.id_kunjungan " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON r.id_dokter = d.id_dokter " +
                "ORDER BY r.created_at DESC";
        ResultSet rs = dbManager.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSetToResep(rs));
        }
        return list;
    }

    @Override
    public boolean update(Resep resep) throws SQLException {
        String sql = "UPDATE tb_resep SET id_kunjungan = ?, id_dokter = ?, " +
                "obat_dan_perlakuan = ?, status_resep = ? WHERE id_resep = ?";
        int result = dbManager.executeUpdate(sql,
                resep.getIdKunjungan(),
                resep.getIdDokter(),
                resep.getObatDanPerlakuan(),
                resep.getStatusResep().name(),
                resep.getIdResep()
        );
        return result > 0;
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tb_resep WHERE id_resep = ?";
        int result = dbManager.executeUpdate(sql, id);
        return result > 0;
    }

    @Override
    public List<Resep> search(String keyword) throws SQLException {
        List<Resep> list = new ArrayList<>();
        String sql = "SELECT r.*, p.nama as nama_pasien, d.nama_dokter as nama_dokter " +
                "FROM tb_resep r " +
                "JOIN tb_kunjungan k ON r.id_kunjungan = k.id_kunjungan " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON r.id_dokter = d.id_dokter " +
                "WHERE p.nama LIKE ? OR r.obat_dan_perlakuan LIKE ? " +
                "ORDER BY r.created_at DESC";
        ResultSet rs = dbManager.executeQuery(sql, "%" + keyword + "%", "%" + keyword + "%");
        while (rs.next()) {
            list.add(mapResultSetToResep(rs));
        }
        return list;
    }

    public List<Resep> getResepMenunggu() throws SQLException {
        List<Resep> list = new ArrayList<>();
        String sql = "SELECT r.*, p.nama as nama_pasien, d.nama_dokter as nama_dokter " +
                "FROM tb_resep r " +
                "JOIN tb_kunjungan k ON r.id_kunjungan = k.id_kunjungan " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON r.id_dokter = d.id_dokter " +
                "WHERE r.status_resep = 'menunggu' " +
                "ORDER BY r.created_at ASC";
        ResultSet rs = dbManager.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSetToResep(rs));
        }
        return list;
    }

    public List<Resep> findByDokter(int idDokter) throws SQLException {
        List<Resep> list = new ArrayList<>();
        String sql = "SELECT r.*, p.nama as nama_pasien, d.nama_dokter as nama_dokter " +
                "FROM tb_resep r " +
                "JOIN tb_kunjungan k ON r.id_kunjungan = k.id_kunjungan " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON r.id_dokter = d.id_dokter " +
                "WHERE r.id_dokter = ? " +
                "ORDER BY r.created_at DESC";
        ResultSet rs = dbManager.executeQuery(sql, idDokter);
        while (rs.next()) {
            list.add(mapResultSetToResep(rs));
        }
        return list;
    }

    public boolean updateStatus(int idResep, StatusResep status) throws SQLException {
        String sql = "UPDATE tb_resep SET status_resep = ? WHERE id_resep = ?";
        int result = dbManager.executeUpdate(sql, status.name(), idResep);
        return result > 0;
    }

    private Resep mapResultSetToResep(ResultSet rs) throws SQLException {
        Resep r = new Resep();
        r.setIdResep(rs.getInt("id_resep"));
        r.setIdKunjungan(rs.getInt("id_kunjungan"));
        r.setIdDokter(rs.getInt("id_dokter"));
        r.setObatDanPerlakuan(rs.getString("obat_dan_perlakuan"));
        r.setStatusResep(StatusResep.valueOf(rs.getString("status_resep").toUpperCase()));
        r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        r.setNamaPasien(rs.getString("nama_pasien"));
        r.setNamaDokter(rs.getString("nama_dokter"));
        return r;
    }

    public List<Resep> getResepDiproses() throws SQLException {
        List<Resep> list = new ArrayList<>();
        String sql = "SELECT r.*, p.nama as nama_pasien, d.nama_dokter as nama_dokter " +
                "FROM tb_resep r " +
                "JOIN tb_kunjungan k ON r.id_kunjungan = k.id_kunjungan " +
                "JOIN tb_pasien p ON k.id_pasien = p.id_pasien " +
                "JOIN tb_dokter d ON r.id_dokter = d.id_dokter " +
                "WHERE r.status_resep = 'diproses_apoteker' " +
                "ORDER BY r.created_at ASC";
        ResultSet rs = dbManager.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSetToResep(rs));
        }
        return list;
    }
}