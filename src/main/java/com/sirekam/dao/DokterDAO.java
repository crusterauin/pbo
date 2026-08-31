package com.sirekam.dao;

import com.sirekam.model.Dokter;
import com.sirekam.util.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DokterDAO implements GenericDAO<Dokter> {

    private DatabaseManager dbManager;

    public DokterDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public boolean save(Dokter dokter) throws SQLException {
        String sql = "INSERT INTO tb_dokter (nama_dokter, spesialisasi, id_user) VALUES (?, ?, ?)";
        int result = dbManager.executeUpdate(sql,
                dokter.getNamaDokter(),
                dokter.getSpesialisasi(),
                dokter.getIdUser()
        );
        return result > 0;
    }

    @Override
    public Dokter findById(int id) throws SQLException {
        String sql = "SELECT * FROM tb_dokter WHERE id_dokter = ?";
        ResultSet rs = dbManager.executeQuery(sql, id);
        if (rs.next()) {
            return mapResultSetToDokter(rs);
        }
        return null;
    }

    @Override
    public List<Dokter> findAll() throws SQLException {
        List<Dokter> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_dokter ORDER BY nama_dokter ASC";
        ResultSet rs = dbManager.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSetToDokter(rs));
        }
        return list;
    }

    @Override
    public boolean update(Dokter dokter) throws SQLException {
        String sql = "UPDATE tb_dokter SET nama_dokter = ?, spesialisasi = ?, id_user = ? " +
                "WHERE id_dokter = ?";
        int result = dbManager.executeUpdate(sql,
                dokter.getNamaDokter(),
                dokter.getSpesialisasi(),
                dokter.getIdUser(),
                dokter.getIdDokter()
        );
        return result > 0;
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tb_dokter WHERE id_dokter = ?";
        int result = dbManager.executeUpdate(sql, id);
        return result > 0;
    }

    @Override
    public List<Dokter> search(String keyword) throws SQLException {
        List<Dokter> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_dokter WHERE nama_dokter LIKE ? ORDER BY nama_dokter ASC";
        ResultSet rs = dbManager.executeQuery(sql, "%" + keyword + "%");
        while (rs.next()) {
            list.add(mapResultSetToDokter(rs));
        }
        return list;
    }

    public Dokter findByUserId(int idUser) throws SQLException {
        String sql = "SELECT * FROM tb_dokter WHERE id_user = ?";
        ResultSet rs = dbManager.executeQuery(sql, idUser);
        if (rs.next()) {
            return mapResultSetToDokter(rs);
        }
        return null;
    }

    private Dokter mapResultSetToDokter(ResultSet rs) throws SQLException {
        Dokter d = new Dokter();
        d.setIdDokter(rs.getInt("id_dokter"));
        d.setNamaDokter(rs.getString("nama_dokter"));
        d.setSpesialisasi(rs.getString("spesialisasi"));
        d.setIdUser(rs.getInt("id_user"));
        return d;
    }
}