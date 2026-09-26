package com.sirekam.client.petugas;

import com.sirekam.model.*;
import com.sirekam.model.enums.JenisChat;
import com.sirekam.model.enums.JenisKelamin;
import com.sirekam.model.enums.StatusKunjungan;
import com.sirekam.controller.PasienController;
import com.sirekam.controller.KunjunganController;
import com.sirekam.controller.DokterController;
import com.sirekam.util.SwingUtils;
import com.sirekam.util.Theme;
import com.sirekam.pattern.iterator.PasienIterator;
import com.sirekam.chat.client.ChatClientGUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDateTime;

public class PetugasDashboardUI extends JPanel {

    private User currentUser;
    private PasienController pasienController;
    private KunjunganController kunjunganController;
    private DokterController dokterController;

    private JTable pasienTable;
    private DefaultTableModel pasienTableModel;
    private JTextField searchField;
    private JTextArea keluhanArea;
    private JComboBox<Dokter> dokterCombo;
    private JLabel statusLabel;
    private Pasien selectedPasien;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PetugasDashboardUI(User user) {
        this.currentUser = user;
        this.pasienController = new PasienController();
        this.kunjunganController = new KunjunganController();
        this.dokterController = new DokterController();

        initComponents();
        loadPasienData();
        loadDokterData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        setBackground(Theme.BG);

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main Content - Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        Theme.styleSplit(splitPane);
        splitPane.setResizeWeight(0.58);

        // Left Panel - Pasien List + Chat
        JPanel leftPanel = createLeftPanel();
        JTabbedPane leftTabbedPane = new JTabbedPane();
        Theme.styleTabs(leftTabbedPane);
        leftTabbedPane.addTab("Data Pasien", leftPanel);
        leftTabbedPane.addTab("Chat", createChatPanel());
        Theme.Card leftCard = new Theme.Card(null);
        leftCard.setBorder(Theme.pad(8, 8, 8, 8));
        leftCard.add(leftTabbedPane, BorderLayout.CENTER);
        splitPane.setLeftComponent(leftCard);

        // Right Panel - Form Kunjungan
        splitPane.setRightComponent(createRightPanel());

        add(splitPane, BorderLayout.CENTER);
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.58));

        // Footer
        JLabel footerLabel = new JLabel("SIREKAM", SwingConstants.CENTER);
        footerLabel.setFont(Theme.font(Font.BOLD, 11f));
        footerLabel.setForeground(Theme.MUTED);
        add(footerLabel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        return Theme.header("Dashboard Petugas",
                currentUser.getNamaLengkap() + " | " +
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                Theme.PRIMARY_DARK, Theme.PRIMARY);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(Theme.pad(14, 6, 6, 6));

        // Search
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        searchField = new JTextField();
        searchField.setFont(Theme.font(Font.PLAIN, 14f));
        searchField.addActionListener(e -> searchPasien());

        JButton searchBtn = Theme.button("Cari", Theme.PRIMARY);
        searchBtn.addActionListener(e -> searchPasien());

        JButton newBtn = Theme.button("+ Pasien Baru", Theme.SUCCESS);
        newBtn.addActionListener(e -> showFormPasienBaru());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(searchBtn);
        btnPanel.add(newBtn);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(btnPanel, BorderLayout.EAST);
        panel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"No RM", "Nama", "Tgl Lahir", "JK", "Asuransi", "No HP", "Kunjungan Terakhir"};
        pasienTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        pasienTable = new JTable(pasienTableModel);
        Theme.styleTable(pasienTable);
        Theme.columnWidths(pasienTable, 80, 140, 100, 90, 100, 120, 170);
        pasienTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectPasien();
            }
        });

        JScrollPane scrollPane = new JScrollPane(pasienTable);
        Theme.styleScroll(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel(" ");
        Theme.styleStatus(statusLabel);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRightPanel() {
        Theme.Card card = new Theme.Card("Form Kunjungan");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        gbc.insets = new Insets(4, 0, 18, 0);
        formPanel.add(Theme.banner("Pilih pasien dari daftar di sebelah kiri"), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 6, 0);
        formPanel.add(Theme.caption("Keluhan Pasien:"), gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 18, 0);
        keluhanArea = new JTextArea(5, 30);
        keluhanArea.setFont(Theme.font(Font.PLAIN, 14f));
        keluhanArea.setLineWrap(true);
        keluhanArea.setWrapStyleWord(true);
        JScrollPane kelScroll = new JScrollPane(keluhanArea);
        Theme.styleScroll(kelScroll);
        formPanel.add(kelScroll, gbc);

        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 6, 0);
        formPanel.add(Theme.caption("Assign ke Dokter:"), gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 24, 0);
        dokterCombo = new JComboBox<>();
        dokterCombo.setFont(Theme.font(Font.PLAIN, 14f));
        dokterCombo.setPreferredSize(new Dimension(200, 40));
        formPanel.add(dokterCombo, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel btnPanel = new JPanel(new GridLayout(1, 0, 10, 0));
        btnPanel.setOpaque(false);

        JButton simpanBtn = Theme.button("Simpan Kunjungan", Theme.PRIMARY);
        simpanBtn.addActionListener(e -> simpanKunjungan());

        JButton editBtn = Theme.button("Edit Pasien", Theme.WARNING);
        editBtn.addActionListener(e -> editPasien());

        JButton riwayatBtn = Theme.button("Riwayat", Theme.INFO);
        riwayatBtn.addActionListener(e -> lihatRiwayat());

        JButton resetBtn = Theme.outlineButton("Reset", Theme.MUTED);
        resetBtn.addActionListener(e -> resetForm());

        btnPanel.add(simpanBtn);
        btnPanel.add(editBtn);
        btnPanel.add(riwayatBtn);
        btnPanel.add(resetBtn);
        formPanel.add(btnPanel, gbc);

        card.add(formPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createChatPanel() {
        return new ChatClientGUI(
                currentUser,
                JenisChat.PETUGAS_DOKTER,
                2,
                "Dokter"
        );
    }

    // ============================================================
    // METHOD LIHAT RIWAYAT
    // ============================================================
    private void lihatRiwayat() {
        if (selectedPasien == null) {
            SwingUtils.showError(this, "Silakan pilih pasien terlebih dahulu!");
            return;
        }

        // Cek apakah pasien memiliki riwayat kunjungan
        try {
            List<Kunjungan> list = kunjunganController.getByPasien(selectedPasien.getIdPasien());
            if (list == null || list.isEmpty()) {
                int response = JOptionPane.showConfirmDialog(
                        this,
                        "Pasien " + selectedPasien.getNama() + " belum memiliki riwayat kunjungan.\nApakah ingin membuat kunjungan baru?",
                        "Tidak Ada Riwayat",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (response == JOptionPane.YES_OPTION) {
                    // Fokus ke form kunjungan
                    keluhanArea.requestFocus();
                }
                return;
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error cek riwayat: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Buka dialog riwayat
        RiwayatKunjunganDialog dialog = new RiwayatKunjunganDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                selectedPasien
        );
        dialog.setVisible(true);
    }

    private void loadPasienData() {
        try {
            List<Pasien> list = pasienController.cariSemua();
            updateTable(list);
            statusLabel.setText("Total pasien: " + list.size());
        } catch (Exception e) {
            SwingUtils.showError(this, "Error load data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDokterData() {
        try {
            List<Dokter> list = dokterController.cariSemua();
            dokterCombo.removeAllItems();
            for (Dokter d : list) {
                dokterCombo.addItem(d);
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error load dokter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void searchPasien() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadPasienData();
            return;
        }

        try {
            List<Pasien> list = pasienController.cari(keyword);
            updateTable(list);
            statusLabel.setText("Hasil pencarian: " + list.size() + " pasien");
        } catch (Exception e) {
            SwingUtils.showError(this, "Error search: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTable(List<Pasien> list) {
        pasienTableModel.setRowCount(0);
        PasienIterator iterator = new PasienIterator(list);
        while (iterator.hasNext()) {
            Pasien p = iterator.next();

            // Ambil tanggal kunjungan terakhir
            String tglKunjunganTerakhir = "-";
            try {
                LocalDateTime tgl = kunjunganController.getTanggalKunjunganTerakhir(p.getIdPasien());
                if (tgl != null) {
                    tglKunjunganTerakhir = tgl.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                }
            } catch (Exception e) {
                // Abaikan error, tampilkan "-"
            }

            pasienTableModel.addRow(new Object[]{
                    p.getIdPasien(),
                    p.getNama(),
                    p.getTanggalLahir() != null ? p.getTanggalLahir().format(DATE_FORMATTER) : "-",
                    p.getJenisKelamin() != null ? p.getJenisKelamin().getDisplayName() : "-",
                    p.getJenisAsuransi() != null ? p.getJenisAsuransi() : "REGULER",
                    p.getNoHp() != null ? p.getNoHp() : "-",
                    tglKunjunganTerakhir
            });
        }
    }

    private void selectPasien() {
        int row = pasienTable.getSelectedRow();
        if (row < 0) {
            selectedPasien = null;
            return;
        }

        String idPasien = (String) pasienTableModel.getValueAt(row, 0);
        try {
            selectedPasien = pasienController.cariById(idPasien);
        } catch (Exception e) {
            SwingUtils.showError(this, "Error load pasien: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showFormPasienBaru() {
        FormPasienDialog dialog = new FormPasienDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadPasienData();
            SwingUtils.showSuccess(this, "Pasien baru berhasil ditambahkan!");
        }
    }

    private void simpanKunjungan() {
        if (selectedPasien == null) {
            SwingUtils.showError(this, "Silakan pilih pasien terlebih dahulu!");
            return;
        }

        String keluhan = keluhanArea.getText().trim();
        if (keluhan.isEmpty()) {
            SwingUtils.showError(this, "Keluhan pasien harus diisi!");
            return;
        }

        Dokter selectedDokter = (Dokter) dokterCombo.getSelectedItem();
        if (selectedDokter == null) {
            SwingUtils.showError(this, "Silakan pilih dokter!");
            return;
        }

        try {
            Kunjungan kunjungan = new Kunjungan();
            kunjungan.setIdPasien(selectedPasien.getIdPasien());
            kunjungan.setIdDokter(selectedDokter.getIdDokter());
            kunjungan.setKeluhan(keluhan);
            kunjungan.setStatus(StatusKunjungan.MENUNGGU);

            int id = kunjunganController.simpanDanGetId(kunjungan);
            if (id > 0) {
                SwingUtils.showSuccess(this, "✅ Kunjungan berhasil disimpan!\n" +
                        "Pasien: " + selectedPasien.getNama() + "\n" +
                        "Dokter: " + selectedDokter.getNamaDokter());
                resetForm();
                loadPasienData();
            } else {
                SwingUtils.showError(this, "Gagal menyimpan kunjungan!");
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void resetForm() {
        keluhanArea.setText("");
        dokterCombo.setSelectedIndex(0);
        pasienTable.clearSelection();
        selectedPasien = null;
        searchField.setText("");
        loadPasienData();
    }

    // ============================================================
// EDIT DATA PASIEN
// ============================================================
    private void editPasien() {
        if (selectedPasien == null) {
            SwingUtils.showError(this, "Silakan pilih pasien terlebih dahulu!");
            return;
        }

        FormEditPasienDialog dialog = new FormEditPasienDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                selectedPasien
        );
        dialog.setVisible(true);

        if (dialog.isUpdated()) {
            // Refresh data pasien
            loadPasienData();
            // Refresh selected pasien
            try {
                selectedPasien = pasienController.cariById(selectedPasien.getIdPasien());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}