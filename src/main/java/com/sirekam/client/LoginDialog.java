package com.sirekam.client;

import com.sirekam.model.User;
import com.sirekam.controller.LoginController;
import com.sirekam.util.SwingUtils;
import com.sirekam.util.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JButton hubungiAdminButton;

    private static final String WHATSAPP_ADMIN_NOMOR = "081804751623";
    private static final String WHATSAPP_ADMIN_PESAN =
            "Halo, admin Klinik Stat Sehat. Saya ada keluhan aplikasi.";
    private User loggedInUser;
    private boolean loginSuccess;

    public LoginDialog(JFrame parent, String roleTitle) {
        super(parent, "Login - " + roleTitle, true);
        Theme.install();
        setUndecorated(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        setBounds(screen);
        initComponents(roleTitle);
    }

    private void initComponents(String roleTitle) {
        JPanel root = new JPanel(null);
        root.setBackground(Theme.BG);
        root.setLayout(new LayoutManager() {
            public void addLayoutComponent(String n, Component c) {}
            public void removeLayoutComponent(Component c) {}
            public Dimension preferredLayoutSize(Container c) { return new Dimension(1200, 700); }
            public Dimension minimumLayoutSize(Container c) { return new Dimension(600, 400); }
            public void layoutContainer(Container c) {
                int w = c.getWidth(), h = c.getHeight();
                int lw = (int) (w * 0.58);
                if (c.getComponentCount() > 1) {
                    c.getComponent(0).setBounds(0, 0, lw, h);
                    c.getComponent(1).setBounds(lw, 0, w - lw, h);
                }
            }
        });
        setContentPane(root);
        root.add(new BrandPanel());
        root.add(createFormSide(roleTitle));

        getRootPane().setDefaultButton(loginButton);
        getRootPane().registerKeyboardAction(
                e -> closeDialog(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private JPanel createFormSide(String roleTitle) {
        JPanel side = new JPanel(new GridBagLayout());
        side.setBackground(Theme.BG);

        Theme.Card card = new Theme.Card(null);
        card.setLayout(new GridBagLayout());
        card.setBorder(Theme.pad(48, 52, 48, 52));
        card.setPreferredSize(new Dimension(540, 630));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel heading = new JLabel("Login - " + roleTitle);
        heading.setFont(Theme.font(Font.BOLD, 32f));
        heading.setForeground(Theme.PRIMARY_DARK);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 34, 0);
        card.add(heading, gbc);

        JLabel userLabel = Theme.caption("Username:");
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(userLabel, gbc);

        usernameField = new JTextField();
        usernameField.setFont(Theme.font(Font.PLAIN, 16f));
        usernameField.setPreferredSize(new Dimension(200, 48));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 18, 0);
        card.add(usernameField, gbc);

        JLabel passLabel = Theme.caption("Password:");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(passLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setFont(Theme.font(Font.PLAIN, 16f));
        passwordField.setPreferredSize(new Dimension(200, 48));
        passwordField.putClientProperty("JPasswordField.showRevealButton", true);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 30, 0);
        card.add(passwordField, gbc);

        loginButton = Theme.button("Login", Theme.PRIMARY);
        loginButton.setFont(Theme.font(Font.BOLD, 15f));
        loginButton.setPreferredSize(new Dimension(200, 50));
        loginButton.addActionListener(e -> doLogin());
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(loginButton, gbc);

        cancelButton = Theme.outlineButton("Batal", Theme.MUTED);
        cancelButton.setFont(Theme.font(Font.BOLD, 14f));
        cancelButton.setPreferredSize(new Dimension(200, 48));
        cancelButton.addActionListener(e -> closeDialog());
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(cancelButton, gbc);

        hubungiAdminButton = Theme.outlineButton("💬 Hubungi Admin", new Color(0x25D366));
        hubungiAdminButton.setFont(Theme.font(Font.BOLD, 14f));
        hubungiAdminButton.setPreferredSize(new Dimension(200, 48));
        hubungiAdminButton.addActionListener(e -> hubungiAdmin());
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(hubungiAdminButton, gbc);

        side.add(card);
        return side;
    }

    /**
     * Membuka WhatsApp (web/app) menuju nomor admin klinik dengan
     * template pesan keluhan yang sudah terisi otomatis.
     */
    private void hubungiAdmin() {
        try {
            String pesanEncoded = URLEncoder.encode(WHATSAPP_ADMIN_PESAN, StandardCharsets.UTF_8);
            String url = "https://wa.me/62" + WHATSAPP_ADMIN_NOMOR.replaceFirst("^0", "") + "?text=" + pesanEncoded;
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                SwingUtils.showInfo(this, "Silakan hubungi admin melalui WhatsApp di nomor " + WHATSAPP_ADMIN_NOMOR);
            }
        } catch (IOException | java.net.URISyntaxException ex) {
            SwingUtils.showError(this, "Gagal membuka WhatsApp: " + ex.getMessage());
        }
    }

    public void closeDialog() {
        setVisible(false);
        dispose();
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

    // ============================================================
    // PANEL MEREK: gradient teal, pola salib, garis EKG bergerak
    // ============================================================
    private static class BrandPanel extends JPanel {
        private float phase = 0f;
        private Timer timer;

        BrandPanel() {
            setOpaque(true);
        }

        @Override public void addNotify() {
            super.addNotify();
            timer = new Timer(33, e -> {
                phase += 0.006f;
                if (phase > 1.3f) phase = -0.2f;
                repaint();
            });
            timer.start();
        }

        @Override public void removeNotify() {
            if (timer != null) timer.stop();
            super.removeNotify();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            int w = getWidth(), h = getHeight();

            g2.setPaint(new GradientPaint(0, 0, new Color(0x052F3A), w, h, new Color(0x0E8C99)));
            g2.fillRect(0, 0, w, h);

            // pola salib kecil
            g2.setComposite(AlphaComposite.SrcOver.derive(0.07f));
            for (int y = 30; y < h; y += 72) {
                for (int x = 30 + ((y / 72) % 2) * 36; x < w; x += 72) {
                    Theme.paintCross(g2, x, y, 16, Color.WHITE);
                }
            }
            g2.setComposite(AlphaComposite.SrcOver);

            // lingkaran besar lembut
            g2.setColor(new Color(255, 255, 255, 14));
            g2.fill(new Ellipse2D.Float(-w * 0.25f, h * 0.45f, w * 0.75f, w * 0.75f));
            g2.setColor(new Color(255, 255, 255, 12));
            g2.fill(new Ellipse2D.Float(w * 0.55f, -h * 0.20f, w * 0.60f, w * 0.60f));

            // lencana salib
            float cx = w / 2f, cy = h * 0.36f, s = Math.min(w, h) * 0.24f;
            g2.setColor(new Color(0, 0, 0, 40));
            g2.fill(new RoundRectangle2D.Float(cx - s / 2, cy - s / 2 + 10, s, s, s * 0.28f, s * 0.28f));
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(cx - s / 2, cy - s / 2, s, s, s * 0.28f, s * 0.28f));
            Theme.paintCross(g2, cx, cy, s * 0.56f, Theme.DANGER);

            // nama aplikasi
            g2.setFont(Theme.font(Font.BOLD, Math.max(40f, s * 0.42f)));
            g2.setColor(Color.WHITE);
            String name = "SIREKAM";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(name, cx - fm.stringWidth(name) / 2f, cy + s / 2f + fm.getAscent() + 26);

            // garis EKG
            float base = h * 0.80f, amp = h * 0.075f;
            Path2D ecg = Theme.ecgPath(0, w, base, amp);
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(255, 255, 255, 55));
            g2.draw(ecg);

            float hx = phase * w;
            Shape oldClip = g2.getClip();
            for (int i = 0; i < 3; i++) {
                float half = 120 + (2 - i) * 40;
                g2.setClip(new Rectangle((int) (hx - half * 2), 0, (int) (half * 2), h));
                g2.setStroke(new BasicStroke(3f + (2 - i) * 2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(150, 255, 235, i == 0 ? 40 : (i == 1 ? 70 : 230)));
                g2.draw(ecg);
            }
            g2.setClip(oldClip);
            g2.dispose();
        }
    }
}
