package com.sirekam.controller;

import com.sirekam.model.Obat;
import com.sirekam.model.Resep;
import com.sirekam.model.ResepDetail;
import com.sirekam.model.enums.StatusResep;
import com.sirekam.dao.ResepDAO;
import com.sirekam.dao.ResepDetailDAO;
import com.sirekam.dao.ObatDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResepController extends GenericController<Resep> {

    private ResepDAO resepDAO;
    private ResepDetailDAO resepDetailDAO;
    private ObatDAO obatDAO;

    public ResepController() {
        super(new ResepDAO());
        this.resepDAO = (ResepDAO) dao;
        this.resepDetailDAO = new ResepDetailDAO();
        this.obatDAO = new ObatDAO();
    }

    public int buatResep(Resep resep) throws SQLException {
        resep.setStatusResep(StatusResep.MENUNGGU);
        return resepDAO.saveAndGetId(resep);
    }

    public boolean tambahDetailResep(ResepDetail detail) throws SQLException {
        return resepDetailDAO.save(detail);
    }

    public List<Resep> getResepMenunggu() throws SQLException {
        return resepDAO.getResepMenunggu();
    }

    public List<Resep> getByDokter(int idDokter) throws SQLException {
        return resepDAO.findByDokter(idDokter);
    }

    public Resep getById(int idResep) throws SQLException {
        return resepDAO.findById(idResep);
    }

    public List<ResepDetail> getDetailResep(int idResep) throws SQLException {
        return resepDetailDAO.findByResep(idResep);
    }

    public boolean updateStatus(int idResep, StatusResep status) throws SQLException {
        return resepDAO.updateStatus(idResep, status);
    }

    public boolean assignObat(int idResep, int idObat, int jumlah) throws SQLException {
        System.out.println("🔍 [DEBUG] assignObat: resep=" + idResep + ", obat=" + idObat + ", jumlah=" + jumlah);

        // Cek stok
        boolean stokCukup = obatDAO.cekStok(idObat, jumlah);
        System.out.println("🔍 [DEBUG] Stok cukup: " + stokCukup);

        if (!stokCukup) {
            // Log stok aktual untuk debugging
            Obat obat = obatDAO.findById(idObat);
            if (obat != null) {
                System.out.println("🔍 [DEBUG] Stok aktual: " + obat.getStok() + ", dibutuhkan: " + jumlah);
            }
            return false;
        }

        // Update stok
        boolean stokUpdated = obatDAO.updateStok(idObat, jumlah);
        System.out.println("🔍 [DEBUG] Stok updated: " + stokUpdated);

        if (stokUpdated) {
            ResepDetail detail = new ResepDetail(idResep, idObat, jumlah);
            boolean detailSaved = resepDetailDAO.save(detail);
            System.out.println("🔍 [DEBUG] Detail saved: " + detailSaved);
            return detailSaved;
        }
        return false;
    }

    public List<Resep> getResepAktif() throws SQLException {
        // Mengambil resep dengan status MENUNGGU atau DIPROSES_APOTEKER
        List<Resep> list = new ArrayList<>();
        list.addAll(resepDAO.getResepMenunggu());
        list.addAll(resepDAO.getResepDiproses());
        return list;
    }
}