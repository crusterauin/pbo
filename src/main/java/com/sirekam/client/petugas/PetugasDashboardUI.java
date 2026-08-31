package com.sirekam.client.petugas;

import com.sirekam.model.*;
import com.sirekam.model.enums.JenisChat;
import com.sirekam.model.enums.JenisKelamin;
import com.sirekam.model.enums.StatusKunjungan;
import com.sirekam.controller.PasienController;
import com.sirekam.controller.KunjunganController;
import com.sirekam.controller.DokterController;
import com.sirekam.util.SwingUtils;
import com.sirekam.pattern.iterator.PasienIterator;
import com.sirekam.chat.client.ChatClientGUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 244, 248));

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content - Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.5);

        // Left Panel - Pasien List + Chat
        JPanel leftPanel = createLeftPanel();
        JTabbedPane leftTabbedPane = new JTabbedPane();
        leftTabbedPane.addTab("📋 Data Pasien", leftPanel);
        leftTabbedPane.addTab("💬 Chat", createChatPanel());
        splitPane.setLeftComponent(leftTabbedPane);

        // Right Panel - Form Kunjungan
        JPanel rightPanel = createRightPanel();
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(240, 244, 248));
        JLabel footerLabel = new JLabel("🔹 Design Pattern: Singleton, Strategy, Iterator | Generic Type");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        footerLabel.setForeground(Color.GRAY);
        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("🧑‍⚕️ Dashboard Petugas");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JLabel userInfo = new JLabel("👤 " + currentUser.getNamaLengkap() + " | " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        userInfo.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);

        return header;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("🔍 Data Pasien"));
        panel.setBackground(Color.WHITE);

        // Search
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        searchField = new JTextField();
        searchField.setToolTipText("Cari berdasarkan nama atau No RM");
        searchField.addActionListener(e -> searchPasien());

        JButton searchBtn = new JButton("🔍 Cari");
        searchBtn.setBackground(new Color(52, 152, 219));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(e -> searchPasien());

        JButton newBtn = new JButton("➕ Pasien Baru");
        newBtn.setBackground(new Color(46, 204, 113));
        newBtn.setForeground(Color.WHITE);
        newBtn.addActionListener(e -> showFormPasienBaru());

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(newBtn);
        searchPanel.add(btnPanel, BorderLayout.SOUTH);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"No RM", "Nama", "Tgl Lahir", "JK", "Asuransi", "No HP"};
        pasienTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        pasienTable = new JTable(pasienTableModel);
        pasienTable.setRowHeight(30);
        pasienTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectPasien();
            }
        });

        JScrollPane scrollPane = new JScrollPane(pasienTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("📝 Form Kunjungan"));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(400, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel pasienInfoLabel = new JLabel("Pilih pasien dari daftar di sebelah kiri");
        pasienInfoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        pasienInfoLabel.setForeground(Color.GRAY);
        formPanel.add(pasienInfoLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel("Keluhan Pasien:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        keluhanArea = new JTextArea(5, 30);
        keluhanArea.setLineWrap(true);
        keluhanArea.setWrapStyleWord(true);
        keluhanArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane kelScroll = new JScrollPane(keluhanArea);
        formPanel.add(kelScroll, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Assign ke Dokter:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        dokterCombo = new JComboBox<>();
        dokterCombo.setPreferredSize(new Dimension(200, 30));
        formPanel.add(dokterCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton simpanBtn = new JButton("💾 Simpan Kunjungan");
        simpanBtn.setBackground(new Color(41, 128, 185));
        simpanBtn.setForeground(Color.WHITE);
        simpanBtn.addActionListener(e -> simpanKunjungan());
        simpanBtn.setFont(new Font("Arial", Font.BOLD, 12));

        JButton resetBtn = new JButton("↺ Reset");
        resetBtn.addActionListener(e -> resetForm());

        btnPanel.add(simpanBtn);
        btnPanel.add(resetBtn);
        formPanel.add(btnPanel, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    // ============================================================
    // CHAT PANEL UNTUK PETUGAS (Kirim ke Dokter Siti id=2)
    // ============================================================
    private JPanel createChatPanel() {
        return new ChatClientGUI(
                currentUser,
                JenisChat.PETUGAS_DOKTER,
                2,  // Receiver ID = Dokter Siti (id=2)
                "Dokter"
        );
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
            pasienTableModel.addRow(new Object[]{
                    p.getIdPasien(),
                    p.getNama(),
                    p.getTanggalLahir() != null ? p.getTanggalLahir().format(DATE_FORMATTER) : "-",
                    p.getJenisKelamin() != null ? p.getJenisKelamin().getDisplayName() : "-",
                    p.getJenisAsuransi() != null ? p.getJenisAsuransi() : "REGULER",
                    p.getNoHp() != null ? p.getNoHp() : "-"
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
}