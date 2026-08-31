package com.sirekam.controller;

import com.sirekam.model.Dokter;
import com.sirekam.dao.DokterDAO;
import java.sql.SQLException;
import java.util.List;

public class DokterController extends GenericController<Dokter> {

    private DokterDAO dokterDAO;

    public DokterController() {
        super(new DokterDAO());
        this.dokterDAO = (DokterDAO) dao;
    }

    public Dokter findById(int id) throws SQLException {
        return dokterDAO.findById(id);
    }

    public Dokter findByUserId(int idUser) throws SQLException {
        return dokterDAO.findByUserId(idUser);
    }

    public List<Dokter> getAllDokter() throws SQLException {
        return dokterDAO.findAll();
    }
}