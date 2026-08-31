package com.sirekam.dao;

import com.sirekam.model.Pasien;
import com.sirekam.model.enums.JenisKelamin;
import com.sirekam.util.DatabaseManager;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PasienDAOTest {

    private static PasienDAO pasienDAO;
    private static String testIdPasien;

    @BeforeAll
    static void setUp() {
        pasienDAO = new PasienDAO();
        System.out.println("=== MEMULAI TEST PASIEN DAO ===");
    }

    @Test
    @Order(1)
    void testGenerateNoRM() {
        System.out.println("TC-01: Generate No RM");
        try {
            String noRM = pasienDAO.generateNoRM();
            assertNotNull(noRM);
            assertTrue(noRM.startsWith("RM-"));
            System.out.println("✅ No RM generated: " + noRM);
            testIdPasien = noRM;
        } catch (SQLException e) {
            fail("Error generating No RM: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    void testSavePasien() {
        System.out.println("TC-02: Save Pasien Baru");
        try {
            String noRM = pasienDAO.generateNoRM();
            Pasien pasien = new Pasien();
            pasien.setIdPasien(noRM);
            pasien.setNama("TEST USER");
            pasien.setTanggalLahir(LocalDate.of(1990, 1, 1));
            pasien.setJenisKelamin(JenisKelamin.L);
            pasien.setAlamat("Jl. Test No. 1");
            pasien.setNoHp("081234567890");
            pasien.setJenisAsuransi("REGULER");

            boolean result = pasienDAO.save(pasien);
            assertTrue(result, "Save pasien harus berhasil");
            testIdPasien = noRM;
            System.out.println("✅ Pasien saved with ID: " + noRM);

            // Verifikasi
            Pasien saved = pasienDAO.findById(noRM);
            assertNotNull(saved);
            assertEquals("TEST USER", saved.getNama());
        } catch (SQLException e) {
            fail("Error saving pasien: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testFindById() {
        System.out.println("TC-03: Find Pasien by ID");
        try {
            Pasien pasien = pasienDAO.findById(testIdPasien);
            assertNotNull(pasien, "Pasien harus ditemukan");
            assertEquals(testIdPasien, pasien.getIdPasien());
            System.out.println("✅ Pasien found: " + pasien.getNama());
        } catch (SQLException e) {
            fail("Error finding pasien: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testFindAll() {
        System.out.println("TC-04: Find All Pasien");
        try {
            List<Pasien> list = pasienDAO.findAll();
            assertNotNull(list);
            assertTrue(list.size() > 0, "Harus ada minimal 1 pasien");
            System.out.println("✅ Total pasien: " + list.size());
        } catch (SQLException e) {
            fail("Error finding all pasien: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    void testSearchPasien() {
        System.out.println("TC-05: Search Pasien");
        try {
            List<Pasien> list = pasienDAO.search("TEST");
            assertNotNull(list);
            assertTrue(list.size() > 0, "Pasien dengan keyword 'TEST' harus ditemukan");
            System.out.println("✅ Found " + list.size() + " pasien with keyword 'TEST'");
        } catch (SQLException e) {
            fail("Error searching pasien: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    void testFindByAsuransi() {
        System.out.println("TC-06: Find Pasien by Asuransi");
        try {
            List<Pasien> list = pasienDAO.findByAsuransi("BPJS");
            assertNotNull(list);
            for (Pasien p : list) {
                assertEquals("BPJS", p.getJenisAsuransi());
            }
            System.out.println("✅ Found " + list.size() + " BPJS pasien");
        } catch (SQLException e) {
            fail("Error finding by asuransi: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    void testDeletePasien() {
        System.out.println("TC-07: Delete Pasien");
        try {
            boolean result = pasienDAO.delete(testIdPasien);
            assertTrue(result, "Delete harus berhasil");

            // Verifikasi sudah terhapus
            Pasien deleted = pasienDAO.findById(testIdPasien);
            assertNull(deleted, "Pasien harus sudah terhapus");
            System.out.println("✅ Pasien deleted: " + testIdPasien);
        } catch (SQLException e) {
            fail("Error deleting pasien: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== SELESAI TEST PASIEN DAO ===");
    }
}