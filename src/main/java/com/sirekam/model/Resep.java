package com.sirekam.model;

import com.sirekam.model.enums.StatusResep;
import java.time.LocalDateTime;

public class Resep {
    private int idResep;
    private int idKunjungan;
    private int idDokter;
    private String obatDanPerlakuan;
    private StatusResep statusResep;
    private LocalDateTime createdAt;
    private String namaPasien;
    private String namaDokter;

    public Resep() {}

    public Resep(int idKunjungan, int idDokter, String obatDanPerlakuan, StatusResep statusResep) {
        this.idKunjungan = idKunjungan;
        this.idDokter = idDokter;
        this.obatDanPerlakuan = obatDanPerlakuan;
        this.statusResep = statusResep;
        this.createdAt = LocalDateTime.now();
    }

    public int getIdResep() { return idResep; }
    public void setIdResep(int idResep) { this.idResep = idResep; }
    public int getIdKunjungan() { return idKunjungan; }
    public void setIdKunjungan(int idKunjungan) { this.idKunjungan = idKunjungan; }
    public int getIdDokter() { return idDokter; }
    public void setIdDokter(int idDokter) { this.idDokter = idDokter; }
    public String getObatDanPerlakuan() { return obatDanPerlakuan; }
    public void setObatDanPerlakuan(String obatDanPerlakuan) { this.obatDanPerlakuan = obatDanPerlakuan; }
    public StatusResep getStatusResep() { return statusResep; }
    public void setStatusResep(StatusResep statusResep) { this.statusResep = statusResep; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getNamaPasien() { return namaPasien; }
    public void setNamaPasien(String namaPasien) { this.namaPasien = namaPasien; }
    public String getNamaDokter() { return namaDokter; }
    public void setNamaDokter(String namaDokter) { this.namaDokter = namaDokter; }
}