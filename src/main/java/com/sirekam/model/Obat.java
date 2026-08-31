package com.sirekam.model;

import java.math.BigDecimal;

public class Obat {
    private int idObat;
    private String namaObat;
    private String satuan;
    private int stok;
    private BigDecimal hargaSatuan;

    public Obat() {}

    public Obat(int idObat, String namaObat, String satuan, int stok, BigDecimal hargaSatuan) {
        this.idObat = idObat;
        this.namaObat = namaObat;
        this.satuan = satuan;
        this.stok = stok;
        this.hargaSatuan = hargaSatuan;
    }

    public int getIdObat() { return idObat; }
    public void setIdObat(int idObat) { this.idObat = idObat; }
    public String getNamaObat() { return namaObat; }
    public void setNamaObat(String namaObat) { this.namaObat = namaObat; }
    public String getSatuan() { return satuan; }
    public void setSatuan(String satuan) { this.satuan = satuan; }
    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }
    public BigDecimal getHargaSatuan() { return hargaSatuan; }
    public void setHargaSatuan(BigDecimal hargaSatuan) { this.hargaSatuan = hargaSatuan; }

    @Override
    public String toString() {
        return namaObat + " (Stok: " + stok + " " + satuan + ", Rp" + hargaSatuan + ")";
    }
}