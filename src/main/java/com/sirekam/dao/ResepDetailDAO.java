package com.sirekam.dao;

import com.sirekam.model.ResepDetail;
import com.sirekam.util.DatabaseManager;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResepDetailDAO implements GenericDAO<ResepDetail> {

    private DatabaseManager dbManager;

    public ResepDetailDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public boolean save(ResepDetail detail) throws SQLException {
        String sql = "INSERT INTO tb_resep_detail (id_resep, id_obat, jumlah) VALUES (?, ?, ?)";
        int result = dbManager.executeUpdate(sql,
                detail.getIdResep(),
                detail.getIdObat(),
                detail.getJumlah()
        );
        return result > 0;
    }

    public int saveAndGetId(ResepDetail detail) throws SQLException {
        String sql = "INSERT INTO tb_resep_detail (id_resep, id_obat, jumlah) VALUES (?, ?, ?)";
        return dbManager.executeUpdateWithGeneratedKey(sql,
                detail.getIdResep(),
                detail.getIdObat(),
                detail.getJumlah()
        );
    }

    @Override
    public ResepDetail findById(int id) throws SQLException {
        String sql = "SELECT rd.*, o.nama_obat, o.satuan, o.harga_satuan " +
                "FROM tb_resep_detail rd " +
                "JOIN tb_obat o ON rd.id_obat = o.id_obat " +
                "WHERE rd.id_resep_detail = ?";
        ResultSet rs = dbManager.executeQuery(sql, id);
        if (rs.next()) {
            return mapResultSetToResepDetail(rs);
        }
        return null;
    }

    @Override
    public List<ResepDetail> findAll() throws SQLException {
        List<ResepDetail> list = new ArrayList<>();
        String sql = "SELECT rd.*, o.nama_obat, o.satuan, o.harga_satuan " +
                "FROM tb_resep_detail rd " +
                "JOIN tb_obat o ON rd.id_obat = o.id_obat " +
                "ORDER BY rd.id_resep_detail ASC";
        ResultSet rs = dbManager.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSetToResepDetail(rs));
        }
        return list;
    }

    @Override
    public boolean update(ResepDetail detail) throws SQLException {
        String sql = "UPDATE tb_resep_detail SET id_resep = ?, id_obat = ?, jumlah = ? " +
                "WHERE id_resep_detail = ?";
        int result = dbManager.executeUpdate(sql,
                detail.getIdResep(),
                detail.getIdObat(),
                detail.getJumlah(),
                detail.getIdResepDetail()
        );
        return result > 0;
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tb_resep_detail WHERE id_resep_detail = ?";
        int result = dbManager.executeUpdate(sql, id);
        return result > 0;
    }

    public boolean deleteByResep(int idResep) throws SQLException {
        String sql = "DELETE FROM tb_resep_detail WHERE id_resep = ?";
        int result = dbManager.executeUpdate(sql, idResep);
        return result > 0;
    }

    @Override
    public List<ResepDetail> search(String keyword) throws SQLException {
        List<ResepDetail> list = new ArrayList<>();
        String sql = "SELECT rd.*, o.nama_obat, o.satuan, o.harga_satuan " +
                "FROM tb_resep_detail rd " +
                "JOIN tb_obat o ON rd.id_obat = o.id_obat " +
                "WHERE o.nama_obat LIKE ? " +
                "ORDER BY rd.id_resep_detail ASC";
        ResultSet rs = dbManager.executeQuery(sql, "%" + keyword + "%");
        while (rs.next()) {
            list.add(mapResultSetToResepDetail(rs));
        }
        return list;
    }

    public List<ResepDetail> findByResep(int idResep) throws SQLException {
        List<ResepDetail> list = new ArrayList<>();
        String sql = "SELECT rd.*, o.nama_obat, o.satuan, o.harga_satuan " +
                "FROM tb_resep_detail rd " +
                "JOIN tb_obat o ON rd.id_obat = o.id_obat " +
                "WHERE rd.id_resep = ? " +
                "ORDER BY rd.id_resep_detail ASC";
        ResultSet rs = dbManager.executeQuery(sql, idResep);
        while (rs.next()) {
            list.add(mapResultSetToResepDetail(rs));
        }
        return list;
    }

    public List<ResepDetail> findByObat(int idObat) throws SQLException {
        List<ResepDetail> list = new ArrayList<>();
        String sql = "SELECT rd.*, o.nama_obat, o.satuan, o.harga_satuan " +
                "FROM tb_resep_detail rd " +
                "JOIN tb_obat o ON rd.id_obat = o.id_obat " +
                "WHERE rd.id_obat = ? " +
                "ORDER BY rd.id_resep_detail ASC";
        ResultSet rs = dbManager.executeQuery(sql, idObat);
        while (rs.next()) {
            list.add(mapResultSetToResepDetail(rs));
        }
        return list;
    }

    public int countByResep(int idResep) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM tb_resep_detail WHERE id_resep = ?";
        ResultSet rs = dbManager.executeQuery(sql, idResep);
        if (rs.next()) {
            return rs.getInt("total");
        }
        return 0;
    }

    public BigDecimal getTotalHargaByResep(int idResep) throws SQLException {
        String sql = "SELECT SUM(o.harga_satuan * rd.jumlah) as total " +
                "FROM tb_resep_detail rd " +
                "JOIN tb_obat o ON rd.id_obat = o.id_obat " +
                "WHERE rd.id_resep = ?";
        ResultSet rs = dbManager.executeQuery(sql, idResep);
        if (rs.next()) {
            return rs.getBigDecimal("total");
        }
        return BigDecimal.ZERO;
    }



    private ResepDetail mapResultSetToResepDetail(ResultSet rs) throws SQLException {
        ResepDetail rd = new ResepDetail();
        rd.setIdResepDetail(rs.getInt("id_resep_detail"));
        rd.setIdResep(rs.getInt("id_resep"));
        rd.setIdObat(rs.getInt("id_obat"));
        rd.setJumlah(rs.getInt("jumlah"));
        rd.setNamaObat(rs.getString("nama_obat"));
        rd.setSatuan(rs.getString("satuan"));
        rd.setHargaSatuan(rs.getBigDecimal("harga_satuan"));
        return rd;
    }
}