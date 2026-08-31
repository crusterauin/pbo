package com.sirekam.dao;

import com.sirekam.model.Pasien;
import com.sirekam.model.enums.JenisKelamin;
import com.sirekam.util.DatabaseManager;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PasienDAO implements GenericDAO<Pasien> {

    private DatabaseManager dbManager;

    public PasienDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public boolean save(Pasien pasien) throws SQLException {
        String sql = "INSERT INTO tb_pasien (id_pasien, nama, tanggal_lahir, " +
                "jenis_kelamin, alamat, no_hp, jenis_asuransi) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int result = dbManager.executeUpdate(sql,
                pasien.getIdPasien(),
                pasien.getNama(),
                pasien.getTanggalLahir(),
                pasien.getJenisKelamin().name(),
                pasien.getAlamat(),
                pasien.getNoHp(),
                pasien.getJenisAsuransi()
        );
        return result > 0;
    }

    public Pasien findById(String idPasien) throws SQLException {
        String sql = "SELECT * FROM tb_pasien WHERE id_pasien = ?";
        ResultSet rs = dbManager.executeQuery(sql, idPasien);
        if (rs.next()) {
            return mapResultSetToPasien(rs);
        }
        return null;
    }

    @Override
    public Pasien findById(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Pasien> findAll() throws SQLException {
        List<Pasien> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_pasien ORDER BY nama ASC";
        ResultSet rs = dbManager.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSetToPasien(rs));
        }
        return list;
    }

    @Override
    public boolean update(Pasien pasien) throws SQLException {
        String sql = "UPDATE tb_pasien SET nama = ?, tanggal_lahir = ?, " +
                "jenis_kelamin = ?, alamat = ?, no_hp = ?, jenis_asuransi = ? " +
                "WHERE id_pasien = ?";
        int result = dbManager.executeUpdate(sql,
                pasien.getNama(),
                pasien.getTanggalLahir(),
                pasien.getJenisKelamin().name(),
                pasien.getAlamat(),
                pasien.getNoHp(),
                pasien.getJenisAsuransi(),
                pasien.getIdPasien()
        );
        return result > 0;
    }

    @Override
    public boolean delete(int id) throws SQLException {
        return false;
    }

    public boolean delete(String idPasien) throws SQLException {
        String sql = "DELETE FROM tb_pasien WHERE id_pasien = ?";
        int result = dbManager.executeUpdate(sql, idPasien);
        return result > 0;
    }

    @Override
    public List<Pasien> search(String keyword) throws SQLException {
        List<Pasien> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_pasien WHERE nama LIKE ? OR id_pasien LIKE ? " +
                "ORDER BY nama ASC";
        ResultSet rs = dbManager.executeQuery(sql, "%" + keyword + "%", "%" + keyword + "%");
        while (rs.next()) {
            list.add(mapResultSetToPasien(rs));
        }
        return list;
    }

    public String generateNoRM() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(id_pasien, 4) AS UNSIGNED)) as max_rm FROM tb_pasien";
        ResultSet rs = dbManager.executeQuery(sql);
        int next = 1;
        if (rs.next()) {
            next = rs.getInt("max_rm") + 1;
        }
        return String.format("RM-%04d", next);
    }

    public List<Pasien> findByAsuransi(String jenisAsuransi) throws SQLException {
        List<Pasien> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_pasien WHERE jenis_asuransi = ? ORDER BY nama ASC";
        ResultSet rs = dbManager.executeQuery(sql, jenisAsuransi);
        while (rs.next()) {
            list.add(mapResultSetToPasien(rs));
        }
        return list;
    }

    private Pasien mapResultSetToPasien(ResultSet rs) throws SQLException {
        Pasien p = new Pasien();
        p.setIdPasien(rs.getString("id_pasien"));
        p.setNama(rs.getString("nama"));
        Date date = rs.getDate("tanggal_lahir");
        if (date != null) {
            p.setTanggalLahir(date.toLocalDate());
        }
        p.setJenisKelamin(JenisKelamin.valueOf(rs.getString("jenis_kelamin")));
        p.setAlamat(rs.getString("alamat"));
        p.setNoHp(rs.getString("no_hp"));
        p.setJenisAsuransi(rs.getString("jenis_asuransi"));
        return p;
    }
}