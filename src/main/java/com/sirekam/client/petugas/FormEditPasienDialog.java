package com.sirekam.client.petugas;

import com.sirekam.model.Pasien;
import com.sirekam.model.enums.JenisKelamin;
import com.sirekam.controller.PasienController;
import com.sirekam.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FormEditPasienDialog extends JDialog {

    private Pasien pasien;
    private PasienController pasienController;

    private JTextField rmField;
    private JTextField namaField;
    private JTextField tglLahirField;
    private JComboBox<JenisKelamin> jkCombo;
    private JComboBox<String> asuransiCombo;
    private JTextArea alamatArea;
    private JTextField noHpField;

    private JButton saveBtn;
    private JButton cancelBtn;
    private boolean updated;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FormEditPasienDialog(Frame parent, Pasien pasien) {
        super(parent, "Edit Data Pasien", true);
        this.pasien = pasien;
        this.pasienController = new PasienController();

        setSize(450, 520);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        loadData();
        SwingUtils.centerWindow(this);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // ===== HEADER =====
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel headerLabel = new JLabel("Edit Data Pasien");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // ===== FORM =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // No RM (read-only)
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("No RM:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        rmField = new JTextField(20);
        rmField.setEditable(false);
        rmField.setBackground(new Color(240, 240, 240));
        formPanel.add(rmField, gbc);

        // Nama
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Nama Lengkap:*"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        namaField = new JTextField(20);
        formPanel.add(namaField, gbc);

        // Tanggal Lahir
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Tanggal Lahir:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        tglLahirField = new JTextField("dd/MM/yyyy", 20);
        tglLahirField.setToolTipText("Format: dd/MM/yyyy");
        formPanel.add(tglLahirField, gbc);

        // Jenis Kelamin
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Jenis Kelamin:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        jkCombo = new JComboBox<>(JenisKelamin.values());
        formPanel.add(jkCombo, gbc);

        // Jenis Asuransi
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Jenis Asuransi:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        asuransiCombo = new JComboBox<>(new String[]{"REGULER", "BPJS", "ASURANSI"});
        formPanel.add(asuransiCombo, gbc);

        // Alamat
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        alamatArea = new JTextArea(3, 20);
        alamatArea.setLineWrap(true);
        alamatArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane scroll = new JScrollPane(alamatArea);
        formPanel.add(scroll, gbc);

        // No HP
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("No HP:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        noHpField = new JTextField(20);
        formPanel.add(noHpField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ===== BUTTONS =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        saveBtn = new JButton("💾 Simpan Perubahan");
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> savePasien());

        cancelBtn = new JButton("Batal");
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(saveBtn);
    }

    private void loadData() {
        if (pasien == null) {
            return;
        }

        rmField.setText(pasien.getIdPasien());
        namaField.setText(pasien.getNama());

        if (pasien.getTanggalLahir() != null) {
            tglLahirField.setText(pasien.getTanggalLahir().format(DATE_FORMATTER));
        } else {
            tglLahirField.setText("");
        }

        jkCombo.setSelectedItem(pasien.getJenisKelamin());

        String asuransi = pasien.getJenisAsuransi();
        if (asuransi != null) {
            asuransiCombo.setSelectedItem(asuransi);
        } else {
            asuransiCombo.setSelectedIndex(0);
        }

        alamatArea.setText(pasien.getAlamat());
        noHpField.setText(pasien.getNoHp());
    }

    private void savePasien() {
        String nama = namaField.getText().trim();
        if (nama.isEmpty()) {
            SwingUtils.showError(this, "Nama pasien harus diisi!");
            namaField.requestFocus();
            return;
        }

        LocalDate tanggalLahir = null;
        String tglStr = tglLahirField.getText().trim();
        if (!tglStr.isEmpty() && !tglStr.equals("dd/MM/yyyy")) {
            try {
                tanggalLahir = LocalDate.parse(tglStr, DATE_FORMATTER);
            } catch (Exception e) {
                SwingUtils.showError(this, "Format tanggal salah! Gunakan dd/MM/yyyy");
                tglLahirField.requestFocus();
                return;
            }
        }

        JenisKelamin jk = (JenisKelamin) jkCombo.getSelectedItem();
        String asuransi = (String) asuransiCombo.getSelectedItem();
        String alamat = alamatArea.getText().trim();
        String noHp = noHpField.getText().trim();

        try {
            // Update data pasien
            pasien.setNama(nama);
            pasien.setTanggalLahir(tanggalLahir);
            pasien.setJenisKelamin(jk);
            pasien.setJenisAsuransi(asuransi);
            pasien.setAlamat(alamat);
            pasien.setNoHp(noHp);

            boolean success = pasienController.update(pasien);
            if (success) {
                updated = true;
                SwingUtils.showSuccess(this, "✅ Data pasien berhasil diperbarui!");
                dispose();
            } else {
                SwingUtils.showError(this, "Gagal memperbarui data pasien!");
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isUpdated() {
        return updated;
    }
}