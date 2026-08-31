package com.sirekam.client.petugas;

import com.sirekam.model.Kunjungan;
import com.sirekam.model.Pasien;
import com.sirekam.controller.KunjunganController;
import com.sirekam.util.SwingUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;  // <-- TAMBAHKAN INI
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RiwayatKunjunganDialog extends JDialog {

    private Pasien pasien;
    private KunjunganController kunjunganController;
    private JTable riwayatTable;
    private DefaultTableModel tableModel;
    private JLabel pasienInfoLabel;
    private JLabel totalKunjunganLabel;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public RiwayatKunjunganDialog(Frame parent, Pasien pasien) {
        super(parent, "Riwayat Kunjungan - " + pasien.getNama(), true);
        this.pasien = pasien;
        this.kunjunganController = new KunjunganController();

        setSize(800, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        loadRiwayat();
        SwingUtils.centerWindow(this);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // ============ HEADER PANEL ============
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Icon & Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("📋 RIWAYAT KUNJUNGAN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Informasi Pasien
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel label1 = new JLabel("Nama:");
        label1.setForeground(Color.WHITE);
        headerPanel.add(label1, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        pasienInfoLabel = new JLabel(pasien.getNama() + " (" + pasien.getIdPasien() + ")");
        pasienInfoLabel.setForeground(Color.WHITE);
        pasienInfoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        headerPanel.add(pasienInfoLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel label2 = new JLabel("Total Kunjungan:");
        label2.setForeground(Color.WHITE);
        headerPanel.add(label2, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        totalKunjunganLabel = new JLabel("0");
        totalKunjunganLabel.setForeground(Color.WHITE);
        totalKunjunganLabel.setFont(new Font("Arial", Font.BOLD, 12));
        headerPanel.add(totalKunjunganLabel, gbc);

        add(headerPanel, BorderLayout.NORTH);

        // ============ TABLE ============
        String[] columns = {"ID", "Tanggal", "Dokter", "Keluhan", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        riwayatTable = new JTable(tableModel);
        riwayatTable.setRowHeight(30);
        riwayatTable.getColumnModel().getColumn(0).setMaxWidth(80);
        riwayatTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        riwayatTable.getColumnModel().getColumn(4).setMaxWidth(120);

        // ============ RENDERER WARNA STATUS ============
        riwayatTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected && column == 4) {
                    String status = value != null ? value.toString() : "";
                    switch (status) {
                        case "Menunggu":
                            c.setBackground(new Color(255, 243, 224));
                            c.setForeground(new Color(230, 126, 34));
                            break;
                        case "Diperiksa":
                            c.setBackground(new Color(224, 247, 250));
                            c.setForeground(new Color(0, 150, 200));
                            break;
                        case "Selesai":
                            c.setBackground(new Color(232, 245, 233));
                            c.setForeground(new Color(46, 125, 50));
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                            break;
                    }
                } else if (!isSelected) {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(riwayatTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📋 Daftar Kunjungan"));
        add(scrollPane, BorderLayout.CENTER);

        // ============ BOTTOM PANEL ============
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton closeBtn = new JButton("Tutup");
        closeBtn.setBackground(new Color(41, 128, 185));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.addActionListener(e -> dispose());

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadRiwayat());

        bottomPanel.add(refreshBtn);
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadRiwayat() {
        tableModel.setRowCount(0);

        try {
            List<Kunjungan> list = kunjunganController.getByPasien(pasien.getIdPasien());

            if (list != null && !list.isEmpty()) {
                for (Kunjungan k : list) {
                    tableModel.addRow(new Object[]{
                            k.getIdKunjungan(),
                            k.getTanggalKunjungan() != null ?
                                    k.getTanggalKunjungan().format(DATE_FORMATTER) : "-",
                            k.getNamaDokter() != null ? k.getNamaDokter() : "-",
                            k.getKeluhan() != null ? k.getKeluhan() : "-",
                            k.getStatus() != null ? k.getStatus().getDisplayName() : "-"
                    });
                }
                totalKunjunganLabel.setText(String.valueOf(list.size()));
            } else {
                tableModel.addRow(new Object[]{"-", "-", "-", "Tidak ada riwayat kunjungan", "-"});
                totalKunjunganLabel.setText("0");
            }

        } catch (SQLException e) {
            SwingUtils.showError(this, "Error load riwayat: " + e.getMessage());
            e.printStackTrace();
        }
    }
}