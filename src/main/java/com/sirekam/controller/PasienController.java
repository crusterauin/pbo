package com.sirekam.controller;

import com.sirekam.model.Pasien;
import com.sirekam.dao.PasienDAO;
import java.sql.SQLException;
import java.util.List;

public class PasienController extends GenericController<Pasien> {

    private PasienDAO pasienDAO;

    public PasienController() {
        super(new PasienDAO());
        this.pasienDAO = (PasienDAO) dao;
    }

    public String generateNoRM() throws SQLException {
        return pasienDAO.generateNoRM();
    }

    public Pasien cariById(String id) throws SQLException {
        return pasienDAO.findById(id);
    }

    public List<Pasien> cariByAsuransi(String jenisAsuransi) throws SQLException {
        return pasienDAO.findByAsuransi(jenisAsuransi);
    }

    public boolean hapus(String id) throws SQLException {
        return pasienDAO.delete(id);
    }
}