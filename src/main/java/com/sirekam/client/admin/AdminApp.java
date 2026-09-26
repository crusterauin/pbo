package com.sirekam.client.admin;

import com.sirekam.client.LoginDialog;
import com.sirekam.model.User;
import com.sirekam.util.SwingUtils;
import com.sirekam.util.Theme;
import javax.swing.*;
import java.awt.*;

public class AdminApp extends JFrame {
    private User currentUser;
    private AdminDashboardUI dashboard;

    public AdminApp() {
        Theme.install();
        // ============================================================
        // TAMPILKAN LOGIN DULU
        // ============================================================
        LoginDialog loginDialog = new LoginDialog(this, "Admin");
        loginDialog.setVisible(true);

        if (!loginDialog.isLoginSuccess()) {
            dispose();
            return;
        }

        currentUser = loginDialog.getLoggedInUser();
        if (!currentUser.getRole().getValue().equals("admin")) {
            SwingUtils.showError(this, "Anda tidak memiliki akses ke aplikasi Admin!");
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
        setTitle("SIREKAM - Aplikasi Admin");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        Theme.install();

        dashboard = new AdminDashboardUI(currentUser);
        setContentPane(dashboard);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminApp::new);
    }
}
