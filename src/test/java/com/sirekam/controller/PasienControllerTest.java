package com.sirekam.controller;

import com.sirekam.model.Pasien;
import com.sirekam.model.enums.JenisKelamin;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PasienControllerTest {

    private static PasienController controller;
    private static String testId;

    @BeforeAll
    static void setUp() {
        controller = new PasienController();
        System.out.println("=== MEMULAI TEST PASIEN CONTROLLER ===");
    }

    @Test
    @Order(1)
    void testGenerateNoRM() {
        System.out.println("TC-21: Generate No RM (Controller)");
        try {
            String noRM = controller.generateNoRM();
            assertNotNull(noRM);
            assertTrue(noRM.startsWith("RM-"));
            System.out.println("✅ No RM generated: " + noRM);
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    void testSimpanPasien() {
        System.out.println("TC-22: Simpan Pasien (Controller)");
        try {
            testId = controller.generateNoRM();
            Pasien pasien = new Pasien();
            pasien.setIdPasien(testId);
            pasien.setNama("CONTROLLER TEST");
            pasien.setTanggalLahir(LocalDate.of(1995, 5, 5));
            pasien.setJenisKelamin(JenisKelamin.P);
            pasien.setAlamat("Jl. Controller");
            pasien.setNoHp("081234567891");
            pasien.setJenisAsuransi("BPJS");

            boolean result = controller.simpan(pasien);
            assertTrue(result);
            System.out.println("✅ Pasien saved: " + testId);
        } catch (Exception e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testCariById() {
        System.out.println("TC-23: Cari Pasien by ID (Controller)");
        try {
            Pasien pasien = controller.cariById(testId);
            assertNotNull(pasien);
            assertEquals("CONTROLLER TEST", pasien.getNama());
            System.out.println("✅ Pasien found: " + pasien.getNama());
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testCariByAsuransi() {
        System.out.println("TC-24: Cari Pasien by Asuransi (Controller)");
        try {
            List<Pasien> list = controller.cariByAsuransi("BPJS");
            assertNotNull(list);
            for (Pasien p : list) {
                assertEquals("BPJS", p.getJenisAsuransi());
            }
            System.out.println("✅ Found " + list.size() + " BPJS pasien");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    void testHapusPasien() {
        System.out.println("TC-25: Hapus Pasien (Controller)");
        try {
            boolean result = controller.hapus(testId);
            assertTrue(result);

            Pasien deleted = controller.cariById(testId);
            assertNull(deleted);
            System.out.println("✅ Pasien deleted: " + testId);
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== SELESAI TEST PASIEN CONTROLLER ===");
    }
}