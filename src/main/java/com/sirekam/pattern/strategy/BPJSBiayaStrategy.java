package com.sirekam.pattern.strategy;

import com.sirekam.model.Pasien;
import com.sirekam.model.ResepDetail;
import java.math.BigDecimal;
import java.util.List;

public class BPJSBiayaStrategy implements BiayaStrategy {

    private static final BigDecimal DISKON_KONSULTASI = new BigDecimal("0.30");

    @Override
    public BigDecimal hitungTotal(BigDecimal biayaKonsultasi, List<ResepDetail> detailObat, Pasien pasien) {
        BigDecimal diskon = biayaKonsultasi.multiply(DISKON_KONSULTASI);
        BigDecimal biayaKonsultasiSetelahDiskon = biayaKonsultasi.subtract(diskon);
        BigDecimal totalObat = detailObat.stream()
                .map(ResepDetail::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return biayaKonsultasiSetelahDiskon.add(totalObat);
    }

    @Override
    public String getNamaStrategy() {
        return "BPJS (Diskon 30% Konsultasi)";
    }
}