package com.sirekam.pattern.strategy;

import com.sirekam.model.Pasien;
import com.sirekam.model.ResepDetail;
import java.math.BigDecimal;
import java.util.List;

public class AsuransiBiayaStrategy implements BiayaStrategy {

    private static final BigDecimal DISKON_TOTAL = new BigDecimal("0.20");

    @Override
    public BigDecimal hitungTotal(BigDecimal biayaKonsultasi, List<ResepDetail> detailObat, Pasien pasien) {
        BigDecimal totalObat = detailObat.stream()
                .map(ResepDetail::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = biayaKonsultasi.add(totalObat);
        BigDecimal diskon = total.multiply(DISKON_TOTAL);
        return total.subtract(diskon);
    }

    @Override
    public String getNamaStrategy() {
        return "Asuransi (Diskon 20% Total)";
    }
}