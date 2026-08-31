package com.sirekam.client.dokter;

import com.sirekam.model.*;
import com.sirekam.model.enums.JenisChat;
import com.sirekam.model.enums.StatusKunjungan;
import com.sirekam.model.enums.StatusResep;
import com.sirekam.controller.KunjunganController;
import com.sirekam.controller.ResepController;
import com.sirekam.controller.DokterController;
import com.sirekam.util.SwingUtils;
import com.sirekam.pattern.iterator.KunjunganIterator;
import com.sirekam.chat.client.ChatClientGUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 244, 248));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(46, 204, 113));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel title = new JLabel("👨‍⚕️ Dashboard Dokter");
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

        // ============================================================
        // LEFT PANEL - TABBED: Pasien + Chat Petugas + Chat Apoteker
        // ============================================================
        JPanel leftPanel = createLeftPanel();

        JTabbedPane leftTabbedPane = new JTabbedPane();
        leftTabbedPane.addTab("📋 Pasien Ditugaskan", leftPanel);
        leftTabbedPane.addTab("💬 Chat Petugas", createChatPetugasPanel());
        leftTabbedPane.addTab("💬 Chat Apoteker", createChatApotekerPanel());

        splitPane.setLeftComponent(leftTabbedPane);

        // Right Panel - Detail & Resep
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
        leftPanel.setBorder(BorderFactory.createTitledBorder("📋 Pasien Ditugaskan"));
        leftPanel.setBackground(Color.WHITE);

        String[] columns = {"No RM", "Nama", "Keluhan", "Status"};
        pasienTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pasienTable = new JTable(pasienTableModel);
        pasienTable.setRowHeight(30);
        pasienTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectPasien();
        });
        JScrollPane scrollPane = new JScrollPane(pasienTable);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftPanel.add(statusLabel, BorderLayout.SOUTH);

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadData());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(refreshBtn);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("📝 Detail Pasien & Resep"));
        rightPanel.setBackground(Color.WHITE);

        JPanel detailPanel = new JPanel(new GridBagLayout());
        detailPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        detailPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 5, 3, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        detailPanel.add(new JLabel("Nama Pasien:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        detailPasienArea = new JTextArea(1, 20);
        detailPasienArea.setEditable(false);
        detailPasienArea.setBackground(new Color(240, 240, 240));
        detailPanel.add(detailPasienArea, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        detailPanel.add(new JLabel("Keluhan:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        detailKeluhanArea = new JTextArea(3, 20);
        detailKeluhanArea.setEditable(false);
        detailKeluhanArea.setBackground(new Color(240, 240, 240));
        detailKeluhanArea.setLineWrap(true);
        detailKeluhanArea.setWrapStyleWord(true);
        JScrollPane kelScroll = new JScrollPane(detailKeluhanArea);
        detailPanel.add(kelScroll, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        detailPanel.add(new JLabel("Resep (Obat & Perlakuan):"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        resepArea = new JTextArea(5, 20);
        resepArea.setLineWrap(true);
        resepArea.setWrapStyleWord(true);
        resepArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane resepScroll = new JScrollPane(resepArea);
        detailPanel.add(resepScroll, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(Color.WHITE);
        JButton kirimBtn = new JButton("📤 Kirim Resep ke Apoteker");
        kirimBtn.setBackground(new Color(46, 204, 113));
        kirimBtn.setForeground(Color.WHITE);
        kirimBtn.addActionListener(e -> kirimResep());
        kirimBtn.setFont(new Font("Arial", Font.BOLD, 12));
        JButton resetBtn = new JButton("↺ Reset");
        resetBtn.addActionListener(e -> resetForm());
        btnPanel.add(kirimBtn);
        btnPanel.add(resetBtn);
        detailPanel.add(btnPanel, gbc);

        rightPanel.add(detailPanel, BorderLayout.CENTER);

        return rightPanel;
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
            List<Kunjungan> list = kunjunganController.getByDokter(idDokter);
            updateTable(list);
            if (statusLabel != null) {
                statusLabel.setText("Total pasien ditugaskan: " + list.size());
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