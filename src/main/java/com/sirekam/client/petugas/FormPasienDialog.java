package com.sirekam.client.petugas;

import com.sirekam.model.Pasien;
import com.sirekam.model.enums.JenisKelamin;
import com.sirekam.controller.PasienController;
import com.sirekam.util.SwingUtils;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FormPasienDialog extends JDialog {
    private JTextField namaField;
    private JTextField tglLahirField;
    private JComboBox<JenisKelamin> jkCombo;
    private JComboBox<String> asuransiCombo;
    private JTextArea alamatArea;
    private JTextField noHpField;
    private JButton saveBtn;
    private JButton cancelBtn;
    private boolean saved;
    private PasienController pasienController;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FormPasienDialog(Frame parent) {
        super(parent, "Tambah Pasien Baru", true);
        setSize(450, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pasienController = new PasienController();
        initComponents();
        SwingUtils.centerWindow(this);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(46, 204, 113));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel headerLabel = new JLabel("📝 Form Data Pasien Baru");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nama Lengkap:*"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        namaField = new JTextField(20);
        formPanel.add(namaField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tanggal Lahir:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        tglLahirField = new JTextField("dd/MM/yyyy", 20);
        tglLahirField.setToolTipText("Format: dd/MM/yyyy");
        formPanel.add(tglLahirField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Jenis Kelamin:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        jkCombo = new JComboBox<>(JenisKelamin.values());
        formPanel.add(jkCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Jenis Asuransi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        asuransiCombo = new JComboBox<>(new String[]{"REGULER", "BPJS", "ASURANSI"});
        formPanel.add(asuransiCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        alamatArea = new JTextArea(3, 20);
        alamatArea.setLineWrap(true);
        alamatArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane scroll = new JScrollPane(alamatArea);
        formPanel.add(scroll, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("No HP:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        noHpField = new JTextField(20);
        formPanel.add(noHpField, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        saveBtn = new JButton("💾 Simpan");
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
            Pasien pasien = new Pasien();
            pasien.setNama(nama);
            pasien.setTanggalLahir(tanggalLahir);
            pasien.setJenisKelamin(jk);
            pasien.setJenisAsuransi(asuransi);
            pasien.setAlamat(alamat);
            pasien.setNoHp(noHp);
            pasien.setIdPasien(pasienController.generateNoRM());
            if (pasienController.simpan(pasien)) {
                saved = true;
                dispose();
            } else {
                SwingUtils.showError(this, "Gagal menyimpan data pasien!");
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isSaved() { return saved; }
}