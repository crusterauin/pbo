package com.sirekam.dao;

import com.sirekam.model.ChatMessage;
import com.sirekam.model.enums.JenisChat;
import com.sirekam.model.enums.StatusBaca;
import com.sirekam.util.DatabaseManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO implements GenericDAO<ChatMessage> {

    private DatabaseManager dbManager;

    public ChatDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    // ==================== CREATE ====================

    @Override
    public boolean save(ChatMessage chat) throws SQLException {
        String sql = "INSERT INTO tb_chat (jenis_chat, id_kunjungan, id_resep, " +
                "id_pengirim, id_penerima, isi_pesan, status_baca) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        int result = dbManager.executeUpdate(sql,
                chat.getJenisChat().getValue(),
                chat.getIdKunjungan(),
                chat.getIdResep(),
                chat.getIdPengirim(),
                chat.getIdPenerima(),
                chat.getIsiPesan(),
                chat.getStatusBaca().getValue()
        );
        return result > 0;
    }

    public int insert(ChatMessage chat) throws SQLException {
        String sql = "INSERT INTO tb_chat (jenis_chat, id_kunjungan, id_resep, " +
                "id_pengirim, id_penerima, isi_pesan, status_baca) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return dbManager.executeUpdateWithGeneratedKey(sql,
                chat.getJenisChat().getValue(),
                chat.getIdKunjungan(),
                chat.getIdResep(),
                chat.getIdPengirim(),
                chat.getIdPenerima(),
                chat.getIsiPesan(),
                chat.getStatusBaca().getValue()
        );
    }

    // ==================== READ ====================

    @Override
    public ChatMessage findById(int id) throws SQLException {
        String sql = "SELECT c.*, u1.nama_lengkap as nama_pengirim, u2.nama_lengkap as nama_penerima " +
                "FROM tb_chat c " +
                "JOIN tb_user u1 ON c.id_pengirim = u1.id_user " +
                "JOIN tb_user u2 ON c.id_penerima = u2.id_user " +
                "WHERE c.id_chat = ?";
        ResultSet rs = dbManager.executeQuery(sql, id);
        if (rs.next()) {
            return mapResultSetToChat(rs);
        }
        return null;
    }

    @Override
    public List<ChatMessage> findAll() throws SQLException {
        List<ChatMessage> list = new ArrayList<>();
        String sql = "SELECT c.*, u1.nama_lengkap as nama_pengirim, u2.nama_lengkap as nama_penerima " +
                "FROM tb_chat c " +
                "JOIN tb_user u1 ON c.id_pengirim = u1.id_user " +
                "JOIN tb_user u2 ON c.id_penerima = u2.id_user " +
                "ORDER BY c.waktu_kirim DESC";
        ResultSet rs = dbManager.executeQuery(sql);
        while (rs.next()) {
            list.add(mapResultSetToChat(rs));
        }
        return list;
    }

    // ==================== FIND BY SPECIFIC CRITERIA ====================

    public List<ChatMessage> findByKunjungan(int idKunjungan) throws SQLException {
        List<ChatMessage> list = new ArrayList<>();
        String sql = "SELECT c.*, u1.nama_lengkap as nama_pengirim, u2.nama_lengkap as nama_penerima " +
                "FROM tb_chat c " +
                "JOIN tb_user u1 ON c.id_pengirim = u1.id_user " +
                "JOIN tb_user u2 ON c.id_penerima = u2.id_user " +
                "WHERE c.id_kunjungan = ? AND c.jenis_chat = 'petugas_dokter' " +
                "ORDER BY c.waktu_kirim ASC";
        ResultSet rs = dbManager.executeQuery(sql, idKunjungan);
        while (rs.next()) {
            list.add(mapResultSetToChat(rs));
        }
        return list;
    }

    public List<ChatMessage> findByResep(int idResep) throws SQLException {
        List<ChatMessage> list = new ArrayList<>();
        String sql = "SELECT c.*, u1.nama_lengkap as nama_pengirim, u2.nama_lengkap as nama_penerima " +
                "FROM tb_chat c " +
                "JOIN tb_user u1 ON c.id_pengirim = u1.id_user " +
                "JOIN tb_user u2 ON c.id_penerima = u2.id_user " +
                "WHERE c.id_resep = ? AND c.jenis_chat = 'dokter_apoteker' " +
                "ORDER BY c.waktu_kirim ASC";
        ResultSet rs = dbManager.executeQuery(sql, idResep);
        while (rs.next()) {
            list.add(mapResultSetToChat(rs));
        }
        return list;
    }

    public List<ChatMessage> findByPengirim(int idPengirim) throws SQLException {
        List<ChatMessage> list = new ArrayList<>();
        String sql = "SELECT c.*, u1.nama_lengkap as nama_pengirim, u2.nama_lengkap as nama_penerima " +
                "FROM tb_chat c " +
                "JOIN tb_user u1 ON c.id_pengirim = u1.id_user " +
                "JOIN tb_user u2 ON c.id_penerima = u2.id_user " +
                "WHERE c.id_pengirim = ? " +
                "ORDER BY c.waktu_kirim DESC";
        ResultSet rs = dbManager.executeQuery(sql, idPengirim);
        while (rs.next()) {
            list.add(mapResultSetToChat(rs));
        }
        return list;
    }

    public List<ChatMessage> findByPenerima(int idPenerima) throws SQLException {
        List<ChatMessage> list = new ArrayList<>();
        String sql = "SELECT c.*, u1.nama_lengkap as nama_pengirim, u2.nama_lengkap as nama_penerima " +
                "FROM tb_chat c " +
                "JOIN tb_user u1 ON c.id_pengirim = u1.id_user " +
                "JOIN tb_user u2 ON c.id_penerima = u2.id_user " +
                "WHERE c.id_penerima = ? " +
                "ORDER BY c.waktu_kirim DESC";
        ResultSet rs = dbManager.executeQuery(sql, idPenerima);
        while (rs.next()) {
            list.add(mapResultSetToChat(rs));
        }
        return list;
    }

    public List<ChatMessage> findUnreadByPenerima(int idPenerima, JenisChat jenisChat) throws SQLException {
        List<ChatMessage> list = new ArrayList<>();
        String sql = "SELECT c.*, u1.nama_lengkap as nama_pengirim, u2.nama_lengkap as nama_penerima " +
                "FROM tb_chat c " +
                "JOIN tb_user u1 ON c.id_pengirim = u1.id_user " +
                "JOIN tb_user u2 ON c.id_penerima = u2.id_user " +
                "WHERE c.id_penerima = ? AND c.jenis_chat = ? AND c.status_baca = 'terkirim' " +
                "ORDER BY c.waktu_kirim ASC";
        ResultSet rs = dbManager.executeQuery(sql, idPenerima, jenisChat.getValue());
        while (rs.next()) {
            list.add(mapResultSetToChat(rs));
        }
        return list;
    }

    public List<ChatMessage> getRecentChats(int limit) throws SQLException {
        List<ChatMessage> list = new ArrayList<>();
        String sql = "SELECT c.*, u1.nama_lengkap as nama_pengirim, u2.nama_lengkap as nama_penerima " +
                "FROM tb_chat c " +
                "JOIN tb_user u1 ON c.id_pengirim = u1.id_user " +
                "JOIN tb_user u2 ON c.id_penerima = u2.id_user " +
                "ORDER BY c.waktu_kirim DESC LIMIT ?";
        ResultSet rs = dbManager.executeQuery(sql, limit);
        while (rs.next()) {
            list.add(mapResultSetToChat(rs));
        }
        return list;
    }

    // ==================== UPDATE ====================

    @Override
    public boolean update(ChatMessage chat) throws SQLException {
        String sql = "UPDATE tb_chat SET jenis_chat = ?, id_kunjungan = ?, id_resep = ?, " +
                "id_pengirim = ?, id_penerima = ?, isi_pesan = ?, status_baca = ? " +
                "WHERE id_chat = ?";
        int result = dbManager.executeUpdate(sql,
                chat.getJenisChat().getValue(),
                chat.getIdKunjungan(),
                chat.getIdResep(),
                chat.getIdPengirim(),
                chat.getIdPenerima(),
                chat.getIsiPesan(),
                chat.getStatusBaca().getValue(),
                chat.getIdChat()
        );
        return result > 0;
    }

    public boolean markAsRead(int idChat) throws SQLException {
        String sql = "UPDATE tb_chat SET status_baca = 'dibaca' WHERE id_chat = ?";
        int result = dbManager.executeUpdate(sql, idChat);
        return result > 0;
    }

    public boolean markAllAsRead(int idPenerima, JenisChat jenisChat) throws SQLException {
        String sql = "UPDATE tb_chat SET status_baca = 'dibaca' " +
                "WHERE id_penerima = ? AND jenis_chat = ? AND status_baca = 'terkirim'";
        int result = dbManager.executeUpdate(sql, idPenerima, jenisChat.getValue());
        return result > 0;
    }

    // ==================== DELETE ====================

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tb_chat WHERE id_chat = ?";
        int result = dbManager.executeUpdate(sql, id);
        return result > 0;
    }

    public boolean deleteByKunjungan(int idKunjungan) throws SQLException {
        String sql = "DELETE FROM tb_chat WHERE id_kunjungan = ? AND jenis_chat = 'petugas_dokter'";
        int result = dbManager.executeUpdate(sql, idKunjungan);
        return result > 0;
    }

    public boolean deleteByResep(int idResep) throws SQLException {
        String sql = "DELETE FROM tb_chat WHERE id_resep = ? AND jenis_chat = 'dokter_apoteker'";
        int result = dbManager.executeUpdate(sql, idResep);
        return result > 0;
    }

    public boolean deleteAllByUser(int idUser) throws SQLException {
        String sql = "DELETE FROM tb_chat WHERE id_pengirim = ? OR id_penerima = ?";
        int result = dbManager.executeUpdate(sql, idUser, idUser);
        return result > 0;
    }

    // ==================== SEARCH ====================

    @Override
    public List<ChatMessage> search(String keyword) throws SQLException {
        List<ChatMessage> list = new ArrayList<>();
        String sql = "SELECT c.*, u1.nama_lengkap as nama_pengirim, u2.nama_lengkap as nama_penerima " +
                "FROM tb_chat c " +
                "JOIN tb_user u1 ON c.id_pengirim = u1.id_user " +
                "JOIN tb_user u2 ON c.id_penerima = u2.id_user " +
                "WHERE c.isi_pesan LIKE ? " +
                "ORDER BY c.waktu_kirim DESC";
        ResultSet rs = dbManager.executeQuery(sql, "%" + keyword + "%");
        while (rs.next()) {
            list.add(mapResultSetToChat(rs));
        }
        return list;
    }

    // ==================== COUNT / STATISTICS ====================

    public int countUnreadByPenerima(int idPenerima) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM tb_chat " +
                "WHERE id_penerima = ? AND status_baca = 'terkirim'";
        ResultSet rs = dbManager.executeQuery(sql, idPenerima);
        if (rs.next()) {
            return rs.getInt("total");
        }
        return 0;
    }

    public int countByJenisChat(JenisChat jenisChat) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM tb_chat WHERE jenis_chat = ?";
        ResultSet rs = dbManager.executeQuery(sql, jenisChat.getValue());
        if (rs.next()) {
            return rs.getInt("total");
        }
        return 0;
    }

    public int countByPengirim(int idPengirim) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM tb_chat WHERE id_pengirim = ?";
        ResultSet rs = dbManager.executeQuery(sql, idPengirim);
        if (rs.next()) {
            return rs.getInt("total");
        }
        return 0;
    }

    public int countByPenerima(int idPenerima) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM tb_chat WHERE id_penerima = ?";
        ResultSet rs = dbManager.executeQuery(sql, idPenerima);
        if (rs.next()) {
            return rs.getInt("total");
        }
        return 0;
    }

    // ==================== MAPPER ====================

    private ChatMessage mapResultSetToChat(ResultSet rs) throws SQLException {
        ChatMessage chat = new ChatMessage();
        chat.setIdChat(rs.getInt("id_chat"));
        chat.setJenisChat(JenisChat.valueOf(rs.getString("jenis_chat").toUpperCase()));

        int kunjunganId = rs.getInt("id_kunjungan");
        chat.setIdKunjungan(rs.wasNull() ? null : kunjunganId);

        int resepId = rs.getInt("id_resep");
        chat.setIdResep(rs.wasNull() ? null : resepId);

        chat.setIdPengirim(rs.getInt("id_pengirim"));
        chat.setIdPenerima(rs.getInt("id_penerima"));
        chat.setIsiPesan(rs.getString("isi_pesan"));
        chat.setWaktuKirim(rs.getTimestamp("waktu_kirim").toLocalDateTime());
        chat.setStatusBaca(StatusBaca.valueOf(rs.getString("status_baca").toUpperCase()));
        chat.setNamaPengirim(rs.getString("nama_pengirim"));
        chat.setNamaPenerima(rs.getString("nama_penerima"));
        return chat;
    }
}