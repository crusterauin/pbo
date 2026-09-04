package com.sirekam.client;

import com.sirekam.model.User;
import com.sirekam.controller.LoginController;
import com.sirekam.util.SwingUtils;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private User loggedInUser;
    private boolean loginSuccess;

    public LoginDialog(JFrame parent, String roleTitle) {
        super(parent, "Login - " + roleTitle, true);
        setSize(450, 280);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        initComponents();
        SwingUtils.centerWindow(this);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // ===== HEADER =====
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));
        JLabel headerLabel = new JLabel("SIREKAM - Login");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // ===== FORM =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(250, 32));
        usernameField.setMinimumSize(new Dimension(200, 32));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 32));
        passwordField.setMinimumSize(new Dimension(200, 32));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ===== BUTTONS =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(41, 128, 185));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 13));
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.addActionListener(e -> doLogin());

        // ===== TOMBOL BATAL =====
        cancelButton = new JButton("Batal");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 13));
        cancelButton.setPreferredSize(new Dimension(100, 35));

        // ============================================================
        // SOLUSI: FORCE CLOSE
        // ============================================================
        cancelButton.addActionListener(e -> {
            System.out.println("🔴= [DEBUG] Tombol Batal diklik!");
            closeDialog(); // Panggil method khusus
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(loginButton);

        // ESC Key
        getRootPane().registerKeyboardAction(
                e -> closeDialog(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    // ============================================================
    // METHOD KHUSUS UNTUK MENUTUP DIALOG
    // ============================================================
    public void closeDialog() {
        System.out.println("🔴 [DEBUG] closeDialog() dipanggil!");
        setVisible(false);
        dispose();
        System.out.println("🔴 [DEBUG] Dialog sudah ditutup!");
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            SwingUtils.showError(this, "Username dan password harus diisi!");
            return;
        }

        try {
            LoginController controller = new LoginController();
            User user = controller.login(username, password);
            if (user != null) {
                loggedInUser = user;
                loginSuccess = true;
                closeDialog();
            } else {
                SwingUtils.showError(this, "Username atau password salah!");
                passwordField.setText("");
                passwordField.requestFocus();
            }
        } catch (SQLException e) {
            SwingUtils.showError(this, "Error koneksi database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }
}