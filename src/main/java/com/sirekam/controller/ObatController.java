package com.sirekam.controller;

import com.sirekam.model.Obat;
import com.sirekam.dao.ObatDAO;
import java.sql.SQLException;
import java.util.List;

public class ObatController extends GenericController<Obat> {

    private ObatDAO obatDAO;

    public ObatController() {
        super(new ObatDAO());
        this.obatDAO = (ObatDAO) dao;
    }

    public Obat findById(int id) throws SQLException {
        return obatDAO.findById(id);
    }

    public boolean updateStok(int idObat, int jumlah) throws SQLException {
        return obatDAO.updateStok(idObat, jumlah);
    }

    public boolean cekStok(int idObat, int jumlah) throws SQLException {
        return obatDAO.cekStok(idObat, jumlah);
    }

    public boolean tambahStok(int idObat, int jumlah, String keterangan, Integer idUser) throws SQLException {
        return obatDAO.tambahStok(idObat, jumlah, keterangan, idUser);
    }

    public boolean kurangiStokManual(int idObat, int jumlah, String keterangan, Integer idUser) throws SQLException {
        return obatDAO.kurangiStokManual(idObat, jumlah, keterangan, idUser);
    }
}