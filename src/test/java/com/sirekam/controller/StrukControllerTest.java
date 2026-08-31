package com.sirekam.controller;

import com.sirekam.dao.*;
import com.sirekam.model.*;
import com.sirekam.model.enums.JenisKelamin;
import com.sirekam.model.enums.StatusKunjungan;
import com.sirekam.model.enums.StatusResep;
import com.sirekam.pattern.strategy.BPJSBiayaStrategy;
import com.sirekam.pattern.strategy.RegulerBiayaStrategy;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StrukControllerTest {

    // ==================== CONTROLLER ====================
    private static StrukController controller;
    private static PasienController pasienController;
    private static KunjunganController kunjunganController;
    private static ResepController resepController;
    private static DokterController dokterController;
    private static ObatController obatController;

    // ==================== DAO (LANGSUNG) ====================
    private static StrukDAO strukDAO;
    private static ResepDetailDAO resepDetailDAO;
    private static ResepDAO resepDAO;
    private static KunjunganDAO kunjunganDAO;
    private static PasienDAO pasienDAO;

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
        controller = new StrukController();
        pasienController = new PasienController();
        kunjunganController = new KunjunganController();
        resepController = new ResepController();
        dokterController = new DokterController();
        obatController = new ObatController();

        // Inisialisasi DAO (LANGSUNG)
        strukDAO = new StrukDAO();
        resepDetailDAO = new ResepDetailDAO();
        resepDAO = new ResepDAO();
        kunjunganDAO = new KunjunganDAO();
        pasienDAO = new PasienDAO();

        // ============================================================
        // 1. Buat pasien test
        // ============================================================
        testPasienId = pasienController.generateNoRM();
        Pasien pasien = new Pasien();
        pasien.setIdPasien(testPasienId);
        pasien.setNama("TEST STRUK");
        pasien.setTanggalLahir(LocalDate.of(1990, 5, 5));
        pasien.setJenisKelamin(JenisKelamin.L);
        pasien.setAlamat("Jl. Struk Test");
        pasien.setNoHp("081234567894");
        pasien.setJenisAsuransi("BPJS");

        // ✅ PAKAI simpan() (dari GenericController)
        boolean pasienSaved = pasienController.simpan(pasien);
        System.out.println("Pasien saved: " + pasienSaved);

        // ============================================================
        // 2. Ambil dokter
        // ============================================================
        // ✅ PAKAI getAllDokter()
        List<Dokter> dokterList = dokterController.getAllDokter();
        testDokterId = dokterList.get(0).getIdDokter();
        System.out.println("Dokter ID: " + testDokterId);

        // ============================================================
        // 3. Ambil obat dan reset stok
        // ============================================================
        // ✅ PAKAI cariSemua() (dari GenericController)
        List<Obat> obatList = obatController.cariSemua();
        testObatId = obatList.get(0).getIdObat();
        System.out.println("Obat ID: " + testObatId);

        // ✅ PAKAI findById() dan update() (dari GenericController)
        Obat obat = obatController.cariById(testObatId);
        if (obat != null) {
            obat.setStok(100);
            boolean obatUpdated = obatController.update(obat);
            System.out.println("Obat stok reset: " + obatUpdated);
        }

        // ============================================================
        // 4. Buat kunjungan
        // ============================================================
        Kunjungan kunjungan = new Kunjungan();
        kunjungan.setIdPasien(testPasienId);
        kunjungan.setIdDokter(testDokterId);
        kunjungan.setKeluhan("Flu berat");
        kunjungan.setStatus(StatusKunjungan.MENUNGGU);

        // ✅ PAKAI simpanDanGetId()
        testKunjunganId = kunjunganController.simpanDanGetId(kunjungan);
        System.out.println("Kunjungan ID: " + testKunjunganId);

        // ============================================================
        // 5. Buat resep
        // ============================================================
        Resep resep = new Resep();
        resep.setIdKunjungan(testKunjunganId);
        resep.setIdDokter(testDokterId);
        resep.setObatDanPerlakuan("Paracetamol 3x1, Vitamin C");
        resep.setStatusResep(StatusResep.MENUNGGU);

        // ✅ PAKAI buatResep()
        testResepId = resepController.buatResep(resep);
        System.out.println("Resep ID: " + testResepId);

        // ============================================================
        // 6. Assign obat
        // ============================================================
        // ✅ PAKAI assignObat()
        boolean obatAssigned = resepController.assignObat(testResepId, testObatId, 10);
        System.out.println("Obat assigned: " + obatAssigned);

        System.out.println("=== MEMULAI TEST STRUK CONTROLLER ===");
    }

    // ==================== TEST CASES ====================

    @Test
    @Order(1)
    void testSetStrategy() {
        System.out.println("TC-63: Set Strategy");
        controller.setStrategy(new RegulerBiayaStrategy());
        assertEquals("Reguler (Tanpa Diskon)", controller.getStrategy().getNamaStrategy());
        System.out.println("✅ Strategy set to Reguler");

        controller.setStrategy(new BPJSBiayaStrategy());
        assertEquals("BPJS (Diskon 30% Konsultasi)", controller.getStrategy().getNamaStrategy());
        System.out.println("✅ Strategy set to BPJS");
    }

    @Test
    @Order(2)
    void testHitungTotalBayarReguler() {
        System.out.println("TC-64: Hitung Total Bayar - Reguler");
        try {
            controller.setStrategy(new RegulerBiayaStrategy());
            BigDecimal total = controller.hitungTotalBayar(testResepId);
            assertNotNull(total);
            assertTrue(total.compareTo(BigDecimal.ZERO) > 0);
            System.out.println("✅ Total Reguler: " + total);
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testHitungTotalBayarBPJS() {
        System.out.println("TC-65: Hitung Total Bayar - BPJS");
        try {
            controller.setStrategy(new BPJSBiayaStrategy());
            BigDecimal total = controller.hitungTotalBayar(testResepId);
            assertNotNull(total);
            assertTrue(total.compareTo(BigDecimal.ZERO) > 0);
            System.out.println("✅ Total BPJS: " + total);
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testCetakStruk() {
        System.out.println("TC-66: Cetak Struk");
        try {
            BigDecimal biayaKonsultasi = new BigDecimal("100000");
            Struk struk = controller.cetakStruk(testResepId, biayaKonsultasi);

            assertNotNull(struk);
            assertTrue(struk.getIdStruk() > 0);
            assertEquals(testResepId, struk.getIdResep());
            assertNotNull(struk.getWaktuCetak());

            System.out.println("✅ Struk printed with ID: " + struk.getIdStruk());
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    void testGenerateStrukText() {
        System.out.println("TC-67: Generate Struk Text");
        try {
            BigDecimal biayaKonsultasi = new BigDecimal("100000");
            Struk struk = controller.cetakStruk(testResepId, biayaKonsultasi);

            String text = controller.generateStrukText(struk);
            assertNotNull(text);
            assertTrue(text.contains("STRUK PEMBAYARAN"));
            assertTrue(text.contains("KLINIK SEHAT SEJAHTERA"));
            assertTrue(text.contains("TOTAL BAYAR"));

            System.out.println("✅ Struk text generated successfully");
            System.out.println(text);
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    // ==================== TEARDOWN ====================
    @AfterAll
    static void tearDown() {
        System.out.println("🧹 Cleaning up test data...");

        try {
            // HAPUS DALAM URUTAN TERBALIK (CHILD DULU)

            // 1. Hapus struk (child dari resep)
            if (testResepId > 0) {
                try {
                    strukDAO.deleteByResep(testResepId);
                    System.out.println("🧹 Deleted struk for resep: " + testResepId);
                } catch (SQLException e) {
                    System.err.println("⚠️ Gagal hapus struk: " + e.getMessage());
                }
            }

            // 2. Hapus resep detail (child dari resep)
            if (testResepId > 0) {
                try {
                    resepDetailDAO.deleteByResep(testResepId);
                    System.out.println("🧹 Deleted resep detail for resep: " + testResepId);
                } catch (SQLException e) {
                    System.err.println("⚠️ Gagal hapus resep detail: " + e.getMessage());
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

        System.out.println("=== SELESAI TEST STRUK CONTROLLER ===");
    }
}