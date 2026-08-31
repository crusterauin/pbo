package com.sirekam.dao;

import com.sirekam.model.Kunjungan;
import com.sirekam.model.Pasien;
import com.sirekam.model.Dokter;
import com.sirekam.model.enums.JenisKelamin;
import com.sirekam.model.enums.StatusKunjungan;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KunjunganDAOTest {

    private static KunjunganDAO kunjunganDAO;
    private static PasienDAO pasienDAO;
    private static DokterDAO dokterDAO;
    private static int testKunjunganId;
    private static String testPasienId;

    @BeforeAll
    static void setUp() throws SQLException {
        kunjunganDAO = new KunjunganDAO();
        pasienDAO = new PasienDAO();
        dokterDAO = new DokterDAO();

        // Buat pasien test
        testPasienId = pasienDAO.generateNoRM();
        Pasien pasien = new Pasien();
        pasien.setIdPasien(testPasienId);
        pasien.setNama("TEST PASIEN KUNJUNGAN");
        pasien.setTanggalLahir(LocalDate.of(2000, 1, 1));
        pasien.setJenisKelamin(JenisKelamin.L);
        pasien.setAlamat("Jl. Test");
        pasien.setNoHp("081234567890");
        pasien.setJenisAsuransi("REGULER");
        pasienDAO.save(pasien);

        System.out.println("=== MEMULAI TEST KUNJUNGAN DAO ===");
    }

    @Test
    @Order(1)
    void testSaveAndGetId() {
        System.out.println("TC-08: Save Kunjungan and Get ID");
        try {
            List<Dokter> dokterList = dokterDAO.findAll();
            assertFalse(dokterList.isEmpty(), "Harus ada minimal 1 dokter");

            Kunjungan kunjungan = new Kunjungan();
            kunjungan.setIdPasien(testPasienId);
            kunjungan.setIdDokter(dokterList.get(0).getIdDokter());
            kunjungan.setKeluhan("Demam tinggi selama 3 hari");
            kunjungan.setStatus(StatusKunjungan.MENUNGGU);

            int id = kunjunganDAO.saveAndGetId(kunjungan);
            assertTrue(id > 0, "ID kunjungan harus > 0");
            testKunjunganId = id;
            System.out.println("✅ Kunjungan saved with ID: " + id);
        } catch (SQLException e) {
            fail("Error saving kunjungan: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    void testFindById() {
        System.out.println("TC-09: Find Kunjungan by ID");
        try {
            Kunjungan kunjungan = kunjunganDAO.findById(testKunjunganId);
            assertNotNull(kunjungan);
            assertEquals(testKunjunganId, kunjungan.getIdKunjungan());
            assertEquals("Demam tinggi selama 3 hari", kunjungan.getKeluhan());
            System.out.println("✅ Kunjungan found: " + kunjungan.getKeluhan());
        } catch (SQLException e) {
            fail("Error finding kunjungan: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testFindByDokter() {
        System.out.println("TC-10: Find Kunjungan by Dokter");
        try {
            List<Dokter> dokterList = dokterDAO.findAll();
            int idDokter = dokterList.get(0).getIdDokter();

            List<Kunjungan> list = kunjunganDAO.findByDokter(idDokter);
            assertNotNull(list);
            assertTrue(list.size() > 0, "Harus ada kunjungan untuk dokter ini");
            System.out.println("✅ Found " + list.size() + " kunjungan for dokter ID: " + idDokter);
        } catch (SQLException e) {
            fail("Error finding by dokter: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testFindByPasien() {
        System.out.println("TC-11: Find Kunjungan by Pasien");
        try {
            List<Kunjungan> list = kunjunganDAO.findByPasien(testPasienId);
            assertNotNull(list);
            assertTrue(list.size() > 0, "Harus ada kunjungan untuk pasien ini");
            System.out.println("✅ Found " + list.size() + " kunjungan for pasien ID: " + testPasienId);
        } catch (SQLException e) {
            fail("Error finding by pasien: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    void testUpdateStatus() {
        System.out.println("TC-12: Update Kunjungan Status");
        try {
            boolean result = kunjunganDAO.updateStatus(testKunjunganId, StatusKunjungan.DIPERIKSA);
            assertTrue(result, "Update status harus berhasil");

            Kunjungan updated = kunjunganDAO.findById(testKunjunganId);
            assertEquals(StatusKunjungan.DIPERIKSA, updated.getStatus());
            System.out.println("✅ Status updated to: " + updated.getStatus().getDisplayName());
        } catch (SQLException e) {
            fail("Error updating status: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    void testGetKunjunganMenunggu() {
        System.out.println("TC-13: Get Kunjungan Menunggu");
        try {
            List<Kunjungan> list = kunjunganDAO.getKunjunganMenunggu();
            assertNotNull(list);
            for (Kunjungan k : list) {
                assertEquals(StatusKunjungan.MENUNGGU, k.getStatus());
            }
            System.out.println("✅ Found " + list.size() + " kunjungan menunggu");
        } catch (SQLException e) {
            fail("Error getting kunjungan menunggu: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    void testDeleteKunjungan() {
        System.out.println("TC-14: Delete Kunjungan");
        try {
            boolean result = kunjunganDAO.delete(testKunjunganId);
            assertTrue(result, "Delete harus berhasil");

            Kunjungan deleted = kunjunganDAO.findById(testKunjunganId);
            assertNull(deleted, "Kunjungan harus sudah terhapus");
            System.out.println("✅ Kunjungan deleted: " + testKunjunganId);
        } catch (SQLException e) {
            fail("Error deleting kunjungan: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        pasienDAO.delete(testPasienId);
        System.out.println("=== SELESAI TEST KUNJUNGAN DAO ===");
    }
}