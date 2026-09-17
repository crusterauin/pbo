package com.sirekam.controller;

import com.sirekam.model.User;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginControllerTest {

    private static LoginController controller;

    @BeforeAll
    static void setUp() {
        controller = new LoginController();
        System.out.println("=== MEMULAI TEST LOGIN CONTROLLER ===");
    }

    @Test
    @Order(1)
    void testLoginPetugasValid() {
        System.out.println("TC-31: Login Petugas Valid");
        try {
            User user = controller.login("petugas1", "password123");
            assertNotNull(user, "Login harus berhasil");
            assertEquals("pendaftaran", user.getRole().getValue());
            System.out.println("✅ Login Petugas successful: " + user.getNamaLengkap());
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    void testLoginDokter1Valid() {
        System.out.println("TC-32: Login Dokter Valid");
        try {
            User user1 = controller.login("dr_grace", "password123");
            assertNotNull(user1, "Login harus berhasil");
            assertEquals("dokter", user1.getRole().getValue());
            System.out.println("✅ Login Dokter1 successful: " + user1.getNamaLengkap());

            User user2 = controller.login("dr_sovia", "password123");
            assertNotNull(user2, "Login harus berhasil");
            assertEquals("dokter", user2.getRole().getValue());
            System.out.println("✅ Login Dokter2 successful: " + user2.getNamaLengkap());
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testLoginDokterValid() {
        System.out.println("TC-32: Login Dokter Valid");
        try {
            User user = controller.login("dr_grace", "password123");
            assertNotNull(user, "Login harus berhasil");
            assertEquals("dokter", user.getRole().getValue());
            System.out.println("✅ Login Dokter successful: " + user.getNamaLengkap());
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testLoginApotekerValid() {
        System.out.println("TC-33: Login Apoteker Valid");
        try {
            User user = controller.login("apoteker1", "password123");
            assertNotNull(user, "Login harus berhasil");
            assertEquals("apoteker", user.getRole().getValue());
            System.out.println("✅ Login Apoteker successful: " + user.getNamaLengkap());
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testLoginInvalid() {
        System.out.println("TC-34: Login Invalid");
        try {
            User user = controller.login("wronguser", "wrongpass");
            assertNull(user, "Login harus gagal");
            System.out.println("✅ Login invalid correctly rejected");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    void testLoginWrongPassword() {
        System.out.println("TC-35: Login Wrong Password");
        try {
            User user = controller.login("petugas1", "wrongpass");
            assertNull(user, "Login dengan password salah harus gagal");
            System.out.println("✅ Wrong password correctly rejected");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== SELESAI TEST LOGIN CONTROLLER ===");
    }
}