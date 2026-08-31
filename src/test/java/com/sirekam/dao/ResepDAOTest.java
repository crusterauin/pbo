package com.sirekam.dao;

import com.sirekam.model.*;
import com.sirekam.model.enums.JenisKelamin;
import com.sirekam.model.enums.StatusKunjungan;
import com.sirekam.model.enums.StatusResep;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ResepDAOTest {

    private static ResepDAO resepDAO;
    private static PasienDAO pasienDAO;
    private static KunjunganDAO kunjunganDAO;
    private static DokterDAO dokterDAO;
    private static ResepDetailDAO resepDetailDAO;
    private static String testPasienId;
    private static int testKunjunganId;
    private static int testResepId;
    private static int testDokterId;


    @BeforeAll
    static void setUp() throws SQLException {
        resepDetailDAO = new ResepDetailDAO();
        resepDAO = new ResepDAO();
        pasienDAO = new PasienDAO();
        kunjunganDAO = new KunjunganDAO();
        dokterDAO = new DokterDAO();

        // 1. Buat pasien test
        testPasienId = pasienDAO.generateNoRM();
        Pasien pasien = new Pasien();
        pasien.setIdPasien(testPasienId);
        pasien.setNama("TEST RESEP PASIEN");
        pasien.setTanggalLahir(LocalDate.of(1995, 6, 15));
        pasien.setJenisKelamin(JenisKelamin.L);
        pasien.setAlamat("Jl. Resep Test No. 1");
        pasien.setNoHp("081234567895");
        pasien.setJenisAsuransi("REGULER");
        pasienDAO.save(pasien);

        // 2. Ambil dokter pertama
        List<Dokter> dokterList = dokterDAO.findAll();
        if (dokterList.isEmpty()) {
            throw new RuntimeException("Tidak ada data dokter! Pastikan seed_data.sql sudah diimport.");
        }
        testDokterId = dokterList.get(0).getIdDokter();

        // 3. Buat kunjungan test
        Kunjungan kunjungan = new Kunjungan();
        kunjungan.setIdPasien(testPasienId);
        kunjungan.setIdDokter(testDokterId);
        kunjungan.setKeluhan("Demam dan batuk selama 5 hari");
        kunjungan.setStatus(StatusKunjungan.MENUNGGU);
        kunjungan.setTanggalKunjungan(LocalDateTime.now());
        testKunjunganId = kunjunganDAO.saveAndGetId(kunjungan);

        System.out.println("=== MEMULAI TEST RESEP DAO ===");
        System.out.println("Setup: Pasien ID=" + testPasienId + ", Kunjungan ID=" + testKunjunganId);
    }

    @Test
    @Order(1)
    void testSaveResep() {
        System.out.println("TC-68: Save Resep");
        try {
            Resep resep = new Resep();
            resep.setIdKunjungan(testKunjunganId);
            resep.setIdDokter(testDokterId);
            resep.setObatDanPerlakuan("Paracetamol 3x1 tablet, Amoxicillin 3x1 kapsul, Istirahat total 3 hari");
            resep.setStatusResep(StatusResep.MENUNGGU);
            resep.setCreatedAt(LocalDateTime.now());

            boolean result = resepDAO.save(resep);
            assertTrue(result, "Save resep harus berhasil");

            // Ambil ID dari database
            List<Resep> list = resepDAO.findByDokter(testDokterId);
            assertFalse(list.isEmpty(), "Harus ada resep untuk dokter ini");
            testResepId = resepDAO.saveAndGetId(resep);  // ← LANGSUNG SIMPAN ID
            System.out.println("✅ Resep saved with ID: " + testResepId);
        } catch (SQLException e) {
            fail("Error saving resep: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    void testSaveAndGetId() {
        System.out.println("TC-69: Save Resep and Get ID");
        try {
            // Buat resep baru untuk test
            Resep resep = new Resep();
            resep.setIdKunjungan(testKunjunganId);
            resep.setIdDokter(testDokterId);
            resep.setObatDanPerlakuan("Vitamin C 2x1 tablet");
            resep.setStatusResep(StatusResep.MENUNGGU);
            resep.setCreatedAt(LocalDateTime.now());

            int id = resepDAO.saveAndGetId(resep);
            assertTrue(id > 0, "ID resep harus > 0");

            // Verifikasi
            Resep saved = resepDAO.findById(id);
            assertNotNull(saved);
            assertEquals("Vitamin C 2x1 tablet", saved.getObatDanPerlakuan());
            System.out.println("✅ Resep saved with ID from generated key: " + id);

            // Hapus resep test tambahan
            resepDAO.delete(id);
        } catch (SQLException e) {
            fail("Error saving resep: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testFindById() {
        System.out.println("TC-70: Find Resep by ID");
        try {
            Resep resep = resepDAO.findById(testResepId);
            assertNotNull(resep, "Resep harus ditemukan");
            assertEquals(testResepId, resep.getIdResep());
            assertEquals(testKunjunganId, resep.getIdKunjungan());
            assertNotNull(resep.getNamaPasien(), "Nama pasien harus ada (JOIN)");
            assertNotNull(resep.getNamaDokter(), "Nama dokter harus ada (JOIN)");
            System.out.println("✅ Resep found: ID=" + resep.getIdResep() +
                    ", Pasien=" + resep.getNamaPasien() +
                    ", Dokter=" + resep.getNamaDokter());
        } catch (SQLException e) {
            fail("Error finding resep: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testFindAll() {
        System.out.println("TC-71: Find All Resep");
        try {
            List<Resep> list = resepDAO.findAll();
            assertNotNull(list);
            assertTrue(list.size() > 0, "Harus ada minimal 1 resep");
            System.out.println("✅ Total resep: " + list.size());
        } catch (SQLException e) {
            fail("Error finding all resep: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    void testUpdateResep() {
        System.out.println("TC-72: Update Resep");
        try {
            Resep resep = resepDAO.findById(testResepId);
            assertNotNull(resep);

            String updatedObat = "Paracetamol 3x1 tablet, Amoxicillin 3x1 kapsul, Istirahat total 5 hari, Perbanyak minum air putih";
            resep.setObatDanPerlakuan(updatedObat);
            resep.setStatusResep(StatusResep.DIPROSES_APOTEKER);

            boolean result = resepDAO.update(resep);
            assertTrue(result, "Update resep harus berhasil");

            Resep updated = resepDAO.findById(testResepId);
            assertEquals(updatedObat, updated.getObatDanPerlakuan());
            assertEquals(StatusResep.DIPROSES_APOTEKER, updated.getStatusResep());
            System.out.println("✅ Resep updated: status=" + updated.getStatusResep().getDisplayName());
        } catch (SQLException e) {
            fail("Error updating resep: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    void testSearchResep() {
        System.out.println("TC-73: Search Resep");
        try {
            List<Resep> list = resepDAO.search("Paracetamol");
            assertNotNull(list);
            assertTrue(list.size() > 0, "Harus ada resep dengan keyword 'Paracetamol'");
            System.out.println("✅ Found " + list.size() + " resep with keyword 'Paracetamol'");
        } catch (SQLException e) {
            fail("Error searching resep: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    void testGetResepMenunggu() {
        System.out.println("TC-74: Get Resep Menunggu");
        try {
            List<Resep> list = resepDAO.getResepMenunggu();
            assertNotNull(list);
            for (Resep r : list) {
                assertEquals(StatusResep.MENUNGGU, r.getStatusResep());
            }
            System.out.println("✅ Found " + list.size() + " resep menunggu");
        } catch (SQLException e) {
            fail("Error getting resep menunggu: " + e.getMessage());
        }
    }

    @Test
    @Order(8)
    void testFindByDokter() {
        System.out.println("TC-75: Find Resep by Dokter");
        try {
            List<Resep> list = resepDAO.findByDokter(testDokterId);
            assertNotNull(list);
            assertTrue(list.size() > 0, "Harus ada resep untuk dokter ini");
            for (Resep r : list) {
                assertEquals(testDokterId, r.getIdDokter());
            }
            System.out.println("✅ Found " + list.size() + " resep for dokter ID: " + testDokterId);
        } catch (SQLException e) {
            fail("Error finding by dokter: " + e.getMessage());
        }
    }

    @Test
    @Order(9)
    void testUpdateStatus() {
        System.out.println("TC-76: Update Status Resep");
        try {
            // Ubah ke SELESAI
            boolean result = resepDAO.updateStatus(testResepId, StatusResep.SELESAI);
            assertTrue(result, "Update status harus berhasil");

            Resep updated = resepDAO.findById(testResepId);
            assertEquals(StatusResep.SELESAI, updated.getStatusResep());
            System.out.println("✅ Status updated to: " + updated.getStatusResep().getDisplayName());
        } catch (SQLException e) {
            fail("Error updating status: " + e.getMessage());
        }
    }

    @Test
    @Order(10)
    void testDeleteResep() {
        System.out.println("TC-77: Delete Resep");
        try {
            boolean result = resepDAO.delete(testResepId);
            assertTrue(result, "Delete resep harus berhasil");

            Resep deleted = resepDAO.findById(testResepId);
            assertNull(deleted, "Resep harus sudah terhapus");
            System.out.println("✅ Resep deleted: " + testResepId);
        } catch (SQLException e) {
            fail("Error deleting resep: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        // HAPUS DALAM URUTAN TERBALIK

        // 1. Hapus resep detail (child dari resep)
        if (testResepId > 0) {
            try {
                resepDetailDAO.deleteByResep(testResepId);
            } catch (SQLException e) {
                System.err.println("⚠️ Gagal hapus resep detail: " + e.getMessage());
            }
        }

        // 2. Hapus resep (child dari kunjungan)
        if (testResepId > 0) {
            try {
                resepDAO.delete(testResepId);
            } catch (SQLException e) {
                System.err.println("⚠️ Gagal hapus resep: " + e.getMessage());
            }
        }

        // 3. Hapus kunjungan (child dari pasien)
        if (testKunjunganId > 0) {
            try {
                kunjunganDAO.delete(testKunjunganId);
            } catch (SQLException e) {
                System.err.println("⚠️ Gagal hapus kunjungan: " + e.getMessage());
            }
        }

        // 4. Hapus pasien
        if (testPasienId != null && !testPasienId.isEmpty()) {
            try {
                pasienDAO.delete(testPasienId);
            } catch (SQLException e) {
                System.err.println("⚠️ Gagal hapus pasien: " + e.getMessage());
            }
        }
    }
}