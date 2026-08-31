package com.sirekam.pattern.strategy;

import com.sirekam.model.Pasien;
import com.sirekam.model.ResepDetail;
import java.math.BigDecimal;
import java.util.List;

public class RegulerBiayaStrategy implements BiayaStrategy {

    @Override
    public BigDecimal hitungTotal(BigDecimal biayaKonsultasi, List<ResepDetail> detailObat, Pasien pasien) {
        BigDecimal totalObat = detailObat.stream()
                .map(ResepDetail::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return biayaKonsultasi.add(totalObat);
    }

    @Override
    public String getNamaStrategy() {
        return "Reguler (Tanpa Diskon)";
    }
}