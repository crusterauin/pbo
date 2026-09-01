package com.sirekam.controller;

import com.sirekam.model.Kunjungan;
import com.sirekam.model.enums.StatusKunjungan;
import com.sirekam.dao.KunjunganDAO;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class KunjunganController extends GenericController<Kunjungan> {

    private KunjunganDAO kunjunganDAO;

    public KunjunganController() {
        super(new KunjunganDAO());
        this.kunjunganDAO = (KunjunganDAO) dao;
    }

    public int simpanDanGetId(Kunjungan kunjungan) throws SQLException {
        kunjungan.setStatus(StatusKunjungan.MENUNGGU);
        return kunjunganDAO.saveAndGetId(kunjungan);
    }

    public List<Kunjungan> getByDokter(int idDokter) throws SQLException {
        return kunjunganDAO.findByDokter(idDokter);
    }

    public List<Kunjungan> getByPasien(String idPasien) throws SQLException {
        return kunjunganDAO.findByPasien(idPasien);
    }

    public Kunjungan getById(int id) throws SQLException {
        return kunjunganDAO.findById(id);
    }

    public boolean updateStatus(int idKunjungan, StatusKunjungan status) throws SQLException {
        return kunjunganDAO.updateStatus(idKunjungan, status);
    }

    public List<Kunjungan> getKunjunganMenunggu() throws SQLException {
        return kunjunganDAO.getKunjunganMenunggu();
    }

    public LocalDateTime getTanggalKunjunganTerakhir(String idPasien) throws SQLException {
        return kunjunganDAO.getTanggalKunjunganTerakhir(idPasien);
    }
}