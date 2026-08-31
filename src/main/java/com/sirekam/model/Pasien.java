package com.sirekam.model;

import com.sirekam.model.enums.JenisKelamin;
import java.time.LocalDate;

public class Pasien {
    private String idPasien;
    private String nama;
    private LocalDate tanggalLahir;
    private JenisKelamin jenisKelamin;
    private String alamat;
    private String noHp;
    private String jenisAsuransi;

    public Pasien() {}

    public Pasien(String idPasien, String nama, LocalDate tanggalLahir,
                  JenisKelamin jenisKelamin, String alamat, String noHp, String jenisAsuransi) {
        this.idPasien = idPasien;
        this.nama = nama;
        this.tanggalLahir = tanggalLahir;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
        this.noHp = noHp;
        this.jenisAsuransi = jenisAsuransi != null ? jenisAsuransi : "REGULER";
    }

    public String getIdPasien() { return idPasien; }
    public void setIdPasien(String idPasien) { this.idPasien = idPasien; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public LocalDate getTanggalLahir() { return tanggalLahir; }
    public void setTanggalLahir(LocalDate tanggalLahir) { this.tanggalLahir = tanggalLahir; }
    public JenisKelamin getJenisKelamin() { return jenisKelamin; }
    public void setJenisKelamin(JenisKelamin jenisKelamin) { this.jenisKelamin = jenisKelamin; }
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public String getNoHp() { return noHp; }
    public void setNoHp(String noHp) { this.noHp = noHp; }
    public String getJenisAsuransi() { return jenisAsuransi; }
    public void setJenisAsuransi(String jenisAsuransi) { this.jenisAsuransi = jenisAsuransi; }

    public boolean isBPJS() { return "BPJS".equals(jenisAsuransi); }
    public boolean isAsuransi() { return "ASURANSI".equals(jenisAsuransi); }

    @Override
    public String toString() {
        return idPasien + " - " + nama + " (" + jenisAsuransi + ")";
    }
}