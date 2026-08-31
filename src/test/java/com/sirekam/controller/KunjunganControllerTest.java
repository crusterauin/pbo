package com.sirekam.controller;

import com.sirekam.model.*;
import com.sirekam.model.enums.JenisKelamin;
import com.sirekam.model.enums.StatusKunjungan;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KunjunganControllerTest {

    private static KunjunganController controller;
    private static PasienController pasienController;
    private static DokterController dokterController;
    private static String testPasienId;
    private static int testKunjunganId;

    @BeforeAll
    static void setUp() throws Exception {
        controller = new KunjunganController();
        pasienController = new PasienController();
        dokterController = new DokterController();

        // Buat pasien test
        testPasienId = pasienController.generateNoRM();
        Pasien pasien = new Pasien();
        pasien.setIdPasien(testPasienId);
        pasien.setNama("TEST KUNJUNGAN CTRL");
        pasien.setTanggalLahir(LocalDate.of(1998, 8, 8));
        pasien.setJenisKelamin(JenisKelamin.L);
        pasien.setAlamat("Jl. Test Ctrl");
        pasien.setNoHp("081234567892");
        pasien.setJenisAsuransi("REGULER");
        pasienController.simpan(pasien);

        System.out.println("=== MEMULAI TEST KUNJUNGAN CONTROLLER ===");
    }

    @Test
    @Order(1)
    void testSimpanDanGetId() {
        System.out.println("TC-26: Simpan Kunjungan dan Get ID (Controller)");
        try {
            List<Dokter> dokterList = dokterController.getAllDokter();
            assertFalse(dokterList.isEmpty());

            Kunjungan kunjungan = new Kunjungan();
            kunjungan.setIdPasien(testPasienId);
            kunjungan.setIdDokter(dokterList.get(0).getIdDokter());
            kunjungan.setKeluhan("Sakit kepala 2 hari");
            kunjungan.setStatus(StatusKunjungan.MENUNGGU);

            int id = controller.simpanDanGetId(kunjungan);
            assertTrue(id > 0);
            testKunjunganId = id;
            System.out.println("✅ Kunjungan saved with ID: " + id);
        } catch (Exception e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    void testGetById() {
        System.out.println("TC-27: Get Kunjungan by ID (Controller)");
        try {
            Kunjungan kunjungan = controller.getById(testKunjunganId);
            assertNotNull(kunjungan);
            assertEquals("Sakit kepala 2 hari", kunjungan.getKeluhan());
            System.out.println("✅ Kunjungan found");
        } catch (Exception e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testGetByDokter() {
        System.out.println("TC-28: Get Kunjungan by Dokter (Controller)");
        try {
            List<Dokter> dokterList = dokterController.getAllDokter();
            List<Kunjungan> list = controller.getByDokter(dokterList.get(0).getIdDokter());
            assertNotNull(list);
            System.out.println("✅ Found " + list.size() + " kunjungan for dokter");
        } catch (Exception e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testUpdateStatus() {
        System.out.println("TC-29: Update Kunjungan Status (Controller)");
        try {
            boolean result = controller.updateStatus(testKunjunganId, StatusKunjungan.SELESAI);
            assertTrue(result);

            Kunjungan updated = controller.getById(testKunjunganId);
            assertEquals(StatusKunjungan.SELESAI, updated.getStatus());
            System.out.println("✅ Status updated to SELESAI");
        } catch (Exception e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    void testGetKunjunganMenunggu() {
        System.out.println("TC-30: Get Kunjungan Menunggu (Controller)");
        try {
            List<Kunjungan> list = controller.getKunjunganMenunggu();
            assertNotNull(list);
            for (Kunjungan k : list) {
                assertEquals(StatusKunjungan.MENUNGGU, k.getStatus());
            }
            System.out.println("✅ Found " + list.size() + " kunjungan menunggu");
        } catch (Exception e) {
            fail("Error: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() throws Exception {
        controller.hapus(testKunjunganId);
        pasienController.hapus(testPasienId);
        System.out.println("=== SELESAI TEST KUNJUNGAN CONTROLLER ===");
    }
}