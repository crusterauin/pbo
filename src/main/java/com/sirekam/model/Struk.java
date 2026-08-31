package com.sirekam.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Struk {
    private int idStruk;
    private int idResep;
    private BigDecimal biayaKonsultasi;
    private BigDecimal biayaObat;
    private BigDecimal totalBayar;
    private LocalDateTime waktuCetak;
    private String namaPasien;
    private String namaDokter;
    private String noRekamMedis;
    private String jenisAsuransi;

    public Struk() {}

    public Struk(int idResep, BigDecimal biayaKonsultasi, BigDecimal biayaObat) {
        this.idResep = idResep;
        this.biayaKonsultasi = biayaKonsultasi;
        this.biayaObat = biayaObat;
        this.totalBayar = biayaKonsultasi.add(biayaObat);
        this.waktuCetak = LocalDateTime.now();
    }

    public int getIdStruk() { return idStruk; }
    public void setIdStruk(int idStruk) { this.idStruk = idStruk; }
    public int getIdResep() { return idResep; }
    public void setIdResep(int idResep) { this.idResep = idResep; }
    public BigDecimal getBiayaKonsultasi() { return biayaKonsultasi; }
    public void setBiayaKonsultasi(BigDecimal biayaKonsultasi) { this.biayaKonsultasi = biayaKonsultasi; }
    public BigDecimal getBiayaObat() { return biayaObat; }
    public void setBiayaObat(BigDecimal biayaObat) { this.biayaObat = biayaObat; }
    public BigDecimal getTotalBayar() { return totalBayar; }
    public void setTotalBayar(BigDecimal totalBayar) { this.totalBayar = totalBayar; }
    public LocalDateTime getWaktuCetak() { return waktuCetak; }
    public void setWaktuCetak(LocalDateTime waktuCetak) { this.waktuCetak = waktuCetak; }
    public String getNamaPasien() { return namaPasien; }
    public void setNamaPasien(String namaPasien) { this.namaPasien = namaPasien; }
    public String getNamaDokter() { return namaDokter; }
    public void setNamaDokter(String namaDokter) { this.namaDokter = namaDokter; }
    public String getNoRekamMedis() { return noRekamMedis; }
    public void setNoRekamMedis(String noRekamMedis) { this.noRekamMedis = noRekamMedis; }
    public String getJenisAsuransi() { return jenisAsuransi; }
    public void setJenisAsuransi(String jenisAsuransi) { this.jenisAsuransi = jenisAsuransi; }
}