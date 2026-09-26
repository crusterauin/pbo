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
import com.sirekam.util.Theme;
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
import java.time.LocalDateTime;
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
    private JTable detailTable;
    private DefaultTableModel detailTableModel;
    private JLabel subTotalLabel;
    private JLabel totalKeseluruhanLabel;

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
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        setBackground(Theme.BG);

        // Header
        add(Theme.header("Dashboard Apoteker",
                currentUser.getNamaLengkap() + " | " +
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                new Color(0x146B4E), Theme.SUCCESS), BorderLayout.NORTH);

        // Main Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        Theme.styleSplit(splitPane);
        splitPane.setResizeWeight(0.45);

        // Left Panel - Resep Masuk + Chat
        JPanel leftPanel = createLeftPanel();
        JTabbedPane leftTabbedPane = new JTabbedPane();
        Theme.styleTabs(leftTabbedPane);
        leftTabbedPane.addTab("Resep Masuk", leftPanel);
        leftTabbedPane.addTab("Riwayat Transaksi", createRiwayatPanel());
        leftTabbedPane.addTab("Chat", createChatPanel());
        Theme.Card leftCard = new Theme.Card(null);
        leftCard.setBorder(Theme.pad(8, 8, 8, 8));
        leftCard.add(leftTabbedPane, BorderLayout.CENTER);
        splitPane.setLeftComponent(leftCard);

        // Right Panel - Proses Resep
        splitPane.setRightComponent(createRightPanel());

        add(splitPane, BorderLayout.CENTER);
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.45));

        // Footer
        JLabel footerLabel = new JLabel("SIREKAM", SwingConstants.CENTER);
        footerLabel.setFont(Theme.font(Font.BOLD, 11f));
        footerLabel.setForeground(Theme.MUTED);
        add(footerLabel, BorderLayout.SOUTH);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(Theme.pad(14, 6, 6, 6));

        String[] columns = {"No Resep", "Pasien", "Dokter", "Status"};
        resepTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resepTable = new JTable(resepTableModel);
        Theme.styleTable(resepTable);
        resepTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectResep();
        });
        JScrollPane scrollPane = new JScrollPane(resepTable);
        Theme.styleScroll(scrollPane);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel(" ");
        Theme.styleStatus(statusLabel);

        JButton refreshBtn = Theme.outlineButton("Refresh", Theme.SUCCESS);
        refreshBtn.addActionListener(e -> {
            loadData();
            loadObat();
        });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(statusLabel, BorderLayout.WEST);
        bottom.add(refreshBtn, BorderLayout.EAST);
        leftPanel.add(bottom, BorderLayout.SOUTH);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        Theme.Card card = new Theme.Card("Proses Resep");

        JPanel detailPanel = new JPanel(new GridBagLayout());
        detailPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // DETAIL RESEP (INFO)
        gbc.gridy = 0;
        gbc.insets = new Insets(4, 0, 6, 0);
        detailPanel.add(Theme.caption("Detail Resep:"), gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.28;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 14, 0);
        detailResepArea = new JTextArea(3, 20);
        Theme.styleReadOnly(detailResepArea);
        JScrollPane resepScroll = new JScrollPane(detailResepArea);
        Theme.styleScroll(resepScroll);
        detailPanel.add(resepScroll, gbc);

        // TABEL OBAT YANG SUDAH DIASSIGN
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 6, 0);
        detailPanel.add(Theme.caption("Obat yang sudah diassign:"), gbc);

        gbc.gridy = 3;
        gbc.weighty = 0.72;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 14, 0);
        String[] detailColumns = {"ID", "Obat", "Jumlah", "Harga", "Subtotal"};
        detailTableModel = new DefaultTableModel(detailColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        detailTable = new JTable(detailTableModel);
        Theme.styleTable(detailTable);
        Theme.columnWidths(detailTable, 50, 300, 90, 130, 150);
        detailTable.getColumnModel().getColumn(0).setMaxWidth(60);
        detailTable.getColumnModel().getColumn(2).setMaxWidth(100);
        detailTable.getColumnModel().getColumn(3).setMaxWidth(160);
        detailTable.getColumnModel().getColumn(4).setMaxWidth(180);
        JScrollPane detailScroll = new JScrollPane(detailTable);
        Theme.styleScroll(detailScroll);
        detailScroll.setPreferredSize(new Dimension(0, 140));
        detailPanel.add(detailScroll, gbc);

        // SUBTOTAL & TOTAL
        gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 16, 0);
        JPanel summary = new JPanel(new GridBagLayout());
        summary.setBackground(new Color(0xF3F8F9));
        summary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true), Theme.pad(10, 16, 10, 16)));
        GridBagConstraints sg = new GridBagConstraints();
        sg.fill = GridBagConstraints.HORIZONTAL;
        sg.insets = new Insets(3, 0, 3, 0);

        sg.gridx = 0; sg.gridy = 0; sg.weightx = 1;
        summary.add(Theme.caption("Subtotal Obat:"), sg);
        subTotalLabel = new JLabel("Rp 0", SwingConstants.RIGHT);
        subTotalLabel.setFont(Theme.font(Font.BOLD, 15f));
        subTotalLabel.setForeground(Theme.PRIMARY);
        sg.gridx = 1; sg.weightx = 0;
        summary.add(subTotalLabel, sg);

        totalKeseluruhanLabel = Theme.caption("Total Bayar:");
        sg.gridx = 0; sg.gridy = 1; sg.weightx = 1;
        summary.add(totalKeseluruhanLabel, sg);
        totalLabel = new JLabel("Rp 0", SwingConstants.RIGHT);
        totalLabel.setFont(Theme.font(Font.BOLD, 22f));
        totalLabel.setForeground(Theme.SUCCESS);
        sg.gridx = 1; sg.weightx = 0;
        summary.add(totalLabel, sg);
        detailPanel.add(summary, gbc);

        // FORM TAMBAH OBAT
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel addForm = new JPanel(new GridBagLayout());
        addForm.setOpaque(false);
        GridBagConstraints ag = new GridBagConstraints();
        ag.anchor = GridBagConstraints.WEST;
        ag.fill = GridBagConstraints.HORIZONTAL;

        ag.gridy = 0; ag.gridx = 0; ag.weightx = 1; ag.insets = new Insets(0, 0, 6, 10);
        addForm.add(Theme.caption("Pilih Obat:"), ag);
        ag.gridx = 1; ag.weightx = 0; ag.insets = new Insets(0, 0, 6, 10);
        addForm.add(Theme.caption("Jumlah:"), ag);

        obatCombo = new JComboBox<>();
        obatCombo.setFont(Theme.font(Font.PLAIN, 14f));
        obatCombo.setPreferredSize(new Dimension(200, 42));
        ag.gridy = 1; ag.gridx = 0; ag.weightx = 1; ag.insets = new Insets(0, 0, 0, 10);
        addForm.add(obatCombo, ag);

        jumlahField = new JTextField(6);
        jumlahField.setFont(Theme.font(Font.PLAIN, 14f));
        jumlahField.setPreferredSize(new Dimension(110, 42));
        ag.gridx = 1; ag.weightx = 0; ag.insets = new Insets(0, 0, 0, 10);
        addForm.add(jumlahField, ag);

        JButton assignBtn = Theme.button("Assign Obat", Theme.PRIMARY);
        assignBtn.setPreferredSize(new Dimension(140, 42));
        assignBtn.addActionListener(e -> assignObat());
        ag.gridx = 2; ag.insets = new Insets(0, 0, 0, 0);
        addForm.add(assignBtn, ag);
        detailPanel.add(addForm, gbc);

        // TOMBOL EDIT, HAPUS & CETAK
        gbc.gridy = 6;
        gbc.insets = new Insets(18, 0, 0, 0);
        JPanel btnPanel = new JPanel(new GridLayout(1, 0, 10, 0));
        btnPanel.setOpaque(false);

        JButton editBtn = Theme.button("Edit Jumlah", Theme.WARNING);
        editBtn.addActionListener(e -> editJumlah());

        JButton deleteBtn = Theme.button("Hapus", Theme.DANGER);
        deleteBtn.addActionListener(e -> hapusItem());

        JButton cetakBtn = Theme.button("Cetak Struk", Theme.SUCCESS);
        cetakBtn.addActionListener(e -> cetakStruk());

        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(cetakBtn);
        detailPanel.add(btnPanel, gbc);

        card.add(detailPanel, BorderLayout.CENTER);
        return card;
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
            detailTableModel.setRowCount(0);
            subTotalLabel.setText("Rp 0");
            return;
        }

        try {
            String resepId = (String) resepTableModel.getValueAt(row, 0);
            int id = Integer.parseInt(resepId.replace("RES-", ""));
            selectedResep = resepController.getById(id);
            if (selectedResep != null) {
                refreshDetailTable();
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
            // Ambil data pasien dari kunjungan
            Kunjungan kunjungan = kunjunganController.getById(selectedResep.getIdKunjungan());
            if (kunjungan != null) {
                Pasien pasien = pasienController.cariById(kunjungan.getIdPasien());
                if (pasien != null) {
                    // Pilih strategy berdasarkan jenis asuransi pasien
                    if (pasien.isBPJS()) {
                        strukController.setStrategy(new BPJSBiayaStrategy());
                    } else if (pasien.isAsuransi()) {
                        strukController.setStrategy(new AsuransiBiayaStrategy());
                    } else {
                        strukController.setStrategy(new RegulerBiayaStrategy());
                    }
                }
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

                // ============================================================
                // REFRESH DETAIL TABLE (LIVE)
                // ============================================================
                refreshDetailTable();
                loadData();
                loadObat();

                // Reset pilihan
                resepTable.clearSelection();
                // Re-select resep yang sama
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
            // ============================================================
            // AUTO SELECT STRATEGY BERDASARKAN PASIEN
            // ============================================================
            Kunjungan kunjungan = kunjunganController.getById(selectedResep.getIdKunjungan());
            if (kunjungan != null) {
                Pasien pasien = pasienController.cariById(kunjungan.getIdPasien());
                if (pasien != null) {
                    if (pasien.isBPJS()) {
                        strukController.setStrategy(new BPJSBiayaStrategy());
                    } else if (pasien.isAsuransi()) {
                        strukController.setStrategy(new AsuransiBiayaStrategy());
                    } else {
                        strukController.setStrategy(new RegulerBiayaStrategy());
                    }
                }
            }

            BigDecimal biayaKonsultasi = new BigDecimal("100000");
            Struk struk = strukController.cetakStruk(selectedResep.getIdResep(), biayaKonsultasi);

            if (struk != null) {
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

    private JPanel createRiwayatPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(Theme.pad(10, 6, 6, 6));

        // ============================================================
        // SPLIT PANE: LEFT = DAFTAR OBAT, RIGHT = RINCIAN
        // ============================================================
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.3);
        splitPane.setBorder(null);
        splitPane.setDividerSize(12);

        // ============================================================
        // LEFT PANEL: DAFTAR OBAT (JLIST)
        // ============================================================
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(Theme.titledBorder("Daftar Obat Terjual"));
        leftPanel.setBackground(Color.WHITE);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> obatList = new JList<>(listModel);
        Theme.styleList(obatList);
        obatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScroll = new JScrollPane(obatList);
        Theme.styleScroll(listScroll);
        leftPanel.add(listScroll, BorderLayout.CENTER);

        JLabel totalObatLabel = new JLabel("Total: 0 obat");
        Theme.styleStatus(totalObatLabel);
        totalObatLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        leftPanel.add(totalObatLabel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);

        // ============================================================
        // RIGHT PANEL: RINCIAN TRANSAKSI
        // ============================================================
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(Theme.titledBorder("Rincian Transaksi"));
        rightPanel.setBackground(Color.WHITE);

        // Label nama obat yang dipilih
        JLabel obatDipilihLabel = new JLabel("Pilih obat di sebelah kiri");
        obatDipilihLabel.setFont(Theme.font(Font.BOLD, 14f));
        obatDipilihLabel.setForeground(Theme.PRIMARY_DARK);
        obatDipilihLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rightPanel.add(obatDipilihLabel, BorderLayout.NORTH);

        // Tabel rincian
        String[] columns = {"No", "Nama Pasien", "Kuantitas", "Tanggal"};
        DefaultTableModel rincianTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable rincianTable = new JTable(rincianTableModel);
        Theme.styleTable(rincianTable);
        rincianTable.getColumnModel().getColumn(0).setMaxWidth(50);
        rincianTable.getColumnModel().getColumn(2).setMaxWidth(80);

        JScrollPane rincianScroll = new JScrollPane(rincianTable);
        Theme.styleScroll(rincianScroll);
        rightPanel.add(rincianScroll, BorderLayout.CENTER);

        // Total transaksi label
        JLabel totalTransaksiLabel = new JLabel("Total: 0 transaksi");
        Theme.styleStatus(totalTransaksiLabel);
        totalTransaksiLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rightPanel.add(totalTransaksiLabel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);

        panel.add(splitPane, BorderLayout.CENTER);

        // ============================================================
        // LOAD DATA DAFTAR OBAT
        // ============================================================
        try {
            List<String> obatListData = strukController.getDaftarObatTerjual();
            for (String nama : obatListData) {
                listModel.addElement(nama);
            }
            totalObatLabel.setText("Total: " + obatListData.size() + " obat");
        } catch (Exception e) {
            SwingUtils.showError(panel, "Error load daftar obat: " + e.getMessage());
            e.printStackTrace();
        }

        // ============================================================
        // EVENT LISTENER: KLIK OBAT → TAMPILKAN RINCIAN
        // ============================================================
        obatList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            String selectedObat = obatList.getSelectedValue();
            if (selectedObat == null) {
                return;
            }

            // Update label
            obatDipilihLabel.setText("💊 " + selectedObat);

            // Clear table
            rincianTableModel.setRowCount(0);

            // Load rincian
            try {
                List<Object[]> rincian = strukController.getRincianByObat(selectedObat);
                int no = 1;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                for (Object[] row : rincian) {
                    rincianTableModel.addRow(new Object[]{
                            no++,
                            row[0], // nama_pasien
                            row[1], // kuantitas
                            row[2] != null ? ((LocalDateTime) row[2]).format(formatter) : "-"
                    });
                }
                totalTransaksiLabel.setText("Total: " + rincian.size() + " transaksi");
            } catch (Exception ex) {
                SwingUtils.showError(panel, "Error load rincian: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // ============================================================
        // TOMBOL REFRESH
        // ============================================================
        JPanel topBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBtnPanel.setOpaque(false);
        JButton refreshBtn = Theme.outlineButton("Refresh", Theme.SUCCESS);
        refreshBtn.addActionListener(ev -> {
            // Refresh daftar obat
            listModel.clear();
            try {
                List<String> obatListData = strukController.getDaftarObatTerjual();
                for (String nama : obatListData) {
                    listModel.addElement(nama);
                }
                totalObatLabel.setText("Total: " + obatListData.size() + " obat");
            } catch (Exception ex) {
                SwingUtils.showError(panel, "Error refresh: " + ex.getMessage());
            }

            // Clear rincian
            rincianTableModel.setRowCount(0);
            totalTransaksiLabel.setText("Total: 0 transaksi");
            obatDipilihLabel.setText("Pilih obat di sebelah kiri");
        });
        topBtnPanel.add(refreshBtn);
        panel.add(topBtnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshDetailTable() {
        detailTableModel.setRowCount(0);
        if (selectedResep == null) {
            subTotalLabel.setText("Rp 0");
            return;
        }

        try {
            List<ResepDetail> details = resepController.getDetailResep(selectedResep.getIdResep());
            BigDecimal subTotal = BigDecimal.ZERO;
            for (ResepDetail rd : details) {
                BigDecimal subtotal = rd.getSubTotal();
                subTotal = subTotal.add(subtotal);
                detailTableModel.addRow(new Object[]{
                        rd.getIdResepDetail(),
                        rd.getNamaObat(),
                        rd.getJumlah(),
                        rd.getHargaSatuan() != null ? "Rp " + rd.getHargaSatuan() : "-",
                        "Rp " + subtotal
                });
            }
            subTotalLabel.setText("Rp " + subTotal);

            // Update detail resep area dengan info tambahan
            if (selectedResep != null) {
                detailResepArea.setText("Pasien: " + selectedResep.getNamaPasien() + "\n" +
                        "Dokter: " + selectedResep.getNamaDokter() + "\n" +
                        "Resep: " + selectedResep.getObatDanPerlakuan() + "\n" +
                        "Status: " + selectedResep.getStatusResep().getDisplayName() +
                        " | Item terassign: " + details.size());
            }

            hitungTotal();

        } catch (Exception e) {
            SwingUtils.showError(this, "Error refresh detail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================================================
// EDIT JUMLAH OBAT YANG SUDAH DIASSIGN
// ============================================================
    private void editJumlah() {
        int row = detailTable.getSelectedRow();
        if (row < 0) {
            SwingUtils.showError(this, "Pilih obat yang akan diedit!");
            return;
        }

        int idResepDetail = (int) detailTableModel.getValueAt(row, 0);
        String namaObat = (String) detailTableModel.getValueAt(row, 1);
        int jumlahSekarang = (int) detailTableModel.getValueAt(row, 2);

        String input = JOptionPane.showInputDialog(this,
                "Masukkan jumlah baru untuk " + namaObat + ":",
                "Edit Jumlah",
                JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            int jumlahBaru = Integer.parseInt(input.trim());
            if (jumlahBaru <= 0) {
                SwingUtils.showError(this, "Jumlah harus lebih dari 0!");
                return;
            }

            boolean success = resepController.updateJumlahResepDetail(idResepDetail, jumlahBaru);
            if (success) {
                SwingUtils.showSuccess(this, "✅ Jumlah " + namaObat + " diubah menjadi " + jumlahBaru);
                refreshDetailTable();
                loadData();
                loadObat();
            } else {
                SwingUtils.showError(this, "Gagal mengubah jumlah!");
            }
        } catch (NumberFormatException e) {
            SwingUtils.showError(this, "Jumlah harus berupa angka!");
        } catch (Exception e) {
            SwingUtils.showError(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================================================
// HAPUS OBAT YANG SUDAH DIASSIGN
// ============================================================
    private void hapusItem() {
        int row = detailTable.getSelectedRow();
        if (row < 0) {
            SwingUtils.showError(this, "Pilih obat yang akan dihapus!");
            return;
        }

        int idResepDetail = (int) detailTableModel.getValueAt(row, 0);
        String namaObat = (String) detailTableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus " + namaObat + " dari daftar assign?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean success = resepController.hapusResepDetail(idResepDetail);
            if (success) {
                SwingUtils.showSuccess(this, "✅ " + namaObat + " berhasil dihapus!");
                refreshDetailTable();
                loadData();
                loadObat();
            } else {
                SwingUtils.showError(this, "Gagal menghapus item!");
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}