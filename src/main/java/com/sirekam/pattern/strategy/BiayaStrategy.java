package com.sirekam.pattern.strategy;

import com.sirekam.model.Pasien;
import com.sirekam.model.ResepDetail;
import java.math.BigDecimal;
import java.util.List;

public interface BiayaStrategy {
    BigDecimal hitungTotal(BigDecimal biayaKonsultasi, List<ResepDetail> detailObat, Pasien pasien);
    String getNamaStrategy();
}