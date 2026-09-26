package com.sirekam.dao;

import com.sirekam.model.StokLog;
import com.sirekam.util.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class StokLogDAO {

    private final DatabaseManager dbManager;

    public StokLogDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean catat(int idObat, String jenis, int jumlah, String keterangan, Integer idUser) throws SQLException {
        String sql = "INSERT INTO tb_stok_log (id_obat, jenis, jumlah, keterangan, id_user) VALUES (?, ?, ?, ?, ?)";
        int result = dbManager.executeUpdate(sql, idObat, jenis, jumlah, keterangan, idUser);
        return result > 0;
    }

    public List<StokLog> findByJenis(String jenis) throws SQLException {
        List<StokLog> list = new ArrayList<>();
        String sql = "SELECT sl.*, o.nama_obat FROM tb_stok_log sl " +
                "JOIN tb_obat o ON sl.id_obat = o.id_obat " +
                "WHERE sl.jenis = ? ORDER BY sl.waktu DESC";
        ResultSet rs = dbManager.executeQuery(sql, jenis);
        while (rs.next()) {
            list.add(mapResultSetToStokLog(rs));
        }
        return list;
    }

    public List<StokLog> findAll() throws SQLException {
        List<StokLog> list = new ArrayList<>();
        String sql = "SELECT sl.*, o.nama_obat FROM tb_stok_log sl " +
                "JOIN tb_obat o ON sl.id_obat = o.id_obat ORDER BY sl.waktu DESC";
        ResultSet rs = dbManager.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSetToStokLog(rs));
        }
        return list;
    }

    private StokLog mapResultSetToStokLog(ResultSet rs) throws SQLException {
        StokLog sl = new StokLog();
        sl.setIdStokLog(rs.getInt("id_stok_log"));
        sl.setIdObat(rs.getInt("id_obat"));
        sl.setNamaObat(rs.getString("nama_obat"));
        sl.setJenis(rs.getString("jenis"));
        sl.setJumlah(rs.getInt("jumlah"));
        sl.setKeterangan(rs.getString("keterangan"));
        int idUser = rs.getInt("id_user");
        sl.setIdUser(rs.wasNull() ? null : idUser);
        Timestamp ts = rs.getTimestamp("waktu");
        if (ts != null) sl.setWaktu(ts.toLocalDateTime());
        return sl;
    }
}
