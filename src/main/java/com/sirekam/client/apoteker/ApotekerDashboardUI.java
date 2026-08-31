package com.sirekam.client.apoteker;

import com.sirekam.model.*;
import com.sirekam.model.enums.JenisChat;
import com.sirekam.model.enums.StatusResep;
import com.sirekam.controller.ResepController;
import com.sirekam.controller.ObatController;
import com.sirekam.controller.StrukController;
import com.sirekam.controller.KunjunganController;
import com.sirekam.controller.PasienController;
import com.sirekam.controller.DokterController;
import com.sirekam.util.SwingUtils;
import com.sirekam.pattern.iterator.ResepIterator;
import com.sirekam.pattern.strategy.RegulerBiayaStrategy;
import com.sirekam.pattern.strategy.BPJSBiayaStrategy;
import com.sirekam.pattern.strategy.AsuransiBiayaStrategy;
import com.sirekam.chat.client.ChatClientGUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ApotekerDashboardUI extends JPanel {

    private User currentUser;
    private ResepController resepController;
    private ObatController obatController;
    private StrukController strukController;
    private KunjunganController kunjunganController;
    private PasienController pasienController;
    private DokterController dokterController;

    private JTable resepTable;
    private DefaultTableModel resepTableModel;
    private JLabel statusLabel;
    private Resep selectedResep;
    private JTextArea detailResepArea;
    private JComboBox<Obat> obatCombo;
    private JTextField jumlahField;
    private JLabel totalLabel;
    private JComboBox<String> strategiCombo;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ApotekerDashboardUI(User user) {
        this.currentUser = user;
        this.resepController = new ResepController();
        this.obatController = new ObatController();
        this.strukController = new StrukController();
        this.kunjunganController = new KunjunganController();
        this.pasienController = new PasienController();
        this.dokterController = new DokterController();

        initComponents();
        loadData();
        loadObat();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 244, 248));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(155, 89, 182));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel title = new JLabel("💊 Dashboard Apoteker");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        JLabel userInfo = new JLabel("👤 " + currentUser.getNamaLengkap() + " | " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        userInfo.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(userInfo, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Main Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);

        // Left Panel - Resep Masuk + Chat
        JPanel leftPanel = createLeftPanel();
        JTabbedPane leftTabbedPane = new JTabbedPane();
        leftTabbedPane.addTab("📋 Resep Masuk", leftPanel);
        leftTabbedPane.addTab("💬 Chat", createChatPanel());
        splitPane.setLeftComponent(leftTabbedPane);

        // Right Panel - Proses Resep
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

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("📋 Resep Masuk"));
        leftPanel.setBackground(Color.WHITE);

        String[] columns = {"No Resep", "Pasien", "Dokter", "Status"};
        resepTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resepTable = new JTable(resepTableModel);
        resepTable.setRowHeight(30);
        resepTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectResep();
        });
        JScrollPane scrollPane = new JScrollPane(resepTable);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftPanel.add(statusLabel, BorderLayout.SOUTH);

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> {
            loadData();
            loadObat();
        });
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(refreshBtn);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("💊 Proses Resep"));
        rightPanel.setBackground(Color.WHITE);

        JPanel detailPanel = new JPanel(new GridBagLayout());
        detailPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        detailPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 5, 3, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        detailPanel.add(new JLabel("Detail Resep:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        detailResepArea = new JTextArea(5, 20);
        detailResepArea.setEditable(false);
        detailResepArea.setBackground(new Color(240, 240, 240));
        detailResepArea.setLineWrap(true);
        detailResepArea.setWrapStyleWord(true);
        JScrollPane resepScroll = new JScrollPane(detailResepArea);
        detailPanel.add(resepScroll, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        detailPanel.add(new JLabel("Pilih Obat:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        obatCombo = new JComboBox<>();
        obatCombo.setPreferredSize(new Dimension(200, 30));
        detailPanel.add(obatCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        detailPanel.add(new JLabel("Jumlah:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        jumlahField = new JTextField(10);
        detailPanel.add(jumlahField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JButton assignBtn = new JButton("💾 Assign Obat");
        assignBtn.setBackground(new Color(41, 128, 185));
        assignBtn.setForeground(Color.WHITE);
        assignBtn.addActionListener(e -> assignObat());
        detailPanel.add(assignBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        detailPanel.add(new JLabel("Strategi Biaya:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        strategiCombo = new JComboBox<>(new String[]{"Reguler", "BPJS (Diskon 30%)", "Asuransi (Diskon 20%)"});
        strategiCombo.addActionListener(e -> hitungTotal());
        detailPanel.add(strategiCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        detailPanel.add(new JLabel("Total Bayar:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        totalLabel = new JLabel("Rp 0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(46, 204, 113));
        detailPanel.add(totalLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        JButton cetakBtn = new JButton("🧾 Cetak Struk");
        cetakBtn.setBackground(new Color(46, 204, 113));
        cetakBtn.setForeground(Color.WHITE);
        cetakBtn.addActionListener(e -> cetakStruk());
        cetakBtn.setFont(new Font("Arial", Font.BOLD, 12));
        detailPanel.add(cetakBtn, gbc);

        rightPanel.add(detailPanel, BorderLayout.CENTER);

        return rightPanel;
    }

    private JPanel createChatPanel() {
        return new ChatClientGUI(
                currentUser,
                JenisChat.DOKTER_APOTEKER,
                2,
                "Dokter"
        );
    }

    private void loadData() {
        try {
            List<Resep> list = new ArrayList<>();
            list.addAll(resepController.getResepMenunggu());
            list.addAll(resepController.getResepDiproses());

            list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

            updateTable(list);
            if (statusLabel != null) {
                statusLabel.setText("Total resep aktif: " + list.size());
            }

            if (selectedResep != null) {
                boolean found = false;
                for (Resep r : list) {
                    if (r.getIdResep() == selectedResep.getIdResep()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    selectedResep = null;
                    detailResepArea.setText("");
                    totalLabel.setText("Rp 0");
                }
            }

        } catch (Exception e) {
            SwingUtils.showError(this, "Error load data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadObat() {
        try {
            List<Obat> list = obatController.cariSemua();
            obatCombo.removeAllItems();
            for (Obat o : list) {
                obatCombo.addItem(o);
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error load obat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTable(List<Resep> list) {
        resepTableModel.setRowCount(0);
        ResepIterator iterator = new ResepIterator(list);
        while (iterator.hasNext()) {
            Resep r = iterator.next();
            String statusDisplay = r.getStatusResep().getDisplayName();
            if (r.getStatusResep() == StatusResep.MENUNGGU) {
                statusDisplay = "🟡 " + statusDisplay;
            } else if (r.getStatusResep() == StatusResep.DIPROSES_APOTEKER) {
                statusDisplay = "🔵 " + statusDisplay;
            }
            resepTableModel.addRow(new Object[]{
                    "RES-" + String.format("%04d", r.getIdResep()),
                    r.getNamaPasien(),
                    r.getNamaDokter(),
                    statusDisplay
            });
        }
    }

    private void selectResep() {
        int row = resepTable.getSelectedRow();
        if (row < 0) {
            selectedResep = null;
            detailResepArea.setText("");
            totalLabel.setText("Rp 0");
            return;
        }

        try {
            String resepId = (String) resepTableModel.getValueAt(row, 0);
            int id = Integer.parseInt(resepId.replace("RES-", ""));
            selectedResep = resepController.getById(id);
            if (selectedResep != null) {
                detailResepArea.setText("Pasien: " + selectedResep.getNamaPasien() + "\n" +
                        "Dokter: " + selectedResep.getNamaDokter() + "\n" +
                        "Resep: " + selectedResep.getObatDanPerlakuan() + "\n" +
                        "Status: " + selectedResep.getStatusResep().getDisplayName());
                hitungTotal();
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error load detail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void selectResepById(int idResep) {
        for (int i = 0; i < resepTableModel.getRowCount(); i++) {
            String resepId = (String) resepTableModel.getValueAt(i, 0);
            int id = Integer.parseInt(resepId.replace("RES-", ""));
            if (id == idResep) {
                resepTable.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    private void hitungTotal() {
        if (selectedResep == null) {
            totalLabel.setText("Rp 0");
            return;
        }

        try {
            String selected = (String) strategiCombo.getSelectedItem();
            if (selected.equals("BPJS (Diskon 30%)")) {
                strukController.setStrategy(new BPJSBiayaStrategy());
            } else if (selected.equals("Asuransi (Diskon 20%)")) {
                strukController.setStrategy(new AsuransiBiayaStrategy());
            } else {
                strukController.setStrategy(new RegulerBiayaStrategy());
            }

            BigDecimal total = strukController.hitungTotalBayar(selectedResep.getIdResep());
            totalLabel.setText("Rp " + total);
        } catch (Exception e) {
            totalLabel.setText("Rp 0");
            e.printStackTrace();
        }
    }

    private void assignObat() {
        if (selectedResep == null) {
            SwingUtils.showError(this, "Silakan pilih resep terlebih dahulu!");
            return;
        }

        int idResep = selectedResep.getIdResep();

        Obat selectedObat = (Obat) obatCombo.getSelectedItem();
        if (selectedObat == null) {
            SwingUtils.showError(this, "Silakan pilih obat!");
            return;
        }

        String jumlahStr = jumlahField.getText().trim();
        if (jumlahStr.isEmpty()) {
            SwingUtils.showError(this, "Jumlah harus diisi!");
            return;
        }

        int jumlah;
        try {
            jumlah = Integer.parseInt(jumlahStr);
            if (jumlah <= 0) {
                SwingUtils.showError(this, "Jumlah harus lebih dari 0!");
                return;
            }
        } catch (NumberFormatException e) {
            SwingUtils.showError(this, "Jumlah harus berupa angka!");
            return;
        }

        try {
            boolean success = resepController.assignObat(idResep, selectedObat.getIdObat(), jumlah);

            if (success) {
                SwingUtils.showSuccess(this, "✅ Obat berhasil diassign!\n" +
                        "Obat: " + selectedObat.getNamaObat() + "\n" +
                        "Jumlah: " + jumlah);

                resepController.updateStatus(idResep, StatusResep.DIPROSES_APOTEKER);

                // JANGAN HAPUS JUMLAH FIELD - biarkan user bisa menambah lagi
                // jumlahField.setText("");

                loadData();
                loadObat();

                selectedResep = resepController.getById(idResep);
                hitungTotal();

                if (selectedResep != null) {
                    detailResepArea.setText("Pasien: " + selectedResep.getNamaPasien() + "\n" +
                            "Dokter: " + selectedResep.getNamaDokter() + "\n" +
                            "Resep: " + selectedResep.getObatDanPerlakuan() + "\n" +
                            "Status: " + selectedResep.getStatusResep().getDisplayName());
                }

                selectResepById(idResep);

            } else {
                SwingUtils.showError(this, "❌ Stok tidak mencukupi!\n" +
                        "Stok tersedia: " + selectedObat.getStok());
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cetakStruk() {
        if (selectedResep == null) {
            SwingUtils.showError(this, "Silakan pilih resep terlebih dahulu!");
            return;
        }

        try {
            BigDecimal biayaKonsultasi = new BigDecimal("100000");
            Struk struk = strukController.cetakStruk(selectedResep.getIdResep(), biayaKonsultasi);

            if (struk != null) {
                Kunjungan kunjungan = kunjunganController.getById(selectedResep.getIdKunjungan());
                if (kunjungan != null) {
                    Pasien pasien = pasienController.cariById(kunjungan.getIdPasien());
                    if (pasien != null) {
                        struk.setNamaPasien(pasien.getNama());
                        struk.setNoRekamMedis(pasien.getIdPasien());
                        struk.setJenisAsuransi(pasien.getJenisAsuransi());
                    }
                    Dokter dokter = dokterController.findById(kunjungan.getIdDokter());
                    if (dokter != null) {
                        struk.setNamaDokter(dokter.getNamaDokter());
                    }
                }

                String strukText = strukController.generateStrukText(struk);
                JTextArea textArea = new JTextArea(strukText);
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(500, 500));

                int option = JOptionPane.showConfirmDialog(
                        this,
                        scrollPane,
                        "🧾 Preview Struk Pembayaran",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

                if (option == JOptionPane.OK_OPTION) {
                    SwingUtils.showSuccess(this, "✅ Struk berhasil dicetak!\n" +
                            "Total Bayar: Rp " + struk.getTotalBayar());
                    loadData();
                    loadObat();
                    selectedResep = null;
                    detailResepArea.setText("");
                    totalLabel.setText("Rp 0");
                    resepTable.clearSelection();
                    jumlahField.setText("");
                }
            } else {
                SwingUtils.showError(this, "Gagal mencetak struk!");
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}