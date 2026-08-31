package com.sirekam.client.dokter;

import com.sirekam.client.LoginDialog;
import com.sirekam.model.User;
import com.sirekam.util.SwingUtils;
import javax.swing.*;
import java.awt.*;

public class DokterApp extends JFrame {
    private User currentUser;
    private DokterDashboardUI dashboard;

    public DokterApp() {
        // ============================================================
        // TAMPILKAN LOGIN DULU
        // ============================================================
        LoginDialog loginDialog = new LoginDialog(this, "Dokter");
        loginDialog.setVisible(true);

        if (!loginDialog.isLoginSuccess()) {
            // Tutup aplikasi
            dispose();
            return;
        }

        currentUser = loginDialog.getLoggedInUser();
        if (!currentUser.getRole().getValue().equals("dokter")) {
            SwingUtils.showError(this, "Anda tidak memiliki akses ke aplikasi Dokter!");
            dispose();
            return;
        }

        // ============================================================
        // SETELAH LOGIN SUKSES, BARU INIT UI
        // ============================================================
        initUI();
        setVisible(true);
    }

    private void initUI() {
        setTitle("SIREKAM - Aplikasi Dokter");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        dashboard = new DokterDashboardUI(currentUser);
        setContentPane(dashboard);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DokterApp());
    }
}