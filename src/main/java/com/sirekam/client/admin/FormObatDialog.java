package com.sirekam.client.admin;

import com.sirekam.model.Obat;
import com.sirekam.controller.ObatController;
import com.sirekam.util.SwingUtils;
import com.sirekam.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Form Admin untuk menambah obat baru ke gudang. Obat yang ditambahkan
 * di sini akan otomatis tampil di halaman Apoteker.
 */
public class FormObatDialog extends JDialog {

    private final ObatController obatController;
    private final Obat editingObat; // null jika mode tambah baru
    private JTextField namaField;
    private JTextField satuanField;
    private JTextField stokField;
    private JTextField hargaField;
    private boolean saved;

    public FormObatDialog(Frame parent, Obat editingObat) {
        super(parent, editingObat == null ? "Tambah Obat Baru" : "Edit Obat - " + editingObat.getNamaObat(), true);
        this.obatController = new ObatController();
        this.editingObat = editingObat;
        Theme.install();
        setSize(420, 440);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
        SwingUtils.centerWindow(this);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        getContentPane().setBackground(Theme.BG);
        ((JComponent) getContentPane()).setBorder(Theme.pad(16, 16, 16, 16));

        Theme.Card card = new Theme.Card(null);
        card.setBorder(Theme.pad(20, 24, 24, 24));
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        JLabel titleLabel = new JLabel(editingObat == null ? "Data Obat Baru" : "Edit Data Obat");
        titleLabel.setFont(Theme.font(Font.BOLD, 16f));
        titleLabel.setForeground(Theme.PRIMARY_DARK);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(titleLabel, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(Theme.caption("Nama Obat:"), gbc);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 12, 0);
        namaField = new JTextField();
        card.add(namaField, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(Theme.caption("Satuan (tablet/botol/dsb):"), gbc);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 12, 0);
        satuanField = new JTextField();
        card.add(satuanField, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(Theme.caption(editingObat == null ? "Stok Awal:" : "Stok:"), gbc);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 12, 0);
        stokField = new JTextField();
        if (editingObat != null) {
            stokField.setEditable(false);
            stokField.setToolTipText("Gunakan tombol Tambah/Kurangi Stok di dashboard untuk mengubah stok");
        }
        card.add(stokField, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(Theme.caption("Harga Satuan (Rp):"), gbc);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 12, 0);
        hargaField = new JTextField();
        card.add(hargaField, gbc);

        if (editingObat != null) {
            namaField.setText(editingObat.getNamaObat());
            satuanField.setText(editingObat.getSatuan());
            stokField.setText(String.valueOf(editingObat.getStok()));
            hargaField.setText(editingObat.getHargaSatuan().toPlainString());
        } else {
            stokField.setText("0");
        }

        add(card, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        btnPanel.setBackground(Theme.BG);
        btnPanel.setBorder(Theme.pad(10, 0, 0, 0));
        JButton cancelBtn = Theme.outlineButton("Batal", Theme.MUTED);
        cancelBtn.addActionListener(e -> dispose());
        JButton saveBtn = Theme.button(editingObat == null ? "Simpan" : "Update", Theme.PRIMARY);
        saveBtn.addActionListener(e -> doSave());
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        add(btnPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(saveBtn);
    }

    private void doSave() {
        String nama = namaField.getText().trim();
        String satuan = satuanField.getText().trim();
        String hargaStr = hargaField.getText().trim().replace(",", ".");

        if (nama.isEmpty() || satuan.isEmpty() || hargaStr.isEmpty()) {
            SwingUtils.showError(this, "Semua field harus diisi!");
            return;
        }

        try {
            BigDecimal harga = new BigDecimal(hargaStr);
            if (harga.compareTo(BigDecimal.ZERO) < 0) {
                SwingUtils.showError(this, "Harga tidak boleh negatif!");
                return;
            }

            if (editingObat == null) {
                int stokAwal;
                try {
                    stokAwal = Integer.parseInt(stokField.getText().trim());
                } catch (NumberFormatException nfe) {
                    SwingUtils.showError(this, "Stok awal harus berupa angka!");
                    return;
                }
                if (stokAwal < 0) {
                    SwingUtils.showError(this, "Stok tidak boleh negatif!");
                    return;
                }
                Obat obat = new Obat(0, nama, satuan, stokAwal, harga);
                boolean ok = obatController.simpan(obat);
                if (ok) {
                    SwingUtils.showSuccess(this, "✅ Obat " + nama + " berhasil ditambahkan!");
                    saved = true;
                    dispose();
                } else {
                    SwingUtils.showError(this, "Gagal menambahkan obat!");
                }
            } else {
                editingObat.setNamaObat(nama);
                editingObat.setSatuan(satuan);
                editingObat.setHargaSatuan(harga);
                boolean ok = obatController.update(editingObat);
                if (ok) {
                    SwingUtils.showSuccess(this, "✅ Obat " + nama + " berhasil diperbarui!");
                    saved = true;
                    dispose();
                } else {
                    SwingUtils.showError(this, "Gagal memperbarui obat!");
                }
            }
        } catch (NumberFormatException e) {
            SwingUtils.showError(this, "Harga harus berupa angka!");
        } catch (Exception e) {
            SwingUtils.showError(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
