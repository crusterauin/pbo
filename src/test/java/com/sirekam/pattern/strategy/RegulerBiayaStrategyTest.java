package com.sirekam.pattern.strategy;

import com.sirekam.model.ResepDetail;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RegulerBiayaStrategyTest {

    private BiayaStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new RegulerBiayaStrategy();
        System.out.println("=== TEST REGULER STRATEGY ===");
    }

    @Test
    void testHitungTotalTanpaObat() {
        System.out.println("TC-36: Reguler Strategy - Tanpa Obat");
        BigDecimal biayaKonsultasi = new BigDecimal("100000");
        List<ResepDetail> detailObat = new ArrayList<>();

        BigDecimal total = strategy.hitungTotal(biayaKonsultasi, detailObat, null);
        assertEquals(new BigDecimal("100000"), total);
        System.out.println("✅ Total: " + total);
    }

    @Test
    void testHitungTotalDenganObat() {
        System.out.println("TC-37: Reguler Strategy - Dengan Obat");
        BigDecimal biayaKonsultasi = new BigDecimal("100000");

        List<ResepDetail> detailObat = new ArrayList<>();
        ResepDetail rd1 = new ResepDetail();
        rd1.setHargaSatuan(new BigDecimal("10000"));
        rd1.setJumlah(2);
        detailObat.add(rd1);

        ResepDetail rd2 = new ResepDetail();
        rd2.setHargaSatuan(new BigDecimal("15000"));
        rd2.setJumlah(1);
        detailObat.add(rd2);

        BigDecimal expected = new BigDecimal("100000")
                .add(new BigDecimal("10000").multiply(new BigDecimal("2")))
                .add(new BigDecimal("15000"));

        BigDecimal total = strategy.hitungTotal(biayaKonsultasi, detailObat, null);
        assertEquals(0, expected.compareTo(total));
        System.out.println("✅ Total: " + total);
    }

    @Test
    void testGetNamaStrategy() {
        System.out.println("TC-38: Reguler Strategy - Get Name");
        assertEquals("Reguler (Tanpa Diskon)", strategy.getNamaStrategy());
        System.out.println("✅ Nama strategy: " + strategy.getNamaStrategy());
    }

    @AfterEach
    void tearDown() {
        System.out.println("=== SELESAI TEST REGULER STRATEGY ===");
    }
}