package com.sirekam.dao;

import com.sirekam.model.*;
import com.sirekam.model.enums.JenisKelamin;
import com.sirekam.model.enums.StatusKunjungan;
import com.sirekam.model.enums.StatusResep;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ResepDetailDAOTest {

    private static ResepDetailDAO resepDetailDAO;
    private static PasienDAO pasienDAO;
    private static KunjunganDAO kunjunganDAO;
    private static ResepDAO resepDAO;
    private static DokterDAO dokterDAO;
    private static ObatDAO obatDAO;

    private static String testPasienId;
    private static int testKunjunganId;
    private static int testResepId;
    private static int testDokterId;
    private static int testObatId1;
    private static int testObatId2;
    private static int testResepDetailId;

    @BeforeAll
    static void setUp() throws SQLException {
        resepDetailDAO = new ResepDetailDAO();
        pasienDAO = new PasienDAO();
        kunjunganDAO = new KunjunganDAO();
        resepDAO = new ResepDAO();
        dokterDAO = new DokterDAO();
        obatDAO = new ObatDAO();

        // 1. Buat pasien test
        testPasienId = pasienDAO.generateNoRM();
        Pasien pasien = new Pasien();
        pasien.setIdPasien(testPasienId);
        pasien.setNama("TEST RESEP DETAIL PASIEN");
        pasien.setTanggalLahir(LocalDate.of(1992, 3, 10));
        pasien.setJenisKelamin(JenisKelamin.P);
        pasien.setAlamat("Jl. Detail Resep No. 7");
        pasien.setNoHp("081234567897");
        pasien.setJenisAsuransi("REGULER");
        pasienDAO.save(pasien);

        // 2. Ambil dokter pertama
        List<Dokter> dokterList = dokterDAO.findAll();
        if (dokterList.isEmpty()) {
            throw new RuntimeException("Tidak ada data dokter! Pastikan seed_data.sql sudah diimport.");
        }
        testDokterId = dokterList.get(0).getIdDokter();

        // 3. Ambil dua obat pertama
        List<Obat> obatList = obatDAO.findAll();
        if (obatList.size() < 2) {
            throw new RuntimeException("Minimal butuh 2 data obat! Pastikan seed_data.sql sudah diimport.");
        }
        testObatId1 = obatList.get(0).getIdObat();
        testObatId2 = obatList.get(1).getIdObat();

        // 4. Buat kunjungan test
        Kunjungan kunjungan = new Kunjungan();
        kunjungan.setIdPasien(testPasienId);
        kunjungan.setIdDokter(testDokterId);
        kunjungan.setKeluhan("Demam dan nyeri sendi");
        kunjungan.setStatus(StatusKunjungan.DIPERIKSA);
        kunjungan.setTanggalKunjungan(LocalDateTime.now());
        testKunjunganId = kunjunganDAO.saveAndGetId(kunjungan);

        // 5. Buat resep test
        Resep resep = new Resep();
        resep.setIdKunjungan(testKunjunganId);
        resep.setIdDokter(testDokterId);
        resep.setObatDanPerlakuan("Paracetamol 3x1 tablet, Amoxicillin 3x1 kapsul");
        resep.setStatusResep(StatusResep.MENUNGGU);
        resep.setCreatedAt(LocalDateTime.now());
        testResepId = resepDAO.saveAndGetId(resep);

        System.out.println("=== MEMULAI TEST RESEP DETAIL DAO ===");
        System.out.println("Setup: Pasien ID=" + testPasienId +
                ", Kunjungan ID=" + testKunjunganId +
                ", Resep ID=" + testResepId +
                ", Obat1 ID=" + testObatId1 +
                ", Obat2 ID=" + testObatId2);
    }

    @Test
    @Order(1)
    void testSaveResepDetail() {
        System.out.println("TC-87: Save Resep Detail");
        try {
            ResepDetail detail = new ResepDetail();
            detail.setIdResep(testResepId);
            detail.setIdObat(testObatId1);
            detail.setJumlah(5);

            boolean result = resepDetailDAO.save(detail);
            assertTrue(result, "Save resep detail harus berhasil");

            // Verifikasi
            List<ResepDetail> list = resepDetailDAO.findByResep(testResepId);
            assertFalse(list.isEmpty(), "Harus ada detail resep");
            testResepDetailId = list.get(0).getIdResepDetail();
            System.out.println("✅ ResepDetail saved with ID: " + testResepDetailId);
        } catch (SQLException e) {
            fail("Error saving resep detail: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    void testSaveAndGetId() {
        System.out.println("TC-88: Save Resep Detail and Get ID");
        try {
            ResepDetail detail = new ResepDetail();
            detail.setIdResep(testResepId);
            detail.setIdObat(testObatId2);
            detail.setJumlah(3);

            int id = resepDetailDAO.saveAndGetId(detail);
            assertTrue(id > 0, "ID resep detail harus > 0");

            // Verifikasi
            ResepDetail saved = resepDetailDAO.findById(id);
            assertNotNull(saved);
            assertEquals(testObatId2, saved.getIdObat());
            assertEquals(3, saved.getJumlah());
            System.out.println("✅ ResepDetail saved with ID from generated key: " + id);

            // Hapus detail test tambahan
            resepDetailDAO.delete(id);
        } catch (SQLException e) {
            fail("Error saving resep detail: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testFindById() {
        System.out.println("TC-89: Find Resep Detail by ID");
        try {
            ResepDetail detail = resepDetailDAO.findById(testResepDetailId);
            assertNotNull(detail, "Resep detail harus ditemukan");
            assertEquals(testResepDetailId, detail.getIdResepDetail());
            assertEquals(testResepId, detail.getIdResep());
            assertEquals(testObatId1, detail.getIdObat());
            assertEquals(5, detail.getJumlah());
            assertNotNull(detail.getNamaObat(), "Nama obat harus ada (JOIN)");
            assertNotNull(detail.getHargaSatuan(), "Harga satuan harus ada (JOIN)");
            System.out.println("✅ ResepDetail found: ID=" + detail.getIdResepDetail() +
                    ", Obat=" + detail.getNamaObat() +
                    ", Jumlah=" + detail.getJumlah());
        } catch (SQLException e) {
            fail("Error finding resep detail: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testFindAll() {
        System.out.println("TC-90: Find All Resep Detail");
        try {
            List<ResepDetail> list = resepDetailDAO.findAll();
            assertNotNull(list);
            assertTrue(list.size() > 0, "Harus ada minimal 1 resep detail");
            System.out.println("✅ Total resep detail: " + list.size());
        } catch (SQLException e) {
            fail("Error finding all resep detail: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    void testUpdateResepDetail() {
        System.out.println("TC-91: Update Resep Detail");
        try {
            ResepDetail detail = resepDetailDAO.findById(testResepDetailId);
            assertNotNull(detail);

            detail.setJumlah(10);

            boolean result = resepDetailDAO.update(detail);
            assertTrue(result, "Update resep detail harus berhasil");

            ResepDetail updated = resepDetailDAO.findById(testResepDetailId);
            assertEquals(10, updated.getJumlah());
            System.out.println("✅ ResepDetail updated: jumlah=" + updated.getJumlah());
        } catch (SQLException e) {
            fail("Error updating resep detail: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    void testSearchResepDetail() {
        System.out.println("TC-92: Search Resep Detail");
        try {
            List<ResepDetail> list = resepDetailDAO.search("Paracetamol");
            assertNotNull(list);
            System.out.println("✅ Found " + list.size() + " resep detail with keyword 'Paracetamol'");
        } catch (SQLException e) {
            fail("Error searching resep detail: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    void testFindByResep() {
        System.out.println("TC-93: Find Resep Detail by Resep");
        try {
            List<ResepDetail> list = resepDetailDAO.findByResep(testResepId);
            assertNotNull(list);
            assertTrue(list.size() > 0, "Harus ada detail resep untuk resep ini");
            for (ResepDetail rd : list) {
                assertEquals(testResepId, rd.getIdResep());
            }
            System.out.println("✅ Found " + list.size() + " resep detail for resep ID: " + testResepId);
        } catch (SQLException e) {
            fail("Error finding by resep: " + e.getMessage());
        }
    }

    @Test
    @Order(8)
    void testFindByObat() {
        System.out.println("TC-94: Find Resep Detail by Obat");
        try {
            List<ResepDetail> list = resepDetailDAO.findByObat(testObatId1);
            assertNotNull(list);
            for (ResepDetail rd : list) {
                assertEquals(testObatId1, rd.getIdObat());
            }
            System.out.println("✅ Found " + list.size() + " resep detail for obat ID: " + testObatId1);
        } catch (SQLException e) {
            fail("Error finding by obat: " + e.getMessage());
        }
    }

    @Test
    @Order(9)
    void testCountByResep() {
        System.out.println("TC-95: Count Resep Detail by Resep");
        try {
            int count = resepDetailDAO.countByResep(testResepId);
            assertTrue(count > 0, "Harus ada minimal 1 detail");
            System.out.println("✅ Total detail for resep ID " + testResepId + ": " + count);
        } catch (SQLException e) {
            fail("Error counting resep detail: " + e.getMessage());
        }
    }

    @Test
    @Order(10)
    void testGetTotalHargaByResep() {
        System.out.println("TC-96: Get Total Harga by Resep");
        try {
            BigDecimal total = resepDetailDAO.getTotalHargaByResep(testResepId);
            assertNotNull(total);
            assertTrue(total.compareTo(BigDecimal.ZERO) > 0, "Total harga harus > 0");
            System.out.println("✅ Total harga for resep ID " + testResepId + ": Rp " + total);
        } catch (SQLException e) {
            fail("Error getting total harga: " + e.getMessage());
        }
    }

    @Test
    @Order(11)
    void testDeleteResepDetail() {
        System.out.println("TC-97: Delete Resep Detail");
        try {
            boolean result = resepDetailDAO.delete(testResepDetailId);
            assertTrue(result, "Delete resep detail harus berhasil");

            ResepDetail deleted = resepDetailDAO.findById(testResepDetailId);
            assertNull(deleted, "Resep detail harus sudah terhapus");
            System.out.println("✅ ResepDetail deleted: " + testResepDetailId);
        } catch (SQLException e) {
            fail("Error deleting resep detail: " + e.getMessage());
        }
    }

    @Test
    @Order(12)
    void testDeleteByResep() {
        System.out.println("TC-98: Delete All Resep Detail by Resep");
        try {
            // Tambahkan beberapa detail baru
            ResepDetail d1 = new ResepDetail();
            d1.setIdResep(testResepId);
            d1.setIdObat(testObatId1);
            d1.setJumlah(2);
            resepDetailDAO.save(d1);

            ResepDetail d2 = new ResepDetail();
            d2.setIdResep(testResepId);
            d2.setIdObat(testObatId2);
            d2.setJumlah(3);
            resepDetailDAO.save(d2);

            // Cek jumlah sebelum delete
            int before = resepDetailDAO.countByResep(testResepId);
            assertTrue(before >= 2, "Harus ada minimal 2 detail");

            // Delete all by resep
            boolean result = resepDetailDAO.deleteByResep(testResepId);
            assertTrue(result, "Delete by resep harus berhasil");

            // Cek jumlah setelah delete
            int after = resepDetailDAO.countByResep(testResepId);
            assertEquals(0, after, "Semua detail harus terhapus");
            System.out.println("✅ All " + before + " resep details deleted for resep ID: " + testResepId);
        } catch (SQLException e) {
            fail("Error deleting by resep: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        // Hapus dalam urutan terbalik
        resepDetailDAO.deleteByResep(testResepId);
        resepDAO.delete(testResepId);
        kunjunganDAO.delete(testKunjunganId);
        pasienDAO.delete(testPasienId);
        System.out.println("=== SELESAI TEST RESEP DETAIL DAO ===");
    }
}