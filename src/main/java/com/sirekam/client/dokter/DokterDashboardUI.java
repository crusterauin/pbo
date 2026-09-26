package com.sirekam.client.dokter;

import com.sirekam.model.*;
import com.sirekam.model.enums.JenisChat;
import com.sirekam.model.enums.StatusKunjungan;
import com.sirekam.model.enums.StatusResep;
import com.sirekam.controller.KunjunganController;
import com.sirekam.controller.ResepController;
import com.sirekam.controller.DokterController;
import com.sirekam.util.SwingUtils;
import com.sirekam.util.Theme;
import com.sirekam.pattern.iterator.KunjunganIterator;
import com.sirekam.chat.client.ChatClientGUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DokterDashboardUI extends JPanel {

    private User currentUser;
    private KunjunganController kunjunganController;
    private ResepController resepController;
    private DokterController dokterController;
    private int idDokter;

    private JTable pasienTable;
    private DefaultTableModel pasienTableModel;
    private JTextArea resepArea;
    private JLabel statusLabel;
    private Kunjungan selectedKunjungan;
    private JTextArea detailPasienArea;
    private JTextArea detailKeluhanArea;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DokterDashboardUI(User user) {
        this.currentUser = user;
        this.kunjunganController = new KunjunganController();
        this.resepController = new ResepController();
        this.dokterController = new DokterController();

        try {
            Dokter dokter = dokterController.findByUserId(user.getIdUser());
            if (dokter != null) {
                this.idDokter = dokter.getIdDokter();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        setBackground(Theme.BG);

        // Header
        add(Theme.header("Dashboard Dokter",
                currentUser.getNamaLengkap() + " | " +
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                new Color(0x0B4F7A), Theme.INFO), BorderLayout.NORTH);

        // Main Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        Theme.styleSplit(splitPane);
        splitPane.setResizeWeight(0.5);

        // LEFT PANEL - TABBED: Pasien + Chat Petugas + Chat Apoteker
        JPanel leftPanel = createLeftPanel();

        JTabbedPane leftTabbedPane = new JTabbedPane();
        Theme.styleTabs(leftTabbedPane);
        leftTabbedPane.addTab("Pasien Ditugaskan", leftPanel);
        leftTabbedPane.addTab("Chat Petugas", createChatPetugasPanel());
        leftTabbedPane.addTab("Chat Apoteker", createChatApotekerPanel());

        Theme.Card leftCard = new Theme.Card(null);
        leftCard.setBorder(Theme.pad(8, 8, 8, 8));
        leftCard.add(leftTabbedPane, BorderLayout.CENTER);
        splitPane.setLeftComponent(leftCard);

        // Right Panel - Detail & Resep
        splitPane.setRightComponent(createRightPanel());

        add(splitPane, BorderLayout.CENTER);
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.5));

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

        String[] columns = {"No RM", "Nama", "Keluhan", "Status"};
        pasienTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pasienTable = new JTable(pasienTableModel);
        Theme.styleTable(pasienTable);
        pasienTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectPasien();
        });
        JScrollPane scrollPane = new JScrollPane(pasienTable);
        Theme.styleScroll(scrollPane);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel(" ");
        Theme.styleStatus(statusLabel);

        JButton refreshBtn = Theme.outlineButton("Refresh", Theme.INFO);
        refreshBtn.addActionListener(e -> loadData());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(statusLabel, BorderLayout.WEST);
        bottom.add(refreshBtn, BorderLayout.EAST);
        leftPanel.add(bottom, BorderLayout.SOUTH);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        Theme.Card card = new Theme.Card("Detail Pasien & Resep");

        JPanel detailPanel = new JPanel(new GridBagLayout());
        detailPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        gbc.insets = new Insets(4, 0, 6, 0);
        detailPanel.add(Theme.caption("Nama Pasien:"), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 16, 0);
        detailPasienArea = new JTextArea(1, 20);
        Theme.styleReadOnly(detailPasienArea);
        detailPasienArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true), Theme.pad(9, 12, 9, 12)));
        detailPanel.add(detailPasienArea, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 6, 0);
        detailPanel.add(Theme.caption("Keluhan:"), gbc);

        gbc.gridy = 3;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 16, 0);
        detailKeluhanArea = new JTextArea(3, 20);
        Theme.styleReadOnly(detailKeluhanArea);
        JScrollPane kelScroll = new JScrollPane(detailKeluhanArea);
        Theme.styleScroll(kelScroll);
        detailPanel.add(kelScroll, gbc);

        gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 6, 0);
        detailPanel.add(Theme.caption("Resep (Obat & Perlakuan):"), gbc);

        gbc.gridy = 5;
        gbc.weighty = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 20, 0);
        resepArea = new JTextArea(5, 20);
        resepArea.setFont(Theme.font(Font.PLAIN, 14f));
        resepArea.setLineWrap(true);
        resepArea.setWrapStyleWord(true);
        JScrollPane resepScroll = new JScrollPane(resepArea);
        Theme.styleScroll(resepScroll);
        detailPanel.add(resepScroll, gbc);

        gbc.gridy = 6;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel btnPanel = new JPanel(new BorderLayout(10, 0));
        btnPanel.setOpaque(false);
        JButton kirimBtn = Theme.button("Kirim Resep ke Apoteker", Theme.SUCCESS);
        kirimBtn.addActionListener(e -> kirimResep());
        JButton resetBtn = Theme.outlineButton("↺ Reset", Theme.MUTED);
        resetBtn.addActionListener(e -> resetForm());
        btnPanel.add(kirimBtn, BorderLayout.CENTER);
        btnPanel.add(resetBtn, BorderLayout.EAST);
        detailPanel.add(btnPanel, gbc);

        card.add(detailPanel, BorderLayout.CENTER);
        return card;
    }

    // ============================================================
    // CHAT PANEL UNTUK PETUGAS (Kirim ke Petugas ID=1)
    // ============================================================
    private JPanel createChatPetugasPanel() {
        return new ChatClientGUI(
                currentUser,
                JenisChat.PETUGAS_DOKTER,
                1,  // Receiver ID = Petugas (id=1)
                "Petugas"
        );
    }

    // ============================================================
    // CHAT PANEL UNTUK APOTEKER (Kirim ke Apoteker ID=4)
    // ============================================================
    private JPanel createChatApotekerPanel() {
        return new ChatClientGUI(
                currentUser,
                JenisChat.DOKTER_APOTEKER,
                4,  // Receiver ID = Apoteker (id=4)
                "Apoteker"
        );
    }

    private void loadData() {
        try {
            // Ambil semua kunjungan untuk dokter ini
            List<Kunjungan> semuaList = kunjunganController.getByDokter(idDokter);

            // ============================================================
            // FILTER: hanya tampilkan yang statusnya MENUNGGU atau DIPERIKSA
            // ============================================================
            List<Kunjungan> list = new ArrayList<>();
            for (Kunjungan k : semuaList) {
                if (k.getStatus() == StatusKunjungan.MENUNGGU ||
                        k.getStatus() == StatusKunjungan.DIPERIKSA) {
                    list.add(k);
                }
            }
            // ============================================================

            updateTable(list);
            if (statusLabel != null) {
                statusLabel.setText("Total pasien aktif: " + list.size());
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error load data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTable(List<Kunjungan> list) {
        pasienTableModel.setRowCount(0);
        KunjunganIterator iterator = new KunjunganIterator(list);
        while (iterator.hasNext()) {
            Kunjungan k = iterator.next();
            String keluhanPreview = k.getKeluhan() != null ?
                    k.getKeluhan().substring(0, Math.min(30, k.getKeluhan().length())) + "..." : "-";
            pasienTableModel.addRow(new Object[]{
                    k.getIdPasien(),
                    k.getNamaPasien(),
                    keluhanPreview,
                    k.getStatus().getDisplayName()
            });
        }
    }

    private void selectPasien() {
        int row = pasienTable.getSelectedRow();
        if (row < 0) {
            selectedKunjungan = null;
            detailPasienArea.setText("");
            detailKeluhanArea.setText("");
            resepArea.setText("");
            return;
        }

        try {
            String idPasien = (String) pasienTableModel.getValueAt(row, 0);
            List<Kunjungan> list = kunjunganController.getByPasien(idPasien);
            if (!list.isEmpty()) {
                selectedKunjungan = list.get(0);
                detailPasienArea.setText(selectedKunjungan.getNamaPasien() + " (" + selectedKunjungan.getIdPasien() + ")");
                detailKeluhanArea.setText(selectedKunjungan.getKeluhan());
                resepArea.setText("");
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error load detail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void kirimResep() {
        if (selectedKunjungan == null) {
            SwingUtils.showError(this, "Silakan pilih pasien terlebih dahulu!");
            return;
        }

        String resep = resepArea.getText().trim();
        if (resep.isEmpty()) {
            SwingUtils.showError(this, "Resep harus diisi!");
            return;
        }

        try {
            kunjunganController.updateStatus(selectedKunjungan.getIdKunjungan(), StatusKunjungan.DIPERIKSA);

            Resep resepObj = new Resep();
            resepObj.setIdKunjungan(selectedKunjungan.getIdKunjungan());
            resepObj.setIdDokter(idDokter);
            resepObj.setObatDanPerlakuan(resep);
            resepObj.setStatusResep(StatusResep.MENUNGGU);

            int idResep = resepController.buatResep(resepObj);
            if (idResep > 0) {
                SwingUtils.showSuccess(this, "✅ Resep berhasil dikirim ke Apoteker!\n" +
                        "Pasien: " + selectedKunjungan.getNamaPasien());
                resetForm();
                loadData();
            } else {
                SwingUtils.showError(this, "Gagal mengirim resep!");
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void resetForm() {
        resepArea.setText("");
        pasienTable.clearSelection();
        selectedKunjungan = null;
        detailPasienArea.setText("");
        detailKeluhanArea.setText("");
    }
}