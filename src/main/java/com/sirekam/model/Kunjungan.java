package com.sirekam.model;

import com.sirekam.model.enums.StatusKunjungan;
import java.time.LocalDateTime;

public class Kunjungan {
    private int idKunjungan;
    private String idPasien;
    private int idDokter;
    private String keluhan;
    private LocalDateTime tanggalKunjungan;
    private StatusKunjungan status;
    private String namaPasien;
    private String namaDokter;

    public Kunjungan() {}

    public Kunjungan(String idPasien, int idDokter, String keluhan, StatusKunjungan status) {
        this.idPasien = idPasien;
        this.idDokter = idDokter;
        this.keluhan = keluhan;
        this.status = status;
        this.tanggalKunjungan = LocalDateTime.now();
    }

    public int getIdKunjungan() { return idKunjungan; }
    public void setIdKunjungan(int idKunjungan) { this.idKunjungan = idKunjungan; }
    public String getIdPasien() { return idPasien; }
    public void setIdPasien(String idPasien) { this.idPasien = idPasien; }
    public int getIdDokter() { return idDokter; }
    public void setIdDokter(int idDokter) { this.idDokter = idDokter; }
    public String getKeluhan() { return keluhan; }
    public void setKeluhan(String keluhan) { this.keluhan = keluhan; }
    public LocalDateTime getTanggalKunjungan() { return tanggalKunjungan; }
    public void setTanggalKunjungan(LocalDateTime tanggalKunjungan) { this.tanggalKunjungan = tanggalKunjungan; }
    public StatusKunjungan getStatus() { return status; }
    public void setStatus(StatusKunjungan status) { this.status = status; }
    public String getNamaPasien() { return namaPasien; }
    public void setNamaPasien(String namaPasien) { this.namaPasien = namaPasien; }
    public String getNamaDokter() { return namaDokter; }
    public void setNamaDokter(String namaDokter) { this.namaDokter = namaDokter; }
}