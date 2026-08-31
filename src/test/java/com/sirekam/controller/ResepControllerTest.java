package com.sirekam.controller;

import com.sirekam.dao.*;
import com.sirekam.model.*;
import com.sirekam.model.enums.JenisKelamin;
import com.sirekam.model.enums.StatusKunjungan;
import com.sirekam.model.enums.StatusResep;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ResepControllerTest {

    // ==================== CONTROLLER ====================
    private static ResepController controller;
    private static PasienController pasienController;
    private static KunjunganController kunjunganController;
    private static DokterController dokterController;
    private static ObatController obatController;

    // ==================== DAO ====================
    private static ResepDAO resepDAO;
    private static ResepDetailDAO resepDetailDAO;
    private static KunjunganDAO kunjunganDAO;
    private static PasienDAO pasienDAO;
    private static StrukDAO strukDAO;

    // ==================== DATA TEST ====================
    private static String testPasienId;
    private static int testKunjunganId;
    private static int testResepId;
    private static int testDokterId;
    private static int testObatId;

    // ==================== SETUP ====================
    @BeforeAll
    static void setUp() throws Exception {
        // Inisialisasi Controller
        controller = new ResepController();
        pasienController = new PasienController();
        kunjunganController = new KunjunganController();
        dokterController = new DokterController();
        obatController = new ObatController();

        // Inisialisasi DAO
        resepDAO = new ResepDAO();
        resepDetailDAO = new ResepDetailDAO();
        kunjunganDAO = new KunjunganDAO();
        pasienDAO = new PasienDAO();
        strukDAO = new StrukDAO();

        // ============================================================
        // 1. Buat pasien test
        // ============================================================
        testPasienId = pasienController.generateNoRM();
        Pasien pasien = new Pasien();
        pasien.setIdPasien(testPasienId);
        pasien.setNama("TEST RESEP");
        pasien.setTanggalLahir(LocalDate.of(2000, 1, 1));
        pasien.setJenisKelamin(JenisKelamin.L);
        pasien.setAlamat("Jl. Resep Test");
        pasien.setNoHp("081234567893");
        pasien.setJenisAsuransi("REGULER");
        pasienController.simpan(pasien);

        // ============================================================
        // 2. Ambil dokter
        // ============================================================
        List<Dokter> dokterList = dokterController.getAllDokter();
        testDokterId = dokterList.get(0).getIdDokter();

        // ============================================================
        // 3. Ambil obat dan reset stok
        // ============================================================
        List<Obat> obatList = obatController.cariSemua();
        testObatId = obatList.get(0).getIdObat();

        Obat obat = obatController.findById(testObatId);
        if (obat != null) {
            obat.setStok(100);
            obatController.update(obat);
            System.out.println("🔍 [DEBUG] Stok obat ID " + testObatId + " di-reset ke 100");
        }

        // ============================================================
        // 4. Buat kunjungan
        // ============================================================
        Kunjungan kunjungan = new Kunjungan();
        kunjungan.setIdPasien(testPasienId);
        kunjungan.setIdDokter(testDokterId);
        kunjungan.setKeluhan("Sakit perut");
        kunjungan.setStatus(StatusKunjungan.MENUNGGU);
        testKunjunganId = kunjunganController.simpanDanGetId(kunjungan);

        System.out.println("=== MEMULAI TEST RESEP CONTROLLER ===");
    }

    // ==================== TEST CASES ====================

    @Test
    @Order(1)
    void testBuatResep() {
        System.out.println("TC-57: Buat Resep");
        try {
            Resep resep = new Resep();
            resep.setIdKunjungan(testKunjunganId);
            resep.setIdDokter(testDokterId);
            resep.setObatDanPerlakuan("Paracetamol 3x1, Istirahat total");
            resep.setStatusResep(StatusResep.MENUNGGU);

            int id = controller.buatResep(resep);
            assertTrue(id > 0);
            testResepId = id;
            System.out.println("✅ Resep saved with ID: " + id);
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    void testGetResepById() {
        System.out.println("TC-58: Get Resep by ID");
        try {
            Resep resep = controller.getById(testResepId);
            assertNotNull(resep);
            assertEquals(testKunjunganId, resep.getIdKunjungan());
            System.out.println("✅ Resep found: " + resep.getIdResep());
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testAssignObat() {
        System.out.println("TC-59: Assign Obat");
        try {
            boolean result = controller.assignObat(testResepId, testObatId, 5);
            assertTrue(result, "Assign obat harus berhasil");

            List<ResepDetail> details = controller.getDetailResep(testResepId);
            assertFalse(details.isEmpty());
            assertEquals(testObatId, details.get(0).getIdObat());
            System.out.println("✅ Obat assigned: " + details.get(0).getNamaObat());
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testAssignObatStokTidakCukup() {
        System.out.println("TC-60: Assign Obat - Stok Tidak Cukup");
        try {
            Obat obat = obatController.findById(testObatId);
            int stok = obat.getStok();

            boolean result = controller.assignObat(testResepId, testObatId, stok + 100);
            assertFalse(result, "Assign dengan stok tidak cukup harus gagal");
            System.out.println("✅ Assign failed correctly (stok tidak cukup)");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    void testUpdateStatusResep() {
        System.out.println("TC-61: Update Status Resep");
        try {
            boolean result = controller.updateStatus(testResepId, StatusResep.DIPROSES_APOTEKER);
            assertTrue(result);

            Resep updated = controller.getById(testResepId);
            assertEquals(StatusResep.DIPROSES_APOTEKER, updated.getStatusResep());
            System.out.println("✅ Status updated to: " + updated.getStatusResep().getDisplayName());
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    void testGetResepMenunggu() {
        System.out.println("TC-62: Get Resep Menunggu");
        try {
            List<Resep> list = controller.getResepMenunggu();
            assertNotNull(list);
            for (Resep r : list) {
                assertEquals(StatusResep.MENUNGGU, r.getStatusResep());
            }
            System.out.println("✅ Found " + list.size() + " resep menunggu");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    // ==================== TEARDOWN ====================
    @AfterAll
    static void tearDown() {
        System.out.println("🧹 Cleaning up ResepControllerTest data...");

        try {
            // ============================================================
            // HAPUS DALAM URUTAN TERBALIK (CHILD DULU)
            // ============================================================

            // 1. Hapus resep detail (child dari resep)
            if (testResepId > 0) {
                try {
                    resepDetailDAO.deleteByResep(testResepId);
                    System.out.println("🧹 Deleted resep detail for resep: " + testResepId);
                } catch (SQLException e) {
                    System.err.println("⚠️ Gagal hapus resep detail: " + e.getMessage());
                }
            }

            // 2. Hapus struk (child dari resep)
            if (testResepId > 0) {
                try {
                    List<Struk> strukList = strukDAO.findByResep(testResepId);
                    for (Struk s : strukList) {
                        strukDAO.delete(s.getIdStruk());
                    }
                    System.out.println("🧹 Deleted struk for resep: " + testResepId);
                } catch (SQLException e) {
                    System.err.println("⚠️ Gagal hapus struk: " + e.getMessage());
                }
            }

            // 3. Hapus resep (child dari kunjungan)
            if (testResepId > 0) {
                try {
                    resepDAO.delete(testResepId);
                    System.out.println("🧹 Deleted resep: " + testResepId);
                } catch (SQLException e) {
                    System.err.println("⚠️ Gagal hapus resep: " + e.getMessage());
                }
            }

            // 4. Hapus kunjungan (child dari pasien)
            if (testKunjunganId > 0) {
                try {
                    kunjunganDAO.delete(testKunjunganId);
                    System.out.println("🧹 Deleted kunjungan: " + testKunjunganId);
                } catch (SQLException e) {
                    System.err.println("⚠️ Gagal hapus kunjungan: " + e.getMessage());
                }
            }

            // 5. Hapus pasien
            if (testPasienId != null && !testPasienId.isEmpty()) {
                try {
                    pasienDAO.delete(testPasienId);
                    System.out.println("🧹 Deleted pasien: " + testPasienId);
                } catch (SQLException e) {
                    System.err.println("⚠️ Gagal hapus pasien: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("⚠️ Error in tearDown: " + e.getMessage());
        }

        System.out.println("=== SELESAI TEST RESEP CONTROLLER ===");
    }
}