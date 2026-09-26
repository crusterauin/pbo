package com.sirekam.dao;

import com.sirekam.model.Obat;
import com.sirekam.util.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ObatDAO implements GenericDAO<Obat> {

    private DatabaseManager dbManager;
    private final StokLogDAO stokLogDAO;

    public ObatDAO() {
        this.dbManager = DatabaseManager.getInstance();
        this.stokLogDAO = new StokLogDAO();
    }

    @Override
    public boolean save(Obat obat) throws SQLException {
        String sql = "INSERT INTO tb_obat (nama_obat, satuan, stok, harga_satuan) VALUES (?, ?, ?, ?)";
        int idObat = dbManager.executeUpdateWithGeneratedKey(sql,
                obat.getNamaObat(),
                obat.getSatuan(),
                obat.getStok(),
                obat.getHargaSatuan()
        );
        if (idObat > 0 && obat.getStok() > 0) {
            stokLogDAO.catat(idObat, "masuk", obat.getStok(), "Stok awal - obat baru", null);
        }
        return idObat > 0;
    }

    /** Dipakai Admin untuk menambah stok obat yang sudah ada (barang masuk). */
    public boolean tambahStok(int idObat, int jumlah, String keterangan, Integer idUser) throws SQLException {
        String sql = "UPDATE tb_obat SET stok = stok + ? WHERE id_obat = ?";
        int result = dbManager.executeUpdate(sql, jumlah, idObat);
        if (result > 0) {
            stokLogDAO.catat(idObat, "masuk", jumlah, keterangan, idUser);
        }
        return result > 0;
    }

    /** Dipakai Admin untuk mengurangi stok secara manual (mis. rusak/kadaluarsa). */
    public boolean kurangiStokManual(int idObat, int jumlah, String keterangan, Integer idUser) throws SQLException {
        String sql = "UPDATE tb_obat SET stok = stok - ? WHERE id_obat = ? AND stok >= ?";
        int result = dbManager.executeUpdate(sql, jumlah, idObat, jumlah);
        if (result > 0) {
            stokLogDAO.catat(idObat, "keluar", jumlah, keterangan, idUser);
        }
        return result > 0;
    }

    @Override
    public Obat findById(int id) throws SQLException {
        String sql = "SELECT * FROM tb_obat WHERE id_obat = ?";
        ResultSet rs = dbManager.executeQuery(sql, id);
        if (rs.next()) {
            return mapResultSetToObat(rs);
        }
        return null;
    }

    @Override
    public List<Obat> findAll() throws SQLException {
        List<Obat> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_obat ORDER BY nama_obat ASC";
        ResultSet rs = dbManager.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSetToObat(rs));
        }
        return list;
    }

    @Override
    public boolean update(Obat obat) throws SQLException {
        String sql = "UPDATE tb_obat SET nama_obat = ?, satuan = ?, stok = ?, harga_satuan = ? " +
                "WHERE id_obat = ?";
        int result = dbManager.executeUpdate(sql,
                obat.getNamaObat(),
                obat.getSatuan(),
                obat.getStok(),
                obat.getHargaSatuan(),
                obat.getIdObat()
        );
        return result > 0;
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tb_obat WHERE id_obat = ?";
        int result = dbManager.executeUpdate(sql, id);
        return result > 0;
    }

    @Override
    public List<Obat> search(String keyword) throws SQLException {
        List<Obat> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_obat WHERE nama_obat LIKE ? ORDER BY nama_obat ASC";
        ResultSet rs = dbManager.executeQuery(sql, "%" + keyword + "%");
        while (rs.next()) {
            list.add(mapResultSetToObat(rs));
        }
        return list;
    }

    public boolean updateStok(int idObat, int jumlah) throws SQLException {
        String sql = "UPDATE tb_obat SET stok = stok - ? WHERE id_obat = ? AND stok >= ?";
        int result = dbManager.executeUpdate(sql, jumlah, idObat, jumlah);
        if (result > 0) {
            stokLogDAO.catat(idObat, "keluar", jumlah, "Pemakaian resep (apoteker)", null);
        }
        return result > 0;
    }

    public boolean cekStok(int idObat, int jumlah) throws SQLException {
        String sql = "SELECT stok FROM tb_obat WHERE id_obat = ?";
        ResultSet rs = dbManager.executeQuery(sql, idObat);
        if (rs.next()) {
            return rs.getInt("stok") >= jumlah;
        }
        return false;
    }

    private Obat mapResultSetToObat(ResultSet rs) throws SQLException {
        Obat o = new Obat();
        o.setIdObat(rs.getInt("id_obat"));
        o.setNamaObat(rs.getString("nama_obat"));
        o.setSatuan(rs.getString("satuan"));
        o.setStok(rs.getInt("stok"));
        o.setHargaSatuan(rs.getBigDecimal("harga_satuan"));
        return o;
    }
}