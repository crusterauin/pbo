package com.sirekam.client.admin;

import com.sirekam.model.*;
import com.sirekam.model.enums.Role;
import com.sirekam.controller.UserController;
import com.sirekam.controller.ObatController;
import com.sirekam.dao.StokLogDAO;
import com.sirekam.util.SwingUtils;
import com.sirekam.util.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dashboard untuk role Admin.
 * 1. Manajemen Akun   -> tambah akun & ubah username/password petugas/dokter/apoteker
 * 2. Manajemen Gudang -> kelola data obat (tambah/kurangi/hapus), lihat stok masuk & keluar
 */
public class AdminDashboardUI extends JPanel {

    private final User currentUser;
    private final UserController userController = new UserController();
    private final ObatController obatController = new ObatController();
    private final StokLogDAO stokLogDAO = new StokLogDAO();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Manajemen Akun
    private JTable userTable;
    private DefaultTableModel userTableModel;

    // Manajemen Gudang - obat
    private JTable obatTable;
    private DefaultTableModel obatTableModel;

    // Manajemen Gudang - stok masuk / keluar
    private JTable stokMasukTable;
    private DefaultTableModel stokMasukTableModel;
    private JTable stokKeluarTable;
    private DefaultTableModel stokKeluarTableModel;

    public AdminDashboardUI(User user) {
        this.currentUser = user;
        initComponents();
        loadUserData();
        loadObatData();
        loadStokMasuk();
        loadStokKeluar();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        setBackground(Theme.BG);

        add(Theme.header("Dashboard Admin",
                currentUser.getNamaLengkap() + " | " +
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                Theme.PRIMARY_DARK, Theme.PRIMARY), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        Theme.styleTabs(tabs);
        tabs.addTab("Manajemen Akun", createManajemenAkunPanel());
        tabs.addTab("Manajemen Gudang", createManajemenGudangPanel());

        Theme.Card mainCard = new Theme.Card(null);
        mainCard.setBorder(Theme.pad(8, 8, 8, 8));
        mainCard.add(tabs, BorderLayout.CENTER);
        add(mainCard, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel("SIREKAM", SwingConstants.CENTER);
        footerLabel.setFont(Theme.font(Font.BOLD, 11f));
        footerLabel.setForeground(Theme.MUTED);
        add(footerLabel, BorderLayout.SOUTH);
    }

    // ================================================================
    // 1. MANAJEMEN AKUN
    // ================================================================
    private JPanel createManajemenAkunPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(Theme.pad(14, 10, 10, 10));

        String[] cols = {"ID", "Username", "Nama Lengkap", "Role"};
        userTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        userTable = new JTable(userTableModel);
        Theme.styleTable(userTable);
        Theme.columnWidths(userTable, 50, 160, 220, 140);
        JScrollPane scroll = new JScrollPane(userTable);
        Theme.styleScroll(scroll);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        btnPanel.setOpaque(false);

        JButton refreshBtn = Theme.outlineButton("Refresh", Theme.INFO);
        refreshBtn.addActionListener(e -> loadUserData());

        JButton editBtn = Theme.outlineButton("Ubah Username/Password", Theme.WARNING);
        editBtn.addActionListener(e -> editAkunTerpilih());

        JButton hapusBtn = Theme.outlineButton("Hapus Akun", Theme.DANGER);
        hapusBtn.addActionListener(e -> hapusAkunTerpilih());

        JButton tambahBtn = Theme.button("+ Tambah Akun Baru", Theme.PRIMARY);
        tambahBtn.addActionListener(e -> {
            FormUserDialog dialog = new FormUserDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadUserData();
            }
        });

        btnPanel.add(refreshBtn);
        btnPanel.add(editBtn);
        btnPanel.add(hapusBtn);
        btnPanel.add(tambahBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadUserData() {
        userTableModel.setRowCount(0);
        try {
            List<User> users = userController.findAllPetugas();
            for (User u : users) {
                userTableModel.addRow(new Object[]{
                        u.getIdUser(), u.getUsername(), u.getNamaLengkap(), u.getRole().getDisplayName()
                });
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error load akun: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editAkunTerpilih() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            SwingUtils.showError(this, "Pilih akun yang akan diubah!");
            return;
        }
        int idUser = (int) userTableModel.getValueAt(row, 0);
        try {
            User user = (User) userController.cariById(idUser);
            if (user == null) {
                SwingUtils.showError(this, "Akun tidak ditemukan!");
                return;
            }
            FormUserDialog dialog = new FormUserDialog((Frame) SwingUtilities.getWindowAncestor(this), user);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadUserData();
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void hapusAkunTerpilih() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            SwingUtils.showError(this, "Pilih akun yang akan dihapus!");
            return;
        }
        int idUser = (int) userTableModel.getValueAt(row, 0);
        String username = (String) userTableModel.getValueAt(row, 1);

        int confirm = SwingUtils.showConfirm(this,
                "Yakin ingin menghapus akun " + username + "?", "Konfirmasi Hapus");
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = userController.hapusAkun(idUser);
            if (ok) {
                SwingUtils.showSuccess(this, "✅ Akun " + username + " berhasil dihapus!");
                loadUserData();
            } else {
                SwingUtils.showError(this, "Gagal menghapus akun!");
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================================================================
    // 2. MANAJEMEN GUDANG
    // ================================================================
    private JPanel createManajemenGudangPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JTabbedPane subTabs = new JTabbedPane();
        Theme.styleTabs(subTabs);
        subTabs.addTab("Data Obat", createDataObatPanel());
        subTabs.addTab("Stok Masuk", createStokMasukPanel());
        subTabs.addTab("Stok Keluar", createStokKeluarPanel());
        panel.add(subTabs, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDataObatPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(Theme.pad(14, 10, 10, 10));

        String[] cols = {"ID", "Nama Obat", "Satuan", "Stok", "Harga Satuan"};
        obatTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        obatTable = new JTable(obatTableModel);
        Theme.styleTable(obatTable);
        Theme.columnWidths(obatTable, 50, 240, 100, 90, 140);
        JScrollPane scroll = new JScrollPane(obatTable);
        Theme.styleScroll(scroll);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        btnPanel.setOpaque(false);

        JButton refreshBtn = Theme.outlineButton("Refresh", Theme.INFO);
        refreshBtn.addActionListener(e -> { loadObatData(); loadStokMasuk(); loadStokKeluar(); });

        JButton tambahStokBtn = Theme.outlineButton("+ Tambah Stok (Masuk)", Theme.SUCCESS);
        tambahStokBtn.addActionListener(e -> prosesStok(true));

        JButton kurangiStokBtn = Theme.outlineButton("- Kurangi Stok (Keluar)", Theme.WARNING);
        kurangiStokBtn.addActionListener(e -> prosesStok(false));

        JButton editBtn = Theme.outlineButton("Edit Obat", Theme.MUTED);
        editBtn.addActionListener(e -> editObatTerpilih());

        JButton hapusBtn = Theme.outlineButton("Hapus Obat", Theme.DANGER);
        hapusBtn.addActionListener(e -> hapusObatTerpilih());

        JButton tambahBtn = Theme.button("+ Tambah Obat Baru", Theme.PRIMARY);
        tambahBtn.addActionListener(e -> {
            FormObatDialog dialog = new FormObatDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadObatData();
                loadStokMasuk();
            }
        });

        btnPanel.add(refreshBtn);
        btnPanel.add(tambahStokBtn);
        btnPanel.add(kurangiStokBtn);
        btnPanel.add(editBtn);
        btnPanel.add(hapusBtn);
        btnPanel.add(tambahBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadObatData() {
        obatTableModel.setRowCount(0);
        try {
            List<Obat> obatList = obatController.cariSemua();
            for (Obat o : obatList) {
                obatTableModel.addRow(new Object[]{
                        o.getIdObat(), o.getNamaObat(), o.getSatuan(), o.getStok(), "Rp " + o.getHargaSatuan()
                });
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error load data obat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editObatTerpilih() {
        int row = obatTable.getSelectedRow();
        if (row < 0) {
            SwingUtils.showError(this, "Pilih obat yang akan diedit!");
            return;
        }
        int idObat = (int) obatTableModel.getValueAt(row, 0);
        try {
            Obat obat = obatController.findById(idObat);
            if (obat == null) {
                SwingUtils.showError(this, "Obat tidak ditemukan!");
                return;
            }
            FormObatDialog dialog = new FormObatDialog((Frame) SwingUtilities.getWindowAncestor(this), obat);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadObatData();
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void hapusObatTerpilih() {
        int row = obatTable.getSelectedRow();
        if (row < 0) {
            SwingUtils.showError(this, "Pilih obat yang akan dihapus!");
            return;
        }
        int idObat = (int) obatTableModel.getValueAt(row, 0);
        String nama = (String) obatTableModel.getValueAt(row, 1);

        int confirm = SwingUtils.showConfirm(this,
                "Yakin ingin menghapus obat " + nama + " dari gudang?\n" +
                        "Obat ini juga akan hilang dari halaman Apoteker.", "Konfirmasi Hapus");
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = obatController.hapus(idObat);
            if (ok) {
                SwingUtils.showSuccess(this, "✅ Obat " + nama + " berhasil dihapus!");
                loadObatData();
            } else {
                SwingUtils.showError(this, "Gagal menghapus obat! Kemungkinan obat masih terpakai di resep.");
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** @param masuk true = tambah stok (barang masuk), false = kurangi stok manual (barang keluar) */
    private void prosesStok(boolean masuk) {
        int row = obatTable.getSelectedRow();
        if (row < 0) {
            SwingUtils.showError(this, "Pilih obat terlebih dahulu!");
            return;
        }
        int idObat = (int) obatTableModel.getValueAt(row, 0);
        String nama = (String) obatTableModel.getValueAt(row, 1);

        String jumlahStr = JOptionPane.showInputDialog(this,
                (masuk ? "Jumlah stok masuk untuk " : "Jumlah stok keluar untuk ") + nama + ":",
                masuk ? "Tambah Stok" : "Kurangi Stok", JOptionPane.QUESTION_MESSAGE);
        if (jumlahStr == null || jumlahStr.trim().isEmpty()) return;

        int jumlah;
        try {
            jumlah = Integer.parseInt(jumlahStr.trim());
            if (jumlah <= 0) {
                SwingUtils.showError(this, "Jumlah harus lebih dari 0!");
                return;
            }
        } catch (NumberFormatException e) {
            SwingUtils.showError(this, "Jumlah harus berupa angka!");
            return;
        }

        String keterangan = JOptionPane.showInputDialog(this, "Keterangan (opsional):",
                masuk ? "Pembelian stok baru" : "Obat rusak/kadaluarsa");
        if (keterangan == null) keterangan = masuk ? "Pembelian stok baru" : "Penyesuaian stok";

        try {
            boolean ok = masuk
                    ? obatController.tambahStok(idObat, jumlah, keterangan, currentUser.getIdUser())
                    : obatController.kurangiStokManual(idObat, jumlah, keterangan, currentUser.getIdUser());
            if (ok) {
                SwingUtils.showSuccess(this, "✅ Stok " + nama + " berhasil " + (masuk ? "ditambah!" : "dikurangi!"));
                loadObatData();
                if (masuk) loadStokMasuk(); else loadStokKeluar();
            } else {
                SwingUtils.showError(this, masuk ? "Gagal menambah stok!" : "Gagal mengurangi stok (stok tidak cukup)!");
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JPanel createStokMasukPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(Theme.pad(14, 10, 10, 10));

        String[] cols = {"Waktu", "Obat", "Jumlah", "Keterangan"};
        stokMasukTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        stokMasukTable = new JTable(stokMasukTableModel);
        Theme.styleTable(stokMasukTable);
        Theme.columnWidths(stokMasukTable, 150, 220, 80, 260);
        JScrollPane scroll = new JScrollPane(stokMasukTable);
        Theme.styleScroll(scroll);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        btnPanel.setOpaque(false);
        JButton refreshBtn = Theme.outlineButton("Refresh", Theme.SUCCESS);
        refreshBtn.addActionListener(e -> loadStokMasuk());
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadStokMasuk() {
        stokMasukTableModel.setRowCount(0);
        try {
            List<StokLog> logs = stokLogDAO.findByJenis("masuk");
            for (StokLog l : logs) {
                stokMasukTableModel.addRow(new Object[]{
                        l.getWaktu() != null ? l.getWaktu().format(DATE_FMT) : "-",
                        l.getNamaObat(), l.getJumlah(), l.getKeterangan()
                });
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error load stok masuk: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JPanel createStokKeluarPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(Theme.pad(14, 10, 10, 10));

        String[] cols = {"Waktu", "Obat", "Jumlah", "Keterangan"};
        stokKeluarTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        stokKeluarTable = new JTable(stokKeluarTableModel);
        Theme.styleTable(stokKeluarTable);
        Theme.columnWidths(stokKeluarTable, 150, 220, 80, 260);
        JScrollPane scroll = new JScrollPane(stokKeluarTable);
        Theme.styleScroll(scroll);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        btnPanel.setOpaque(false);
        JButton refreshBtn = Theme.outlineButton("Refresh", Theme.DANGER);
        refreshBtn.addActionListener(e -> loadStokKeluar());
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadStokKeluar() {
        stokKeluarTableModel.setRowCount(0);
        try {
            List<StokLog> logs = stokLogDAO.findByJenis("keluar");
            for (StokLog l : logs) {
                stokKeluarTableModel.addRow(new Object[]{
                        l.getWaktu() != null ? l.getWaktu().format(DATE_FMT) : "-",
                        l.getNamaObat(), l.getJumlah(), l.getKeterangan()
                });
            }
        } catch (Exception e) {
            SwingUtils.showError(this, "Error load stok keluar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
