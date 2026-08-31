package com.sirekam.controller;

import com.sirekam.model.ChatMessage;
import com.sirekam.model.enums.JenisChat;
import com.sirekam.model.enums.StatusBaca;
import com.sirekam.dao.ChatDAO;
import java.sql.SQLException;
import java.util.List;

public class ChatController extends GenericController<ChatMessage> {

    private ChatDAO chatDAO;

    public ChatController() {
        super(new ChatDAO());
        this.chatDAO = (ChatDAO) dao;
    }

    public boolean kirimPesan(ChatMessage message) throws SQLException {
        message.setStatusBaca(StatusBaca.TERKIRIM);
        return chatDAO.save(message);
    }

    public int kirimPesanDanGetId(ChatMessage message) throws SQLException {
        message.setStatusBaca(StatusBaca.TERKIRIM);
        return chatDAO.insert(message);
    }

    public List<ChatMessage> getPesanPetugasDokter(int idKunjungan) throws SQLException {
        return chatDAO.findByKunjungan(idKunjungan);
    }

    public List<ChatMessage> getPesanDokterApoteker(int idResep) throws SQLException {
        return chatDAO.findByResep(idResep);
    }

    public List<ChatMessage> getPesanBaru(int idPenerima, JenisChat jenisChat) throws SQLException {
        return chatDAO.findUnreadByPenerima(idPenerima, jenisChat);
    }

    public boolean tandaiDibaca(int idChat) throws SQLException {
        return chatDAO.markAsRead(idChat);
    }

    public boolean tandaiSemuaDibaca(int idPenerima, JenisChat jenisChat) throws SQLException {
        return chatDAO.markAllAsRead(idPenerima, jenisChat);
    }

    public int getJumlahPesanBaru(int idPenerima) throws SQLException {
        return chatDAO.countUnreadByPenerima(idPenerima);
    }

    public List<ChatMessage> getRecentChats(int limit) throws SQLException {
        return chatDAO.getRecentChats(limit);
    }

    public List<ChatMessage> getPesanByPengirim(int idPengirim) throws SQLException {
        return chatDAO.findByPengirim(idPengirim);
    }

    public List<ChatMessage> getPesanByPenerima(int idPenerima) throws SQLException {
        return chatDAO.findByPenerima(idPenerima);
    }

    public List<ChatMessage> getPesanByResep(int idResep) throws SQLException {
        return chatDAO.findByResep(idResep);
    }
}