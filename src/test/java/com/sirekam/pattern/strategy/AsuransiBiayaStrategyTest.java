package com.sirekam.pattern.strategy;

import com.sirekam.model.ResepDetail;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AsuransiBiayaStrategyTest {

    private BiayaStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new AsuransiBiayaStrategy();
        System.out.println("=== TEST ASURANSI STRATEGY ===");
    }

    @Test
    void testHitungTotalDiskon20Persen() {
        System.out.println("TC-42: Asuransi Strategy - Diskon 20% Total");
        BigDecimal biayaKonsultasi = new BigDecimal("100000");
        List<ResepDetail> detailObat = new ArrayList<>();

        // Diskon 20% dari 100.000 = 20.000, total = 80.000
        BigDecimal expected = new BigDecimal("80000");

        BigDecimal total = strategy.hitungTotal(biayaKonsultasi, detailObat, null);
        assertEquals(0, expected.compareTo(total),
                "Expected: " + expected + " but was: " + total);
        System.out.println("✅ Total after 20% discount: " + total);
    }

    @Test
    void testHitungTotalDenganObat() {
        System.out.println("TC-43: Asuransi Strategy - Dengan Obat");
        BigDecimal biayaKonsultasi = new BigDecimal("100000");

        List<ResepDetail> detailObat = new ArrayList<>();
        ResepDetail rd = new ResepDetail();
        rd.setHargaSatuan(new BigDecimal("50000"));
        rd.setJumlah(2);
        detailObat.add(rd);

        // Total: 100.000 + 100.000 = 200.000, diskon 20% = 40.000, total = 160.000
        BigDecimal expected = new BigDecimal("160000");

        BigDecimal total = strategy.hitungTotal(biayaKonsultasi, detailObat, null);
        assertEquals(0, expected.compareTo(total));
        System.out.println("✅ Total with obat: " + total);
    }

    @Test
    void testGetNamaStrategy() {
        System.out.println("TC-44: Asuransi Strategy - Get Name");
        assertEquals("Asuransi (Diskon 20% Total)", strategy.getNamaStrategy());
        System.out.println("✅ Nama strategy: " + strategy.getNamaStrategy());
    }

    @AfterEach
    void tearDown() {
        System.out.println("=== SELESAI TEST ASURANSI STRATEGY ===");
    }
}