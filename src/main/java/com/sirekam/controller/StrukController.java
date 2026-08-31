package com.sirekam.controller;

import com.sirekam.model.*;
import com.sirekam.dao.StrukDAO;
import com.sirekam.dao.ResepDAO;
import com.sirekam.dao.ResepDetailDAO;
import com.sirekam.dao.KunjunganDAO;
import com.sirekam.pattern.strategy.BiayaStrategy;
import com.sirekam.pattern.strategy.RegulerBiayaStrategy;
import com.sirekam.model.enums.StatusKunjungan;
import com.sirekam.model.enums.StatusResep;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import com.sirekam.dao.ResepDetailDAO;
import com.sirekam.model.ResepDetail;

public class StrukController extends GenericController<Struk> {

    private StrukDAO strukDAO;
    private ResepDAO resepDAO;
    private ResepDetailDAO resepDetailDAO;
    private KunjunganDAO kunjunganDAO;
    private BiayaStrategy strategy;

    public StrukController() {
        super(new StrukDAO());
        this.strukDAO = (StrukDAO) dao;
        this.resepDAO = new ResepDAO();
        this.resepDetailDAO = new ResepDetailDAO();
        this.kunjunganDAO = new KunjunganDAO();
        this.strategy = new RegulerBiayaStrategy();
    }

    public void setStrategy(BiayaStrategy strategy) {
        this.strategy = strategy;
        System.out.println("📊 Strategy changed to: " + strategy.getNamaStrategy());
    }

    public BiayaStrategy getStrategy() {
        return strategy;
    }

    public BigDecimal hitungTotalBayar(int idResep) throws SQLException {
        Resep resep = resepDAO.findById(idResep);
        if (resep == null) {
            throw new SQLException("Resep tidak ditemukan");
        }
        Kunjungan kunjungan = kunjunganDAO.findById(resep.getIdKunjungan());
        if (kunjungan == null) {
            throw new SQLException("Kunjungan tidak ditemukan");
        }
        BigDecimal biayaKonsultasi = new BigDecimal("100000");
        List<ResepDetail> detailObat = resepDetailDAO.findByResep(idResep);
        return strategy.hitungTotal(biayaKonsultasi, detailObat, null);
    }

    public Struk cetakStruk(int idResep, BigDecimal biayaKonsultasi) throws SQLException {
        System.out.println("🔍 [DEBUG] cetakStruk called for resep: " + idResep);

        // 1. Cari resep
        Resep resep = resepDAO.findById(idResep);
        if (resep == null) {
            System.err.println("❌ Resep not found: " + idResep);
            return null;
        }

        // 2. Cari detail obat
        List<ResepDetail> detailObat = resepDetailDAO.findByResep(idResep);

        // 3. Cari kunjungan
        Kunjungan kunjungan = kunjunganDAO.findById(resep.getIdKunjungan());

        // 4. Hitung total obat
        BigDecimal totalObat = detailObat.stream()
                .map(ResepDetail::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. Hitung total bayar dengan strategy
        BigDecimal totalBayar = strategy.hitungTotal(biayaKonsultasi, detailObat, null);

        // 6. Buat objek Struk
        Struk struk = new Struk(idResep, biayaKonsultasi, totalObat);
        struk.setTotalBayar(totalBayar);

        System.out.println("🔍 [DEBUG] Saving struk...");

        // 7. SIMPAN DAN AMBIL ID-NYA
        // Gunakan saveAndGetId, bukan save()
        int generatedId = strukDAO.saveAndGetId(struk);
        System.out.println("🔍 [DEBUG] Struk saved with ID: " + generatedId);

        if (generatedId > 0) {
            // Set ID ke objek struk
            struk.setIdStruk(generatedId);

            // 8. Update status resep
            System.out.println("🔍 [DEBUG] Updating resep status...");
            boolean resepUpdated = resepDAO.updateStatus(idResep, StatusResep.SELESAI);
            System.out.println("🔍 [DEBUG] Resep updated: " + resepUpdated);

            // 9. Update status kunjungan
            if (kunjungan != null) {
                System.out.println("🔍 [DEBUG] Updating kunjungan status...");
                boolean kunjunganUpdated = kunjunganDAO.updateStatus(
                        kunjungan.getIdKunjungan(),
                        StatusKunjungan.SELESAI
                );
                System.out.println("🔍 [DEBUG] Kunjungan updated: " + kunjunganUpdated);
            }
        } else {
            System.err.println("❌ Failed to save struk!");
            return null;
        }

        // 10. KEMBALIKAN OBJEK STRUK DENGAN ID YANG SUDAH TERISI
        return struk;
    }

    public String generateStrukText(Struk struk) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("      KLINIK SEHAT SEJAHTERA\n");
        sb.append("========================================\n");
        sb.append("STRUK PEMBAYARAN\n");
        sb.append("No. Struk : STR-").append(String.format("%04d", struk.getIdStruk())).append("\n");
        sb.append("Tanggal  : ").append(struk.getWaktuCetak()).append("\n");

        // Informasi Pasien
        if (struk.getNamaPasien() != null) {
            sb.append("Pasien   : ").append(struk.getNamaPasien()).append("\n");
        }
        if (struk.getNoRekamMedis() != null) {
            sb.append("No. RM   : ").append(struk.getNoRekamMedis()).append("\n");
        }
        if (struk.getNamaDokter() != null) {
            sb.append("Dokter   : ").append(struk.getNamaDokter()).append("\n");
        }
        if (struk.getJenisAsuransi() != null) {
            sb.append("Asuransi : ").append(struk.getJenisAsuransi()).append("\n");
        }

        sb.append("----------------------------------------\n");

        // ============================================================
        // RINCIAN OBAT
        // ============================================================
        sb.append("RINCIAN OBAT:\n");

        ResepDetailDAO resepDetailDAO = new ResepDetailDAO();
        List<ResepDetail> detailObat = resepDetailDAO.findByResep(struk.getIdResep());

        if (detailObat != null && !detailObat.isEmpty()) {
            int no = 1;
            for (ResepDetail rd : detailObat) {
                String namaObat = rd.getNamaObat() != null ? rd.getNamaObat() : "Obat ID: " + rd.getIdObat();
                String satuan = rd.getSatuan() != null ? rd.getSatuan() : "";
                BigDecimal harga = rd.getHargaSatuan() != null ? rd.getHargaSatuan() : BigDecimal.ZERO;
                int jumlah = rd.getJumlah();
                BigDecimal subtotal = harga.multiply(BigDecimal.valueOf(jumlah));

                sb.append(String.format("  %d. %s\n", no, namaObat));
                sb.append(String.format("     %d %s x Rp %,.0f = Rp %,.0f\n",
                        jumlah, satuan, harga, subtotal));
                no++;
            }
        } else {
            sb.append("  (Tidak ada detail obat)\n");
        }
        // ============================================================

        sb.append("----------------------------------------\n");
        sb.append("Biaya Konsultasi : Rp ").append(struk.getBiayaKonsultasi()).append("\n");
        sb.append("Biaya Obat       : Rp ").append(struk.getBiayaObat()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("TOTAL BAYAR      : Rp ").append(struk.getTotalBayar()).append("\n");
        sb.append("========================================\n");
        sb.append("Terima kasih telah berkunjung!\n");
        sb.append("========================================\n");
        return sb.toString();
    }
}