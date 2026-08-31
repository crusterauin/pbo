package com.sirekam.model;

public class Dokter {
    private int idDokter;
    private String namaDokter;
    private String spesialisasi;
    private int idUser;

    public Dokter() {}

    public Dokter(int idDokter, String namaDokter, String spesialisasi, int idUser) {
        this.idDokter = idDokter;
        this.namaDokter = namaDokter;
        this.spesialisasi = spesialisasi;
        this.idUser = idUser;
    }

    public int getIdDokter() { return idDokter; }
    public void setIdDokter(int idDokter) { this.idDokter = idDokter; }
    public String getNamaDokter() { return namaDokter; }
    public void setNamaDokter(String namaDokter) { this.namaDokter = namaDokter; }
    public String getSpesialisasi() { return spesialisasi; }
    public void setSpesialisasi(String spesialisasi) { this.spesialisasi = spesialisasi; }
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    @Override
    public String toString() {
        return namaDokter + " (" + spesialisasi + ")";
    }
}