package com.sirekam.pattern.strategy;

import com.sirekam.model.ResepDetail;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BPJSBiayaStrategyTest {

    private BiayaStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new BPJSBiayaStrategy();
        System.out.println("=== TEST BPJS STRATEGY ===");
    }

    @Test
    void testHitungTotalDiskon30Persen() {
        System.out.println("TC-39: BPJS Strategy - Diskon 30% Konsultasi");
        BigDecimal biayaKonsultasi = new BigDecimal("100000");
        List<ResepDetail> detailObat = new ArrayList<>();

        // Diskon 30% dari 100.000 = 30.000, total = 70.000
        BigDecimal expected = new BigDecimal("70000");

        BigDecimal total = strategy.hitungTotal(biayaKonsultasi, detailObat, null);
        assertEquals(0, expected.compareTo(total));
        System.out.println("✅ Total after 30% discount: " + total);
    }

    @Test
    void testHitungTotalDenganObat() {
        System.out.println("TC-40: BPJS Strategy - Dengan Obat");
        BigDecimal biayaKonsultasi = new BigDecimal("100000");

        List<ResepDetail> detailObat = new ArrayList<>();
        ResepDetail rd = new ResepDetail();
        rd.setHargaSatuan(new BigDecimal("50000"));
        rd.setJumlah(1);
        detailObat.add(rd);

        // Konsultasi 100.000 - 30.000 = 70.000 + Obat 50.000 = 120.000
        BigDecimal expected = new BigDecimal("120000");

        BigDecimal total = strategy.hitungTotal(biayaKonsultasi, detailObat, null);
        assertEquals(0, expected.compareTo(total));
        System.out.println("✅ Total with obat: " + total);
    }

    @Test
    void testGetNamaStrategy() {
        System.out.println("TC-41: BPJS Strategy - Get Name");
        assertEquals("BPJS (Diskon 30% Konsultasi)", strategy.getNamaStrategy());
        System.out.println("✅ Nama strategy: " + strategy.getNamaStrategy());
    }

    @AfterEach
    void tearDown() {
        System.out.println("=== SELESAI TEST BPJS STRATEGY ===");
    }
}