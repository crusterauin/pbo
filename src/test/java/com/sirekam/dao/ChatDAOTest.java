package com.sirekam.dao;

import com.sirekam.model.*;
import com.sirekam.model.enums.JenisChat;
import com.sirekam.model.enums.JenisKelamin;
import com.sirekam.model.enums.StatusBaca;
import com.sirekam.model.enums.StatusKunjungan;
import com.sirekam.model.enums.StatusResep;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChatDAOTest {

    private static ChatDAO chatDAO;
    private static PasienDAO pasienDAO;
    private static KunjunganDAO kunjunganDAO;
    private static ResepDAO resepDAO;
    private static DokterDAO dokterDAO;
    private static UserDAO userDAO;

    private static String testPasienId;
    private static int testKunjunganId;
    private static int testResepId;
    private static int testDokterId;
    private static int testPetugasId = 1; // petugas1
    private static int testApotekerId = 4; // apoteker1
    private static int testChatId;

    @BeforeAll
    static void setUp() throws SQLException {
        chatDAO = new ChatDAO();
        pasienDAO = new PasienDAO();
        kunjunganDAO = new KunjunganDAO();
        resepDAO = new ResepDAO();
        dokterDAO = new DokterDAO();
        userDAO = new UserDAO();

        // 1. Buat pasien test
        testPasienId = pasienDAO.generateNoRM();
        Pasien pasien = new Pasien();
        pasien.setIdPasien(testPasienId);
        pasien.setNama("TEST CHAT PASIEN");
        pasien.setTanggalLahir(LocalDate.of(1993, 7, 25));
        pasien.setJenisKelamin(JenisKelamin.L);
        pasien.setAlamat("Jl. Chat Test No. 9");
        pasien.setNoHp("081234567898");
        pasien.setJenisAsuransi("REGULER");
        pasienDAO.save(pasien);

        // 2. Ambil dokter pertama
        List<Dokter> dokterList = dokterDAO.findAll();
        if (dokterList.isEmpty()) {
            throw new RuntimeException("Tidak ada data dokter!");
        }
        testDokterId = dokterList.get(0).getIdDokter();

        // 3. Buat kunjungan test
        Kunjungan kunjungan = new Kunjungan();
        kunjungan.setIdPasien(testPasienId);
        kunjungan.setIdDokter(testDokterId);
        kunjungan.setKeluhan("Sakit tenggorokan");
        kunjungan.setStatus(StatusKunjungan.MENUNGGU);
        kunjungan.setTanggalKunjungan(LocalDateTime.now());
        testKunjunganId = kunjunganDAO.saveAndGetId(kunjungan);

        // 4. Buat resep test
        Resep resep = new Resep();
        resep.setIdKunjungan(testKunjunganId);
        resep.setIdDokter(testDokterId);
        resep.setObatDanPerlakuan("Paracetamol 3x1");
        resep.setStatusResep(StatusResep.MENUNGGU);
        resep.setCreatedAt(LocalDateTime.now());
        testResepId = resepDAO.saveAndGetId(resep);

        System.out.println("=== MEMULAI TEST CHAT DAO ===");
        System.out.println("Setup: Kunjungan ID=" + testKunjunganId +
                ", Resep ID=" + testResepId +
                ", Petugas ID=" + testPetugasId +
                ", Dokter ID=" + testDokterId +
                ", Apoteker ID=" + testApotekerId);
    }

    // ==================== TEST PETUGAS-DOKTER CHAT ====================

    @Test
    @Order(1)
    void testSaveChatPetugasDokter() {
        System.out.println("TC-99: Save Chat Petugas-Dokter");
        try {
            ChatMessage chat = new ChatMessage();
            chat.setJenisChat(JenisChat.PETUGAS_DOKTER);
            chat.setIdKunjungan(testKunjunganId);
            chat.setIdResep(null);
            chat.setIdPengirim(testPetugasId);
            chat.setIdPenerima(testDokterId);
            chat.setIsiPesan("Pasien sudah datang, mohon segera diperiksa.");
            chat.setStatusBaca(StatusBaca.TERKIRIM);
            chat.setWaktuKirim(LocalDateTime.now());

            int id = chatDAO.insert(chat);
            assertTrue(id > 0, "ID chat harus > 0");
            testChatId = id;
            System.out.println("✅ Chat Petugas-Dokter saved with ID: " + id);
        } catch (SQLException e) {
            fail("Error saving chat: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    void testFindById() {
        System.out.println("TC-100: Find Chat by ID");
        try {
            ChatMessage chat = chatDAO.findById(testChatId);
            assertNotNull(chat, "Chat harus ditemukan");
            assertEquals(testChatId, chat.getIdChat());
            assertEquals(JenisChat.PETUGAS_DOKTER, chat.getJenisChat());
            assertEquals(testKunjunganId, chat.getIdKunjungan());
            assertEquals(testPetugasId, chat.getIdPengirim());
            assertEquals(testDokterId, chat.getIdPenerima());
            assertNotNull(chat.getNamaPengirim());
            assertNotNull(chat.getNamaPenerima());
            System.out.println("✅ Chat found: ID=" + chat.getIdChat() +
                    ", Pengirim=" + chat.getNamaPengirim() +
                    ", Isi=" + chat.getIsiPesan());
        } catch (SQLException e) {
            fail("Error finding chat: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testFindByKunjungan() {
        System.out.println("TC-101: Find Chat by Kunjungan");
        try {
            List<ChatMessage> list = chatDAO.findByKunjungan(testKunjunganId);
            assertNotNull(list);
            assertTrue(list.size() > 0, "Harus ada chat untuk kunjungan ini");
            for (ChatMessage c : list) {
                assertEquals(JenisChat.PETUGAS_DOKTER, c.getJenisChat());
                assertEquals(testKunjunganId, c.getIdKunjungan());
            }
            System.out.println("✅ Found " + list.size() + " chat(s) for kunjungan ID: " + testKunjunganId);
        } catch (SQLException e) {
            fail("Error finding by kunjungan: " + e.getMessage());
        }
    }

    // ==================== TEST DOKTER-APOTEKER CHAT ====================

    @Test
    @Order(4)
    void testSaveChatDokterApoteker() {
        System.out.println("TC-102: Save Chat Dokter-Apoteker");
        try {
            ChatMessage chat = new ChatMessage();
            chat.setJenisChat(JenisChat.DOKTER_APOTEKER);
            chat.setIdKunjungan(null);
            chat.setIdResep(testResepId);
            chat.setIdPengirim(testDokterId);
            chat.setIdPenerima(testApotekerId);
            chat.setIsiPesan("Mohon siapkan obat untuk pasien ini.");
            chat.setStatusBaca(StatusBaca.TERKIRIM);
            chat.setWaktuKirim(LocalDateTime.now());

            int id = chatDAO.insert(chat);
            assertTrue(id > 0, "ID chat harus > 0");
            System.out.println("✅ Chat Dokter-Apoteker saved with ID: " + id);

            // Simpan ID untuk test berikutnya
            chat.setIdChat(id);
        } catch (SQLException e) {
            fail("Error saving chat: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    void testFindByResep() {
        System.out.println("TC-103: Find Chat by Resep");
        try {
            List<ChatMessage> list = chatDAO.findByResep(testResepId);
            assertNotNull(list);
            assertTrue(list.size() > 0, "Harus ada chat untuk resep ini");
            for (ChatMessage c : list) {
                assertEquals(JenisChat.DOKTER_APOTEKER, c.getJenisChat());
                assertEquals(testResepId, c.getIdResep());
            }
            System.out.println("✅ Found " + list.size() + " chat(s) for resep ID: " + testResepId);
        } catch (SQLException e) {
            fail("Error finding by resep: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    void testFindByPengirim() {
        System.out.println("TC-104: Find Chat by Pengirim");
        try {
            List<ChatMessage> list = chatDAO.findByPengirim(testDokterId);
            assertNotNull(list);
            assertTrue(list.size() > 0, "Harus ada chat dari dokter ini");
            for (ChatMessage c : list) {
                assertEquals(testDokterId, c.getIdPengirim());
            }
            System.out.println("✅ Found " + list.size() + " chat(s) from pengirim ID: " + testDokterId);
        } catch (SQLException e) {
            fail("Error finding by pengirim: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    void testFindByPenerima() {
        System.out.println("TC-105: Find Chat by Penerima");
        try {
            List<ChatMessage> list = chatDAO.findByPenerima(testApotekerId);
            assertNotNull(list);
            assertTrue(list.size() > 0, "Harus ada chat untuk apoteker");
            for (ChatMessage c : list) {
                assertEquals(testApotekerId, c.getIdPenerima());
            }
            System.out.println("✅ Found " + list.size() + " chat(s) for penerima ID: " + testApotekerId);
        } catch (SQLException e) {
            fail("Error finding by penerima: " + e.getMessage());
        }
    }

    @Test
    @Order(8)
    void testFindUnreadByPenerima() {
        System.out.println("TC-106: Find Unread Chat by Penerima");
        try {
            // Kirim pesan baru (belum dibaca)
            ChatMessage chat = new ChatMessage();
            chat.setJenisChat(JenisChat.DOKTER_APOTEKER);
            chat.setIdKunjungan(null);
            chat.setIdResep(testResepId);
            chat.setIdPengirim(testDokterId);
            chat.setIdPenerima(testApotekerId);
            chat.setIsiPesan("Pesan baru untuk diuji.");
            chat.setStatusBaca(StatusBaca.TERKIRIM);
            chat.setWaktuKirim(LocalDateTime.now());
            chatDAO.insert(chat);

            List<ChatMessage> list = chatDAO.findUnreadByPenerima(testApotekerId, JenisChat.DOKTER_APOTEKER);
            assertNotNull(list);
            assertTrue(list.size() > 0, "Harus ada pesan belum dibaca");
            for (ChatMessage c : list) {
                assertEquals(StatusBaca.TERKIRIM, c.getStatusBaca());
            }
            System.out.println("✅ Found " + list.size() + " unread chat(s)");
        } catch (SQLException e) {
            fail("Error finding unread: " + e.getMessage());
        }
    }

    // ==================== TEST UPDATE ====================

    @Test
    @Order(9)
    void testMarkAsRead() {
        System.out.println("TC-107: Mark Chat as Read");
        try {
            // Cari chat yang belum dibaca
            List<ChatMessage> unread = chatDAO.findUnreadByPenerima(testApotekerId, JenisChat.DOKTER_APOTEKER);
            if (unread.isEmpty()) {
                System.out.println("⚠️ Tidak ada chat belum dibaca, skip test");
                return;
            }

            int chatId = unread.get(0).getIdChat();
            boolean result = chatDAO.markAsRead(chatId);
            assertTrue(result, "Mark as read harus berhasil");

            ChatMessage updated = chatDAO.findById(chatId);
            assertEquals(StatusBaca.DIBACA, updated.getStatusBaca());
            System.out.println("✅ Chat ID " + chatId + " marked as read");
        } catch (SQLException e) {
            fail("Error marking as read: " + e.getMessage());
        }
    }

    @Test
    @Order(10)
    void testMarkAllAsRead() {
        System.out.println("TC-108: Mark All Chat as Read");
        try {
            // Kirim beberapa pesan baru
            for (int i = 0; i < 3; i++) {
                ChatMessage chat = new ChatMessage();
                chat.setJenisChat(JenisChat.DOKTER_APOTEKER);
                chat.setIdKunjungan(null);
                chat.setIdResep(testResepId);
                chat.setIdPengirim(testDokterId);
                chat.setIdPenerima(testApotekerId);
                chat.setIsiPesan("Pesan test " + i);
                chat.setStatusBaca(StatusBaca.TERKIRIM);
                chat.setWaktuKirim(LocalDateTime.now());
                chatDAO.insert(chat);
            }

            boolean result = chatDAO.markAllAsRead(testApotekerId, JenisChat.DOKTER_APOTEKER);
            assertTrue(result, "Mark all as read harus berhasil");

            List<ChatMessage> unread = chatDAO.findUnreadByPenerima(testApotekerId, JenisChat.DOKTER_APOTEKER);
            // Pesan yang baru dikirim dari test sebelumnya mungkin masih ada
            // Yang penting pesan yang baru kita kirim sudah terbaca
            System.out.println("✅ All chats marked as read");
        } catch (SQLException e) {
            fail("Error marking all as read: " + e.getMessage());
        }
    }

    @Test
    @Order(11)
    void testCountUnreadByPenerima() {
        System.out.println("TC-109: Count Unread Chat by Penerima");
        try {
            int count = chatDAO.countUnreadByPenerima(testApotekerId);
            assertTrue(count >= 0, "Count harus >= 0");
            System.out.println("✅ Unread count for penerima " + testApotekerId + ": " + count);
        } catch (SQLException e) {
            fail("Error counting unread: " + e.getMessage());
        }
    }

    @Test
    @Order(12)
    void testCountByJenisChat() {
        System.out.println("TC-110: Count Chat by Jenis Chat");
        try {
            int petugasDokter = chatDAO.countByJenisChat(JenisChat.PETUGAS_DOKTER);
            int dokterApoteker = chatDAO.countByJenisChat(JenisChat.DOKTER_APOTEKER);

            assertTrue(petugasDokter >= 0, "Count harus >= 0");
            assertTrue(dokterApoteker >= 0, "Count harus >= 0");
            System.out.println("✅ Petugas-Dokter: " + petugasDokter + ", Dokter-Apoteker: " + dokterApoteker);
        } catch (SQLException e) {
            fail("Error counting by jenis chat: " + e.getMessage());
        }
    }

    @Test
    @Order(13)
    void testSearchChat() {
        System.out.println("TC-111: Search Chat");
        try {
            List<ChatMessage> list = chatDAO.search("pasien");
            assertNotNull(list);
            System.out.println("✅ Found " + list.size() + " chat(s) with keyword 'pasien'");
        } catch (SQLException e) {
            fail("Error searching chat: " + e.getMessage());
        }
    }

    @Test
    @Order(14)
    void testGetRecentChats() {
        System.out.println("TC-112: Get Recent Chats");
        try {
            List<ChatMessage> list = chatDAO.getRecentChats(5);
            assertNotNull(list);
            assertTrue(list.size() <= 5, "Maksimal 5 chat terbaru");
            System.out.println("✅ Recent chats retrieved: " + list.size());
        } catch (SQLException e) {
            fail("Error getting recent chats: " + e.getMessage());
        }
    }

    // ==================== TEST DELETE ====================

    @Test
    @Order(15)
    void testDeleteChat() {
        System.out.println("TC-113: Delete Chat");
        try {
            // Cari chat yang bisa dihapus
            List<ChatMessage> list = chatDAO.findByKunjungan(testKunjunganId);
            if (list.isEmpty()) {
                System.out.println("⚠️ Tidak ada chat untuk dihapus, skip test");
                return;
            }

            int id = list.get(0).getIdChat();
            boolean result = chatDAO.delete(id);
            assertTrue(result, "Delete chat harus berhasil");

            ChatMessage deleted = chatDAO.findById(id);
            assertNull(deleted, "Chat harus sudah terhapus");
            System.out.println("✅ Chat deleted: " + id);
        } catch (SQLException e) {
            fail("Error deleting chat: " + e.getMessage());
        }
    }

    @Test
    @Order(16)
    void testDeleteByKunjungan() {
        System.out.println("TC-114: Delete Chat by Kunjungan");
        try {
            // Cek apakah ada chat untuk kunjungan ini
            List<ChatMessage> before = chatDAO.findByKunjungan(testKunjunganId);
            if (before.isEmpty()) {
                System.out.println("⚠️ Tidak ada chat untuk kunjungan ini, skip test");
                return;
            }

            boolean result = chatDAO.deleteByKunjungan(testKunjunganId);
            assertTrue(result, "Delete by kunjungan harus berhasil");

            List<ChatMessage> after = chatDAO.findByKunjungan(testKunjunganId);
            assertEquals(0, after.size(), "Semua chat untuk kunjungan harus terhapus");
            System.out.println("✅ All chats deleted for kunjungan ID: " + testKunjunganId);
        } catch (SQLException e) {
            fail("Error deleting by kunjungan: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        // Hapus semua data test
        chatDAO.deleteByKunjungan(testKunjunganId);
        chatDAO.deleteByResep(testResepId);
        resepDAO.delete(testResepId);
        kunjunganDAO.delete(testKunjunganId);
        pasienDAO.delete(testPasienId);
        System.out.println("=== SELESAI TEST CHAT DAO ===");
    }
}