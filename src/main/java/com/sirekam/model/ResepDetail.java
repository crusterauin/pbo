package com.sirekam.model;

import java.math.BigDecimal;

public class ResepDetail {
    private int idResepDetail;
    private int idResep;
    private int idObat;
    private int jumlah;
    private String namaObat;
    private String satuan;
    private BigDecimal hargaSatuan;

    public ResepDetail() {}

    public ResepDetail(int idResep, int idObat, int jumlah) {
        this.idResep = idResep;
        this.idObat = idObat;
        this.jumlah = jumlah;
    }

    public int getIdResepDetail() { return idResepDetail; }
    public void setIdResepDetail(int idResepDetail) { this.idResepDetail = idResepDetail; }
    public int getIdResep() { return idResep; }
    public void setIdResep(int idResep) { this.idResep = idResep; }
    public int getIdObat() { return idObat; }
    public void setIdObat(int idObat) { this.idObat = idObat; }
    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }
    public String getNamaObat() { return namaObat; }
    public void setNamaObat(String namaObat) { this.namaObat = namaObat; }
    public String getSatuan() { return satuan; }
    public void setSatuan(String satuan) { this.satuan = satuan; }
    public BigDecimal getHargaSatuan() { return hargaSatuan; }
    public void setHargaSatuan(BigDecimal hargaSatuan) { this.hargaSatuan = hargaSatuan; }

    public BigDecimal getSubTotal() {
        if (hargaSatuan != null) {
            return hargaSatuan.multiply(BigDecimal.valueOf(jumlah));
        }
        return BigDecimal.ZERO;
    }
}