package com.sirekam.model;

import com.sirekam.model.enums.JenisChat;
import com.sirekam.model.enums.StatusBaca;
import java.time.LocalDateTime;

public class ChatMessage {
    private int idChat;
    private JenisChat jenisChat;
    private Integer idKunjungan;
    private Integer idResep;
    private int idPengirim;
    private int idPenerima;
    private String isiPesan;
    private LocalDateTime waktuKirim;
    private StatusBaca statusBaca;
    private String namaPengirim;
    private String namaPenerima;

    public ChatMessage() {}

    public ChatMessage(JenisChat jenisChat, Integer idKunjungan, Integer idResep,
                       int idPengirim, int idPenerima, String isiPesan) {
        this.jenisChat = jenisChat;
        this.idKunjungan = idKunjungan;
        this.idResep = idResep;
        this.idPengirim = idPengirim;
        this.idPenerima = idPenerima;
        this.isiPesan = isiPesan;
        this.waktuKirim = LocalDateTime.now();
        this.statusBaca = StatusBaca.TERKIRIM;
    }

    public int getIdChat() { return idChat; }
    public void setIdChat(int idChat) { this.idChat = idChat; }

    public JenisChat getJenisChat() { return jenisChat; }
    public void setJenisChat(JenisChat jenisChat) { this.jenisChat = jenisChat; }

    public Integer getIdKunjungan() { return idKunjungan; }
    public void setIdKunjungan(Integer idKunjungan) { this.idKunjungan = idKunjungan; }

    public Integer getIdResep() { return idResep; }
    public void setIdResep(Integer idResep) { this.idResep = idResep; }

    public int getIdPengirim() { return idPengirim; }
    public void setIdPengirim(int idPengirim) { this.idPengirim = idPengirim; }

    public int getIdPenerima() { return idPenerima; }
    public void setIdPenerima(int idPenerima) { this.idPenerima = idPenerima; }

    public String getIsiPesan() { return isiPesan; }
    public void setIsiPesan(String isiPesan) { this.isiPesan = isiPesan; }

    public LocalDateTime getWaktuKirim() { return waktuKirim; }
    public void setWaktuKirim(LocalDateTime waktuKirim) { this.waktuKirim = waktuKirim; }

    public StatusBaca getStatusBaca() { return statusBaca; }
    public void setStatusBaca(StatusBaca statusBaca) { this.statusBaca = statusBaca; }

    public String getNamaPengirim() { return namaPengirim; }
    public void setNamaPengirim(String namaPengirim) { this.namaPengirim = namaPengirim; }

    public String getNamaPenerima() { return namaPenerima; }
    public void setNamaPenerima(String namaPenerima) { this.namaPenerima = namaPenerima; }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "idChat=" + idChat +
                ", jenisChat=" + jenisChat +
                ", idKunjungan=" + idKunjungan +
                ", idResep=" + idResep +
                ", idPengirim=" + idPengirim +
                ", idPenerima=" + idPenerima +
                ", isiPesan='" + isiPesan + '\'' +
                ", waktuKirim=" + waktuKirim +
                ", statusBaca=" + statusBaca +
                '}';
    }
}