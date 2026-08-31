package com.sirekam.client.petugas;

import com.sirekam.client.LoginDialog;
import com.sirekam.model.User;
import com.sirekam.util.SwingUtils;
import javax.swing.*;
import java.awt.*;

public class PetugasApp extends JFrame {
    private User currentUser;
    private PetugasDashboardUI dashboard;

    public PetugasApp() {
        LoginDialog loginDialog = new LoginDialog(this, "Petugas");
        loginDialog.setVisible(true);

        if (!loginDialog.isLoginSuccess()) {
            // Tutup aplikasi
            this.dispose();
            return;
        }

        currentUser = loginDialog.getLoggedInUser();
        if (!currentUser.getRole().getValue().equals("pendaftaran")) {
            SwingUtils.showError(this, "Anda tidak memiliki akses ke aplikasi Petugas!");
            this.dispose();
            return;
        }

        initUI();
        setVisible(true);
    }

    private void initUI() {
        setTitle("SIREKAM - Aplikasi Petugas");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        dashboard = new PetugasDashboardUI(currentUser);
        setContentPane(dashboard);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PetugasApp());
    }
}