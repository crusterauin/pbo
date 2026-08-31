package com.sirekam.pattern.iterator;

import com.sirekam.model.Pasien;
import com.sirekam.model.enums.JenisKelamin;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class PasienIteratorTest {

    private List<Pasien> pasienList;
    private PasienIterator iterator;

    @BeforeEach
    void setUp() {
        System.out.println("=== TEST PASIEN ITERATOR ===");
        pasienList = new ArrayList<>();

        Pasien p1 = new Pasien();
        p1.setIdPasien("RM-0001");
        p1.setNama("Andi");
        pasienList.add(p1);

        Pasien p2 = new Pasien();
        p2.setIdPasien("RM-0002");
        p2.setNama("Budi");
        pasienList.add(p2);

        Pasien p3 = new Pasien();
        p3.setIdPasien("RM-0003");
        p3.setNama("Cici");
        pasienList.add(p3);

        iterator = new PasienIterator(pasienList);
    }

    @Test
    void testHasNext() {
        System.out.println("TC-45: PasienIterator - hasNext");
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
        System.out.println("✅ hasNext works correctly");
    }

    @Test
    void testNext() {
        System.out.println("TC-46: PasienIterator - next");
        assertEquals("Andi", iterator.next().getNama());
        assertEquals("Budi", iterator.next().getNama());
        assertEquals("Cici", iterator.next().getNama());
        System.out.println("✅ next works correctly");
    }

    @Test
    void testNextThrowsException() {
        System.out.println("TC-47: PasienIterator - next throws exception");
        iterator.next();
        iterator.next();
        iterator.next();
        assertThrows(NoSuchElementException.class, () -> iterator.next());
        System.out.println("✅ Exception thrown correctly");
    }

    @Test
    void testIterateAllElements() {
        System.out.println("TC-48: PasienIterator - iterate all elements");
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        assertEquals(3, count);
        System.out.println("✅ Iterated " + count + " elements");
    }

    @AfterEach
    void tearDown() {
        System.out.println("=== SELESAI TEST PASIEN ITERATOR ===");
    }
}