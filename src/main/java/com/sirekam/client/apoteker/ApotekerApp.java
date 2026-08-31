package com.sirekam.client.apoteker;

import com.sirekam.client.LoginDialog;
import com.sirekam.model.User;
import com.sirekam.util.SwingUtils;
import javax.swing.*;
import java.awt.*;

public class ApotekerApp extends JFrame {
    private User currentUser;
    private ApotekerDashboardUI dashboard;

    public ApotekerApp() {
        // ============================================================
        // TAMPILKAN LOGIN DULU
        // ============================================================
        LoginDialog loginDialog = new LoginDialog(this, "Apoteker");
        loginDialog.setVisible(true);

        if (!loginDialog.isLoginSuccess()) {
            // Tutup aplikasi
            dispose();
            return;
        }

        currentUser = loginDialog.getLoggedInUser();
        if (!currentUser.getRole().getValue().equals("apoteker")) {
            SwingUtils.showError(this, "Anda tidak memiliki akses ke aplikasi Apoteker!");
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
        setTitle("SIREKAM - Aplikasi Apoteker");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        dashboard = new ApotekerDashboardUI(currentUser);
        setContentPane(dashboard);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ApotekerApp());
    }
}