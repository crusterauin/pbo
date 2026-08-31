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
public class StrukDAOTest {

    private static StrukDAO strukDAO;
    private static PasienDAO pasienDAO;
    private static KunjunganDAO kunjunganDAO;
    private static ResepDAO resepDAO;
    private static DokterDAO dokterDAO;
    private static ObatDAO obatDAO;
    private static ResepDetailDAO resepDetailDAO;

    private static String testPasienId;
    private static int testKunjunganId;
    private static int testResepId;
    private static int testDokterId;
    private static int testObatId;
    private static int testStrukId;

    @BeforeAll
    static void setUp() throws SQLException {
        strukDAO = new StrukDAO();
        pasienDAO = new PasienDAO();
        kunjunganDAO = new KunjunganDAO();
        resepDAO = new ResepDAO();
        dokterDAO = new DokterDAO();
        obatDAO = new ObatDAO();
        resepDetailDAO = new ResepDetailDAO();

        // 1. Buat pasien test
        testPasienId = pasienDAO.generateNoRM();
        Pasien pasien = new Pasien();
        pasien.setIdPasien(testPasienId);
        pasien.setNama("TEST STRUK PASIEN");
        pasien.setTanggalLahir(LocalDate.of(1988, 10, 20));
        pasien.setJenisKelamin(JenisKelamin.P);
        pasien.setAlamat("Jl. Struk Test No. 5");
        pasien.setNoHp("081234567896");
        pasien.setJenisAsuransi("BPJS");
        pasienDAO.save(pasien);

        // 2. Ambil dokter pertama
        List<Dokter> dokterList = dokterDAO.findAll();
        if (dokterList.isEmpty()) {
            throw new RuntimeException("Tidak ada data dokter! Pastikan seed_data.sql sudah diimport.");
        }
        testDokterId = dokterList.get(0).getIdDokter();

        // 3. Ambil obat pertama
        List<Obat> obatList = obatDAO.findAll();
        if (obatList.isEmpty()) {
            throw new RuntimeException("Tidak ada data obat! Pastikan seed_data.sql sudah diimport.");
        }
        testObatId = obatList.get(0).getIdObat();

        // 4. Buat kunjungan test
        Kunjungan kunjungan = new Kunjungan();
        kunjungan.setIdPasien(testPasienId);
        kunjungan.setIdDokter(testDokterId);
        kunjungan.setKeluhan("Sakit kepala dan mual");
        kunjungan.setStatus(StatusKunjungan.DIPERIKSA);
        kunjungan.setTanggalKunjungan(LocalDateTime.now());
        testKunjunganId = kunjunganDAO.saveAndGetId(kunjungan);

        // 5. Buat resep test
        Resep resep = new Resep();
        resep.setIdKunjungan(testKunjunganId);
        resep.setIdDokter(testDokterId);
        resep.setObatDanPerlakuan("Paracetamol 3x1 tablet, Antimo 3x1 tablet");
        resep.setStatusResep(StatusResep.DIPROSES_APOTEKER);
        resep.setCreatedAt(LocalDateTime.now());
        testResepId = resepDAO.saveAndGetId(resep);

        // 6. Tambahkan detail resep
        ResepDetail detail = new ResepDetail();
        detail.setIdResep(testResepId);
        detail.setIdObat(testObatId);
        detail.setJumlah(10);
        resepDetailDAO.save(detail);

        System.out.println("=== MEMULAI TEST STRUK DAO ===");
        System.out.println("Setup: Pasien ID=" + testPasienId +
                ", Kunjungan ID=" + testKunjunganId +
                ", Resep ID=" + testResepId +
                ", Obat ID=" + testObatId);
    }

    @Test
    @Order(1)
    void testSaveStruk() {
        System.out.println("TC-78: Save Struk");
        try {
            // Hitung biaya
            BigDecimal biayaKonsultasi = new BigDecimal("100000");
            BigDecimal biayaObat = new BigDecimal("50000");
            BigDecimal totalBayar = biayaKonsultasi.add(biayaObat);

            Struk struk = new Struk();
            struk.setIdResep(testResepId);
            struk.setBiayaKonsultasi(biayaKonsultasi);
            struk.setBiayaObat(biayaObat);
            struk.setTotalBayar(totalBayar);
            struk.setWaktuCetak(LocalDateTime.now());

            boolean result = strukDAO.save(struk);
            assertTrue(result, "Save struk harus berhasil");

            // Ambil ID dari database
            List<Struk> list = strukDAO.findByResep(testResepId);
            assertFalse(list.isEmpty(), "Harus ada struk untuk resep ini");
            testStrukId = list.get(0).getIdStruk();
            System.out.println("✅ Struk saved with ID: " + testStrukId);
        } catch (SQLException e) {
            fail("Error saving struk: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    void testSaveAndGetId() {
        System.out.println("TC-79: Save Struk and Get ID");
        try {
            BigDecimal biayaKonsultasi = new BigDecimal("150000");
            BigDecimal biayaObat = new BigDecimal("75000");
            BigDecimal totalBayar = biayaKonsultasi.add(biayaObat);

            Struk struk = new Struk();
            struk.setIdResep(testResepId);
            struk.setBiayaKonsultasi(biayaKonsultasi);
            struk.setBiayaObat(biayaObat);
            struk.setTotalBayar(totalBayar);
            struk.setWaktuCetak(LocalDateTime.now());

            int id = strukDAO.saveAndGetId(struk);
            assertTrue(id > 0, "ID struk harus > 0");

            // Verifikasi
            Struk saved = strukDAO.findById(id);
            assertNotNull(saved);
            assertEquals(0, totalBayar.compareTo(saved.getTotalBayar()),
                    "Expected: " + totalBayar + " but was: " + saved.getTotalBayar());
            System.out.println("✅ Struk saved with ID from generated key: " + id);

            // Hapus struk test tambahan
            strukDAO.delete(id);
        } catch (SQLException e) {
            fail("Error saving struk: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testFindById() {
        System.out.println("TC-80: Find Struk by ID");
        try {
            Struk struk = strukDAO.findById(testStrukId);
            assertNotNull(struk, "Struk harus ditemukan");
            assertEquals(testStrukId, struk.getIdStruk());
            assertEquals(testResepId, struk.getIdResep());
            assertNotNull(struk.getNamaPasien(), "Nama pasien harus ada (JOIN)");
            assertNotNull(struk.getNamaDokter(), "Nama dokter harus ada (JOIN)");
            assertNotNull(struk.getWaktuCetak(), "Waktu cetak harus ada");
            System.out.println("✅ Struk found: ID=" + struk.getIdStruk() +
                    ", Pasien=" + struk.getNamaPasien() +
                    ", Total=" + struk.getTotalBayar());
        } catch (SQLException e) {
            fail("Error finding struk: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testFindAll() {
        System.out.println("TC-81: Find All Struk");
        try {
            List<Struk> list = strukDAO.findAll();
            assertNotNull(list);
            assertTrue(list.size() > 0, "Harus ada minimal 1 struk");
            System.out.println("✅ Total struk: " + list.size());
        } catch (SQLException e) {
            fail("Error finding all struk: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    void testUpdateStruk() {
        System.out.println("TC-82: Update Struk");
        try {
            Struk struk = strukDAO.findById(testStrukId);
            assertNotNull(struk);

            BigDecimal newBiayaKonsultasi = new BigDecimal("200000");
            BigDecimal newBiayaObat = new BigDecimal("100000");
            BigDecimal newTotalBayar = newBiayaKonsultasi.add(newBiayaObat);

            struk.setBiayaKonsultasi(newBiayaKonsultasi);
            struk.setBiayaObat(newBiayaObat);
            struk.setTotalBayar(newTotalBayar);

            boolean result = strukDAO.update(struk);
            assertTrue(result, "Update struk harus berhasil");

            Struk updated = strukDAO.findById(testStrukId);
            assertEquals(0, newTotalBayar.compareTo(updated.getTotalBayar()),
                    "Expected: " + newTotalBayar + " but was: " + updated.getTotalBayar());
            System.out.println("✅ Struk updated: total=" + updated.getTotalBayar());
        } catch (SQLException e) {
            fail("Error updating struk: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    void testFindByResep() {
        System.out.println("TC-83: Find Struk by Resep");
        try {
            List<Struk> list = strukDAO.findByResep(testResepId);
            assertNotNull(list);
            assertTrue(list.size() > 0, "Harus ada struk untuk resep ini");
            for (Struk s : list) {
                assertEquals(testResepId, s.getIdResep());
            }
            System.out.println("✅ Found " + list.size() + " struk for resep ID: " + testResepId);
        } catch (SQLException e) {
            fail("Error finding by resep: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    void testSearchStruk() {
        System.out.println("TC-84: Search Struk");
        try {
            List<Struk> list = strukDAO.search("STRUK");
            assertNotNull(list);
            System.out.println("✅ Found " + list.size() + " struk with keyword 'STRUK'");
        } catch (SQLException e) {
            fail("Error searching struk: " + e.getMessage());
        }
    }

    @Test
    @Order(8)
    void testDeleteStruk() {
        System.out.println("TC-85: Delete Struk");
        try {
            boolean result = strukDAO.delete(testStrukId);
            assertTrue(result, "Delete struk harus berhasil");

            Struk deleted = strukDAO.findById(testStrukId);
            assertNull(deleted, "Struk harus sudah terhapus");
            System.out.println("✅ Struk deleted: " + testStrukId);
        } catch (SQLException e) {
            fail("Error deleting struk: " + e.getMessage());
        }
    }

    @Test
    @Order(9)
    void testStrukWithCompleteData() {
        System.out.println("TC-86: Create Struk with Complete Data");
        try {
            // Buat data lengkap
            BigDecimal biayaKonsultasi = new BigDecimal("150000");
            BigDecimal biayaObat = new BigDecimal("75000");
            BigDecimal totalBayar = biayaKonsultasi.add(biayaObat);

            Struk struk = new Struk();
            struk.setIdResep(testResepId);
            struk.setBiayaKonsultasi(biayaKonsultasi);
            struk.setBiayaObat(biayaObat);
            struk.setTotalBayar(totalBayar);
            struk.setWaktuCetak(LocalDateTime.now());

            // Tambahkan data tampilan
            struk.setNamaPasien("TEST STRUK PASIEN");
            struk.setNamaDokter("dr. Test");
            struk.setNoRekamMedis(testPasienId);
            struk.setJenisAsuransi("BPJS");

            int id = strukDAO.saveAndGetId(struk);
            assertTrue(id > 0);

            Struk saved = strukDAO.findById(id);
            assertNotNull(saved);
            assertEquals("TEST STRUK PASIEN", saved.getNamaPasien());
            assertEquals("BPJS", saved.getJenisAsuransi());

            System.out.println("✅ Complete Struk created: ID=" + id +
                    ", Pasien=" + saved.getNamaPasien() +
                    ", Asuransi=" + saved.getJenisAsuransi());

            // Cleanup
            strukDAO.delete(id);
        } catch (SQLException e) {
            fail("Error creating complete struk: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        // HAPUS DARI CHILD DULU
        strukDAO.deleteByResep(testResepId);  // ← TAMBAHKAN
        resepDetailDAO.deleteByResep(testResepId);
        resepDAO.delete(testResepId);
        kunjunganDAO.delete(testKunjunganId);
        pasienDAO.delete(testPasienId);
    }
}