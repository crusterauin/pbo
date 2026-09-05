package com.sirekam.launcher;

import com.sirekam.client.petugas.PetugasApp;
import com.sirekam.client.dokter.DokterApp;
import com.sirekam.client.apoteker.ApotekerApp;
import javax.swing.*;
import java.awt.*;

public class SIREKAMLauncher extends JFrame {

    private JFrame petugasFrame = null;
    private JFrame dokterFrame = null;
    private JFrame apotekerFrame = null;

    // Menyimpan referensi label pada setiap card agar ukuran font
    // dapat menyesuaikan (responsive) saat window di-maximize / di-resize
    private final java.util.List<JLabel> cardTitleLabels = new java.util.ArrayList<>();
    private final java.util.List<JLabel> cardSubLabels = new java.util.ArrayList<>();
    private final java.util.List<JLabel> cardDescLabels = new java.util.ArrayList<>();

    // Lebar acuan (base width) saat ukuran font awal (16 / 11 / 11) ditentukan
    private static final int BASE_WIDTH = 800;

    public SIREKAMLauncher() {
        setTitle("SIREKAM - Sistem Informasi Rekam Medis");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(" SISTEM INFORMASI REKAM MEDIS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel versionLabel = new JLabel("v1.0");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(200, 200, 200));
        headerPanel.add(versionLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Card Panel
        JPanel cardPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        cardPanel.setBackground(new Color(240, 244, 248));

        cardPanel.add(createRoleCard("PETUGAS", "Pendaftaran & Administrasi",
                "Mendaftarkan pasien, mencatat keluhan, melihat riwayat rekam medis, dan assign dokter",
                new Color(52, 152, 219),
                e -> openPetugasApp()
        ));

        cardPanel.add(createRoleCard("DOKTER", "Pelayanan Medis",
                "Melihat pasien ditugaskan, menulis resep, koordinasi dengan Petugas dan Apoteker",
                new Color(46, 204, 113),
                e -> openDokterApp()
        ));

        cardPanel.add(createRoleCard(
                "APOTEKER", "Farmasi & Pembayaran",
                "Mengelola obat, assign obat, dan cetak struk pembayaran",
                new Color(155, 89, 182),
                e -> openApotekerApp()
        ));

        add(cardPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(240, 244, 248));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        JLabel footerLabel = new JLabel("© 2026 SIREKAM | Klinik Stat Sehat");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        footerLabel.setForeground(Color.GRAY);
        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);

        // Listener untuk menyesuaikan ukuran font saat frame di-resize / di-maximize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                adjustFontSizes();
            }
        });
    }

    /**
     * Menyesuaikan ukuran font judul, subjudul, dan deskripsi pada seluruh
     * panel card berdasarkan lebar frame saat ini dibandingkan lebar acuan.
     * Dipanggil otomatis setiap kali frame di-resize (termasuk saat maximize).
     */
    private void adjustFontSizes() {
        double scale = getWidth() / (double) BASE_WIDTH;
        // Batasi skala agar font tidak terlalu kecil atau terlalu besar
        scale = Math.max(0.85, Math.min(scale, 1.9));

        int titleSize = (int) Math.round(16 * scale);
        int subSize = (int) Math.round(11 * scale);
        int descSize = (int) Math.round(11 * scale);

        for (JLabel l : cardTitleLabels) {
            l.setFont(new Font("Arial", Font.BOLD, titleSize));
        }
        for (JLabel l : cardSubLabels) {
            l.setFont(new Font("Arial", Font.PLAIN, subSize));
        }
        for (JLabel l : cardDescLabels) {
            l.setFont(new Font("Arial", Font.PLAIN, descSize));
        }

        revalidate();
        repaint();
    }

    /**
     * Menghasilkan versi "soft" (pastel) dari sebuah warna dengan mencampurnya
     * dengan putih. whiteAmount 0.0 = warna asli, 1.0 = putih penuh.
     */
    private Color softenColor(Color base, float whiteAmount) {
        int r = (int) (base.getRed() * (1 - whiteAmount) + 255 * whiteAmount);
        int g = (int) (base.getGreen() * (1 - whiteAmount) + 255 * whiteAmount);
        int b = (int) (base.getBlue() * (1 - whiteAmount) + 255 * whiteAmount);
        return new Color(r, g, b);
    }

    private JPanel createRoleCard(String title, String subtitle,
                                  String description, Color color,
                                  java.awt.event.ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));
        card.setBackground(Color.WHITE);

        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        iconPanel.setBackground(Color.WHITE);
        card.add(iconPanel, BorderLayout.NORTH);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        subLabel.setForeground(Color.GRAY);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        textPanel.add(subLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        textPanel.add(descLabel);
        card.add(textPanel, BorderLayout.CENTER);

        // Daftarkan label ke list agar ukuran fontnya ikut menyesuaikan
        // ketika window di-resize / di-maximize
        cardTitleLabels.add(titleLabel);
        cardSubLabels.add(subLabel);
        cardDescLabels.add(descLabel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.WHITE);
        JButton btn = new JButton("Buka " + title);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        btnPanel.add(btn);
        card.add(btnPanel, BorderLayout.SOUTH);

        // Warna soft (pastel) hasil campuran warna role dengan putih,
        // dipakai sebagai latar saat card disentuh/di-hover
        final Color softHoverColor = softenColor(color, 0.85f);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(softHoverColor);
                iconPanel.setBackground(softHoverColor);
                textPanel.setBackground(softHoverColor);
                btnPanel.setBackground(softHoverColor);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color, 2),
                        BorderFactory.createEmptyBorder(20, 15, 20, 15)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
                iconPanel.setBackground(Color.WHITE);
                textPanel.setBackground(Color.WHITE);
                btnPanel.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(20, 15, 20, 15)
                ));
            }
        });

        return card;
    }

    private void openPetugasApp() {
        if (petugasFrame == null || !petugasFrame.isVisible()) {
            petugasFrame = new PetugasApp();
            petugasFrame.setVisible(true);
            petugasFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosed(java.awt.event.WindowEvent e) {
                    petugasFrame = null;
                }
            });
        } else {
            petugasFrame.toFront();
        }
    }

    private void openDokterApp() {
        if (dokterFrame == null || !dokterFrame.isVisible()) {
            dokterFrame = new DokterApp();
            dokterFrame.setVisible(true);
            dokterFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosed(java.awt.event.WindowEvent e) {
                    dokterFrame = null;
                }
            });
        } else {
            dokterFrame.toFront();
        }
    }

    private void openApotekerApp() {
        if (apotekerFrame == null || !apotekerFrame.isVisible()) {
            apotekerFrame = new ApotekerApp();
            apotekerFrame.setVisible(true);
            apotekerFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosed(java.awt.event.WindowEvent e) {
                    apotekerFrame = null;
                }
            });
        } else {
            apotekerFrame.toFront();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SIREKAMLauncher().setVisible(true);
        });
    }
}




