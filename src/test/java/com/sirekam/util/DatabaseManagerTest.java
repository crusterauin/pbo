package com.sirekam.util;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseManagerTest {

    @Test
    @Order(1)
    void testSingletonInstance() {
        System.out.println("TC-52: Singleton Pattern Test");
        DatabaseManager instance1 = DatabaseManager.getInstance();
        DatabaseManager instance2 = DatabaseManager.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "Instance harus sama (Singleton)");
        System.out.println("✅ Singleton instance verified: instance1 == instance2");
    }

    @Test
    @Order(2)
    void testGetConnection() {
        System.out.println("TC-53: Get Database Connection");
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            Connection conn = db.getConnection();
            assertNotNull(conn);
            assertFalse(conn.isClosed());
            System.out.println("✅ Connection successful");
        } catch (SQLException e) {
            fail("Connection failed: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testExecuteQuery() {
        System.out.println("TC-54: Execute Query");
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            var rs = db.executeQuery("SELECT 1 as test");
            assertNotNull(rs);
            assertTrue(rs.next());
            assertEquals(1, rs.getInt("test"));
            System.out.println("✅ Query executed successfully");
        } catch (SQLException e) {
            fail("Query failed: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testCloseConnection() {
        System.out.println("TC-55: Close Connection");
        DatabaseManager db = DatabaseManager.getInstance();
        db.closeConnection();
        // Tidak ada assert, hanya memastikan tidak error
        System.out.println("✅ Connection closed");
    }

    @Test
    @Order(5)
    void testMultipleGetInstanceSameConnection() {
        System.out.println("TC-56: Multiple getInstance returns same connection");
        try {
            DatabaseManager db1 = DatabaseManager.getInstance();
            DatabaseManager db2 = DatabaseManager.getInstance();

            Connection conn1 = db1.getConnection();
            Connection conn2 = db2.getConnection();

            assertSame(conn1, conn2, "Koneksi harus sama karena singleton");
            System.out.println("✅ Same connection returned");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        DatabaseManager.getInstance().closeConnection();
        System.out.println("=== SELESAI TEST DATABASE MANAGER ===");
    }
}