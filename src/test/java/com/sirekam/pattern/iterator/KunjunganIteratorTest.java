package com.sirekam.pattern.iterator;

import com.sirekam.model.Kunjungan;
import com.sirekam.model.enums.StatusKunjungan;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class KunjunganIteratorTest {

    private List<Kunjungan> kunjunganList;
    private KunjunganIterator iterator;

    @BeforeEach
    void setUp() {
        System.out.println("=== TEST KUNJUNGAN ITERATOR ===");
        kunjunganList = new ArrayList<>();

        Kunjungan k1 = new Kunjungan();
        k1.setIdKunjungan(1);
        k1.setKeluhan("Demam");
        k1.setStatus(StatusKunjungan.MENUNGGU);
        kunjunganList.add(k1);

        Kunjungan k2 = new Kunjungan();
        k2.setIdKunjungan(2);
        k2.setKeluhan("Batuk");
        k2.setStatus(StatusKunjungan.DIPERIKSA);
        kunjunganList.add(k2);

        Kunjungan k3 = new Kunjungan();
        k3.setIdKunjungan(3);
        k3.setKeluhan("Sakit kepala");
        k3.setStatus(StatusKunjungan.SELESAI);
        kunjunganList.add(k3);

        iterator = new KunjunganIterator(kunjunganList);
    }

    @Test
    void testHasNext() {
        System.out.println("TC-49: KunjunganIterator - hasNext");
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
        System.out.println("TC-50: KunjunganIterator - next");
        assertEquals(1, iterator.next().getIdKunjungan());
        assertEquals(2, iterator.next().getIdKunjungan());
        assertEquals(3, iterator.next().getIdKunjungan());
        System.out.println("✅ next works correctly");
    }

    @Test
    void testNextThrowsException() {
        System.out.println("TC-51: KunjunganIterator - next throws exception");
        iterator.next();
        iterator.next();
        iterator.next();
        assertThrows(NoSuchElementException.class, () -> iterator.next());
        System.out.println("✅ Exception thrown correctly");
    }

    @AfterEach
    void tearDown() {
        System.out.println("=== SELESAI TEST KUNJUNGAN ITERATOR ===");
    }
}