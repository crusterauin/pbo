package com.sirekam.util;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Collections;

/**
 * Tema visual rumah sakit (teal / putih / merah salib) untuk seluruh aplikasi SIREKAM.
 */
public final class Theme {

    private Theme() {}

    public static final Color PRIMARY       = new Color(0x0B6E7F);
    public static final Color PRIMARY_DARK  = new Color(0x073F4D);
    public static final Color PRIMARY_LIGHT = new Color(0xDDF0F2);
    public static final Color BG            = new Color(0xEEF5F6);
    public static final Color SURFACE       = Color.WHITE;
    public static final Color INK           = new Color(0x14313A);
    public static final Color MUTED         = new Color(0x5F7A82);
    public static final Color BORDER        = new Color(0xD0E1E4);
    public static final Color SUCCESS       = new Color(0x2E9E6B);
    public static final Color WARNING       = new Color(0xF0A93B);
    public static final Color DANGER        = new Color(0xD64550);
    public static final Color INFO          = new Color(0x2C7BB6);

    private static boolean installed = false;

    public static synchronized void install() {
        if (installed) return;
        FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", "#0B6E7F"));
        FlatLightLaf.setup();

        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 12);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("TextField.margin", new Insets(6, 10, 6, 10));
        UIManager.put("PasswordField.margin", new Insets(6, 10, 6, 10));
        UIManager.put("TextArea.margin", new Insets(8, 10, 8, 10));
        UIManager.put("ComboBox.padding", new Insets(5, 8, 5, 8));

        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.gridColor", new Color(0xE3EEF0));
        UIManager.put("Table.alternateRowColor", new Color(0xF6FAFB));
        UIManager.put("Table.selectionBackground", new Color(0xBFE3E7));
        UIManager.put("Table.selectionForeground", INK);
        UIManager.put("Table.selectionInactiveBackground", new Color(0xD3ECEF));
        UIManager.put("Table.selectionInactiveForeground", INK);
        UIManager.put("Table.cellMargins", new Insets(4, 10, 4, 10));
        UIManager.put("TableHeader.background", PRIMARY_LIGHT);
        UIManager.put("TableHeader.foreground", PRIMARY_DARK);
        UIManager.put("TableHeader.bottomSeparatorColor", PRIMARY);
        UIManager.put("TableHeader.separatorColor", PRIMARY_LIGHT);
        UIManager.put("TableHeader.cellMargins", new Insets(6, 10, 6, 10));

        UIManager.put("List.selectionBackground", new Color(0xBFE3E7));
        UIManager.put("List.selectionForeground", INK);

        UIManager.put("TabbedPane.tabHeight", 42);
        UIManager.put("TabbedPane.tabInsets", new Insets(6, 18, 6, 18));
        UIManager.put("TabbedPane.underlineColor", PRIMARY);
        UIManager.put("TabbedPane.selectedForeground", PRIMARY_DARK);
        UIManager.put("TabbedPane.inactiveUnderlineColor", PRIMARY);
        UIManager.put("TabbedPane.hoverColor", PRIMARY_LIGHT);
        UIManager.put("TabbedPane.focusColor", PRIMARY_LIGHT);
        UIManager.put("TabbedPane.tabSelectionHeight", 3);
        UIManager.put("TabbedPane.contentSeparatorHeight", 1);
        UIManager.put("TabbedPane.showTabSeparators", false);

        UIManager.put("SplitPane.dividerSize", 12);
        UIManager.put("SplitPaneDivider.gripColor", BG);

        installed = true;
    }

    // ------------------------------------------------------------------ fonts

    public static Font font(int style, float size) {
        Font base = UIManager.getFont("defaultFont");
        if (base == null) base = new JLabel().getFont();
        return base.deriveFont(style, size);
    }

    // ---------------------------------------------------------------- buttons

    public static JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(bg.equals(WARNING) ? INK : Color.WHITE);
        b.setFont(font(Font.BOLD, 13f));
        b.setFocusPainted(false);
        b.setMargin(new Insets(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.putClientProperty("JButton.buttonType", "roundRect");
        return b;
    }

    public static JButton outlineButton(String text, Color accent) {
        JButton b = new JButton(text);
        b.setBackground(Color.WHITE);
        b.setForeground(accent);
        b.setFont(font(Font.BOLD, 13f));
        b.setFocusPainted(false);
        b.setMargin(new Insets(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.putClientProperty("JButton.buttonType", "roundRect");
        b.putClientProperty("JButton.borderColor", accent);
        return b;
    }

    // ---------------------------------------------------------------- labels

    public static JLabel caption(String text) {
        JLabel l = new JLabel(text);
        l.setFont(font(Font.BOLD, 12f));
        l.setForeground(MUTED);
        return l;
    }

    public static JLabel value(String text) {
        JLabel l = new JLabel(text);
        l.setFont(font(Font.BOLD, 15f));
        l.setForeground(INK);
        return l;
    }

    public static void styleStatus(JLabel label) {
        label.setFont(font(Font.PLAIN, 12f));
        label.setForeground(MUTED);
    }

    /** Kotak informasi (banner) berlatar hijau-muda. */
    public static JLabel banner(String text) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_LIGHT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(PRIMARY);
                g2.fillRoundRect(0, 0, 5, getHeight(), 5, 5);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setOpaque(false);
        l.setFont(font(Font.BOLD, 13f));
        l.setForeground(PRIMARY_DARK);
        l.setBorder(BorderFactory.createEmptyBorder(11, 16, 11, 12));
        return l;
    }

    // ------------------------------------------------------------- containers

    public static Border titledBorder(String title) {
        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER, 1, true), title);
        tb.setTitleFont(font(Font.BOLD, 13f));
        tb.setTitleColor(PRIMARY_DARK);
        return tb;
    }

    public static Border pad(int t, int l, int b, int r) {
        return BorderFactory.createEmptyBorder(t, l, b, r);
    }

    /** Kartu putih berujung membulat dengan judul opsional. */
    public static class Card extends JPanel {
        public Card(String title) {
            super(new BorderLayout(0, 12));
            setOpaque(false);
            setBorder(pad(18, 18, 18, 18));
            if (title != null) {
                JLabel t = new JLabel(title);
                t.setFont(font(Font.BOLD, 16f));
                t.setForeground(PRIMARY_DARK);
                t.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
                add(t, BorderLayout.NORTH);
            }
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(SURFACE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 18, 18));
            g2.setColor(BORDER);
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 18, 18));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // -------------------------------------------------------- table / tabs etc.

    public static void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFont(font(Font.PLAIN, 13f));
        JTableHeader h = table.getTableHeader();
        h.setFont(font(Font.BOLD, 13f));
        h.setReorderingAllowed(false);
        if (h.getDefaultRenderer() instanceof javax.swing.table.DefaultTableCellRenderer) {
            ((javax.swing.table.DefaultTableCellRenderer) h.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
        }
        h.setPreferredSize(new Dimension(h.getPreferredSize().width, 40));
    }

    public static void columnWidths(JTable table, int... widths) {
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    public static void styleScroll(JScrollPane sp) {
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        sp.getViewport().setBackground(Color.WHITE);
    }

    public static void styleList(JList<?> list) {
        list.setFixedCellHeight(38);
        list.setFont(font(Font.PLAIN, 13f));
    }

    public static void styleTabs(JTabbedPane tabs) {
        tabs.setFont(font(Font.BOLD, 13f));
        tabs.setBackground(Color.WHITE);
        tabs.setBorder(BorderFactory.createEmptyBorder());
    }

    public static void styleSplit(JSplitPane split) {
        split.setBorder(null);
        split.setBackground(BG);
        split.setOpaque(false);
        split.setContinuousLayout(true);
        split.setDividerSize(14);
    }

    /** Area teks read-only bergaya kolom informasi. */
    public static void styleReadOnly(JTextArea area) {
        area.setEditable(false);
        area.setBackground(new Color(0xF3F8F9));
        area.setForeground(INK);
        area.setFont(font(Font.PLAIN, 14f));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
    }

    // ----------------------------------------------------------------- header

    /** Bar judul dashboard: gradient, lambang salib, dan garis EKG. */
    public static JPanel header(String title, String userInfo, Color from, Color to) {
        JPanel bar = new JPanel(new BorderLayout(16, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setPaint(new GradientPaint(0, 0, from, w, h, to));
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 20, 20));

                // lingkaran lembut
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(w - 260, -90, 260, 260);
                g2.fillOval(w - 120, h - 60, 170, 170);

                // garis EKG
                Path2D ecg = ecgPath(w * 0.38f, w * 0.80f, h * 0.62f, h * 0.30f);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(255, 255, 255, 60));
                g2.draw(ecg);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(pad(14, 22, 14, 26));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);
        left.add(new CrossBadge(46));
        JLabel t = new JLabel(title);
        t.setFont(font(Font.BOLD, 26f));
        t.setForeground(Color.WHITE);
        left.add(t);

        JLabel u = new JLabel(userInfo);
        u.setFont(font(Font.BOLD, 14f));
        u.setForeground(Color.WHITE);

        bar.add(left, BorderLayout.WEST);
        bar.add(u, BorderLayout.EAST);
        return bar;
    }

    /** Membuat jalur denyut EKG dari x0 sampai x1. */
    public static Path2D ecgPath(float x0, float x1, float base, float amp) {
        Path2D p = new Path2D.Float();
        float span = 150f;
        p.moveTo(x0, base);
        float x = x0;
        while (x < x1) {
            float e = Math.min(x + span, x1);
            float u = e - x;
            p.lineTo(x + u * 0.30f, base);
            p.lineTo(x + u * 0.36f, base - amp * 0.35f);
            p.lineTo(x + u * 0.42f, base);
            p.lineTo(x + u * 0.48f, base + amp * 0.30f);
            p.lineTo(x + u * 0.55f, base - amp * 1.9f);
            p.lineTo(x + u * 0.62f, base + amp * 0.55f);
            p.lineTo(x + u * 0.68f, base);
            p.lineTo(x + u * 0.82f, base - amp * 0.45f);
            p.lineTo(x + u * 0.92f, base);
            p.lineTo(e, base);
            x = e;
        }
        return p;
    }

    /** Lencana salib putih di dalam kotak membulat. */
    public static class CrossBadge extends JComponent {
        private final int size;
        public CrossBadge(int size) {
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, size, size, size * 0.30f, size * 0.30f));
            paintCross(g2, size / 2f, size / 2f, size * 0.56f, DANGER);
            g2.dispose();
        }
    }

    public static void paintCross(Graphics2D g2, float cx, float cy, float s, Color c) {
        float arm = s * 0.34f;
        g2.setColor(c);
        g2.fill(new RoundRectangle2D.Float(cx - arm / 2, cy - s / 2, arm, s, arm * 0.35f, arm * 0.35f));
        g2.fill(new RoundRectangle2D.Float(cx - s / 2, cy - arm / 2, s, arm, arm * 0.35f, arm * 0.35f));
    }
}
