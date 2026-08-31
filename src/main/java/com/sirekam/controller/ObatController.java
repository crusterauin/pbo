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
}