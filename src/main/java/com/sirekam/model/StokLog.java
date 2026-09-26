package com.sirekam.model;

import java.time.LocalDateTime;

public class StokLog {
    private int idStokLog;
    private int idObat;
    private String namaObat; // hasil join, untuk ditampilkan di tabel
    private String jenis; // "masuk" atau "keluar"
    private int jumlah;
    private String keterangan;
    private Integer idUser;
    private LocalDateTime waktu;

    public StokLog() {}

    public int getIdStokLog() { return idStokLog; }
    public void setIdStokLog(int idStokLog) { this.idStokLog = idStokLog; }
    public int getIdObat() { return idObat; }
    public void setIdObat(int idObat) { this.idObat = idObat; }
    public String getNamaObat() { return namaObat; }
    public void setNamaObat(String namaObat) { this.namaObat = namaObat; }
    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }
    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }
    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }
    public Integer getIdUser() { return idUser; }
    public void setIdUser(Integer idUser) { this.idUser = idUser; }
    public LocalDateTime getWaktu() { return waktu; }
    public void setWaktu(LocalDateTime waktu) { this.waktu = waktu; }
}
