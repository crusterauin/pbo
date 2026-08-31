package com.sirekam.dao;

import com.sirekam.model.Obat;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ObatDAOTest {

    private static ObatDAO obatDAO;
    private static int testObatId;

    @BeforeAll
    static void setUp() {
        obatDAO = new ObatDAO();
        System.out.println("=== MEMULAI TEST OBAT DAO ===");
    }

    @Test
    @Order(1)
    void testSaveObat() {
        System.out.println("TC-15: Save Obat");
        try {
            Obat obat = new Obat();
            obat.setNamaObat("TEST OBAT");
            obat.setSatuan("tablet");
            obat.setStok(100);
            obat.setHargaSatuan(new BigDecimal("10000"));

            boolean result = obatDAO.save(obat);
            assertTrue(result, "Save obat harus berhasil");

            // Ambil ID dari database
            List<Obat> list = obatDAO.search("TEST OBAT");
            assertFalse(list.isEmpty());
            testObatId = list.get(0).getIdObat();
            System.out.println("✅ Obat saved with ID: " + testObatId);
        } catch (SQLException e) {
            fail("Error saving obat: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    void testFindById() {
        System.out.println("TC-16: Find Obat by ID");
        try {
            Obat obat = obatDAO.findById(testObatId);
            assertNotNull(obat);
            assertEquals("TEST OBAT", obat.getNamaObat());
            System.out.println("✅ Obat found: " + obat.getNamaObat());
        } catch (SQLException e) {
            fail("Error finding obat: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testFindAll() {
        System.out.println("TC-17: Find All Obat");
        try {
            List<Obat> list = obatDAO.findAll();
            assertNotNull(list);
            assertTrue(list.size() > 0);
            System.out.println("✅ Total obat: " + list.size());
        } catch (SQLException e) {
            fail("Error finding all obat: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testCekStok() {
        System.out.println("TC-18: Cek Stok Obat");
        try {
            boolean cek = obatDAO.cekStok(testObatId, 50);
            assertTrue(cek, "Stok harus cukup untuk 50");

            boolean cek2 = obatDAO.cekStok(testObatId, 200);
            assertFalse(cek2, "Stok harus tidak cukup untuk 200");
            System.out.println("✅ Stok check passed");
        } catch (SQLException e) {
            fail("Error cek stok: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    void testUpdateStok() {
        System.out.println("TC-19: Update Stok Obat");
        try {
            Obat before = obatDAO.findById(testObatId);
            int stokBefore = before.getStok();

            boolean result = obatDAO.updateStok(testObatId, 30);
            assertTrue(result, "Update stok harus berhasil");

            Obat after = obatDAO.findById(testObatId);
            assertEquals(stokBefore - 30, after.getStok());
            System.out.println("✅ Stok updated from " + stokBefore + " to " + after.getStok());
        } catch (SQLException e) {
            fail("Error update stok: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    void testDeleteObat() {
        System.out.println("TC-20: Delete Obat");
        try {
            boolean result = obatDAO.delete(testObatId);
            assertTrue(result, "Delete harus berhasil");

            Obat deleted = obatDAO.findById(testObatId);
            assertNull(deleted, "Obat harus sudah terhapus");
            System.out.println("✅ Obat deleted: " + testObatId);
        } catch (SQLException e) {
            fail("Error deleting obat: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== SELESAI TEST OBAT DAO ===");
    }
}