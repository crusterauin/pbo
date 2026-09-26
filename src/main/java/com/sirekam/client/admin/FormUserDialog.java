package com.sirekam.client.admin;

import com.sirekam.model.User;
import com.sirekam.model.enums.Role;
import com.sirekam.controller.UserController;
import com.sirekam.util.SwingUtils;
import com.sirekam.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Form untuk Admin: menambah akun baru (petugas/dokter/apoteker)
 * atau mengubah username & password akun yang sudah ada.
 */
public class FormUserDialog extends JDialog {

    private final UserController userController;
    private final User editingUser; // null jika mode tambah baru

    private JTextField namaField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<Role> roleCombo;
    private JTextField spesialisasiField;
    private JLabel spesialisasiLabel;
    private boolean saved;

    public FormUserDialog(Frame parent, User editingUser) {
        super(parent, editingUser == null ? "Tambah Akun Baru" : "Edit Akun - " + editingUser.getUsername(), true);
        this.userController = new UserController();
        this.editingUser = editingUser;
        Theme.install();
        setSize(460, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
        SwingUtils.centerWindow(this);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        getContentPane().setBackground(Theme.BG);
        ((JComponent) getContentPane()).setBorder(Theme.pad(16, 16, 16, 16));

        Theme.Card card = new Theme.Card(null);
        card.setBorder(Theme.pad(20, 24, 24, 24));
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        JLabel titleLabel = new JLabel(editingUser == null ? "Tambah Akun Petugas/Dokter/Apoteker" : "Edit Akun");
        titleLabel.setFont(Theme.font(Font.BOLD, 16f));
        titleLabel.setForeground(Theme.PRIMARY_DARK);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(titleLabel, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(Theme.caption("Nama Lengkap:"), gbc);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 12, 0);
        namaField = new JTextField();
        card.add(namaField, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(Theme.caption("Username:"), gbc);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 12, 0);
        usernameField = new JTextField();
        card.add(usernameField, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(Theme.caption(editingUser == null ? "Password:" : "Password Baru (kosongkan jika tidak diubah):"), gbc);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 12, 0);
        passwordField = new JPasswordField();
        card.add(passwordField, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(Theme.caption("Role:"), gbc);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 12, 0);
        roleCombo = new JComboBox<>(new Role[]{Role.PENDAFTARAN, Role.DOKTER, Role.APOTEKER});
        roleCombo.addActionListener(e -> updateSpesialisasiVisibility());
        card.add(roleCombo, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 4, 0);
        spesialisasiLabel = Theme.caption("Spesialisasi Dokter:");
        card.add(spesialisasiLabel, gbc);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 12, 0);
        spesialisasiField = new JTextField();
        card.add(spesialisasiField, gbc);

        if (editingUser != null) {
            namaField.setText(editingUser.getNamaLengkap());
            usernameField.setText(editingUser.getUsername());
            roleCombo.setSelectedItem(editingUser.getRole());
            roleCombo.setEnabled(false); // role tidak diubah lewat form edit sederhana ini
        }
        updateSpesialisasiVisibility();

        add(card, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        btnPanel.setBackground(Theme.BG);
        btnPanel.setBorder(Theme.pad(10, 0, 0, 0));
        JButton cancelBtn = Theme.outlineButton("Batal", Theme.MUTED);
        cancelBtn.addActionListener(e -> dispose());
        JButton saveBtn = Theme.button(editingUser == null ? "Simpan" : "Update", Theme.PRIMARY);
        saveBtn.addActionListener(e -> doSave());
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        add(btnPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(saveBtn);
    }

    private void updateSpesialisasiVisibility() {
        boolean isDokter = roleCombo.getSelectedItem() == Role.DOKTER;
        spesialisasiLabel.setVisible(isDokter && editingUser == null);
        spesialisasiField.setVisible(isDokter && editingUser == null);
    }

    private void doSave() {
        String nama = namaField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        Role role = (Role) roleCombo.getSelectedItem();

        if (nama.isEmpty() || username.isEmpty()) {
            SwingUtils.showError(this, "Nama dan username harus diisi!");
            return;
        }
        if (editingUser == null && password.isEmpty()) {
            SwingUtils.showError(this, "Password harus diisi untuk akun baru!");
            return;
        }

        try {
            if (editingUser == null) {
                User user = new User();
                user.setNamaLengkap(nama);
                user.setUsername(username);
                user.setPassword(password);
                user.setRole(role);
                boolean ok = userController.tambahAkun(user, spesialisasiField.getText().trim());
                if (ok) {
                    SwingUtils.showSuccess(this, "✅ Akun " + username + " berhasil ditambahkan!");
                    saved = true;
                    dispose();
                } else {
                    SwingUtils.showError(this, "Gagal menambahkan akun!");
                }
            } else {
                editingUser.setNamaLengkap(nama);
                editingUser.setUsername(username);
                if (!password.isEmpty()) {
                    editingUser.setPassword(password);
                }
                boolean ok = userController.ubahAkun(editingUser);
                if (ok) {
                    SwingUtils.showSuccess(this, "✅ Akun " + username + " berhasil diperbarui!");
                    saved = true;
                    dispose();
                } else {
                    SwingUtils.showError(this, "Gagal memperbarui akun!");
                }
            }
        } catch (SQLException ex) {
            SwingUtils.showError(this, "Error database: " + ex.getMessage()
                    + (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("duplicate")
                        ? "\nUsername sudah digunakan." : ""));
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
