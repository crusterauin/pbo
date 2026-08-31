package com.sirekam.chat.client;

import com.sirekam.model.ChatMessage;
import com.sirekam.model.User;
import com.sirekam.model.enums.JenisChat;
import com.sirekam.model.enums.StatusBaca;
import com.sirekam.controller.ChatController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatClientGUI extends JPanel {

    private User currentUser;
    private ChatClient chatClient;
    private ChatController chatController;

    private JPanel chatHistoryPanel;
    private JTextField messageField;
    private JButton sendButton;
    private JLabel statusLabel;

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private Timer pollingTimer;
    private boolean useSocket = true;

    private int receiverId;
    private JenisChat jenisChat;
    private String partnerName;

    public ChatClientGUI(User user, JenisChat jenisChat, int receiverId, String partnerName) {
        this.currentUser = user;
        this.jenisChat = jenisChat;
        this.receiverId = receiverId;
        this.partnerName = partnerName;
        this.chatController = new ChatController();
        this.chatClient = ChatClient.getInstance();
        this.chatClient.setGUI(this);

        initComponents();

        // Coba koneksi ke chat server
        if (!chatClient.connect("127.0.0.1", 6789, user)) {
            useSocket = false;
            startPolling();
            JOptionPane.showMessageDialog(this,
                    "Chat server tidak tersedia. Menggunakan mode polling.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        loadChatHistory();
        setupAutoRefresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // ============ TOP PANEL ============
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("💬 Chat dengan " + partnerName));

        JLabel partnerLabel = new JLabel("Partner: " + partnerName);
        partnerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(partnerLabel, BorderLayout.WEST);

        statusLabel = new JLabel("🟢 Online");
        statusLabel.setForeground(Color.GREEN);
        topPanel.add(statusLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ============ CHAT HISTORY ============
        chatHistoryPanel = new JPanel();
        chatHistoryPanel.setLayout(new BoxLayout(chatHistoryPanel, BoxLayout.Y_AXIS));
        chatHistoryPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(chatHistoryPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // ============ BOTTOM PANEL ============
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

        messageField = new JTextField();
        messageField.addActionListener(e -> sendMessage());

        sendButton = new JButton("📤 Kirim");
        sendButton.setBackground(new Color(41, 128, 185));
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(e -> sendMessage());

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadChatHistory());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(refreshBtn);
        btnPanel.add(sendButton);

        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadChatHistory() {
        try {
            List<ChatMessage> messages = chatController.getPesanByPengirim(currentUser.getIdUser());
            List<ChatMessage> received = chatController.getPesanByPenerima(currentUser.getIdUser());

            if (jenisChat == JenisChat.PETUGAS_DOKTER) {
                messages.removeIf(m -> m.getJenisChat() != JenisChat.PETUGAS_DOKTER);
                received.removeIf(m -> m.getJenisChat() != JenisChat.PETUGAS_DOKTER);
            } else {
                messages.removeIf(m -> m.getJenisChat() != JenisChat.DOKTER_APOTEKER);
                received.removeIf(m -> m.getJenisChat() != JenisChat.DOKTER_APOTEKER);
            }

            messages.addAll(received);
            messages.removeIf(m -> (m.getIdPengirim() != receiverId && m.getIdPenerima() != receiverId));
            messages.sort((a, b) -> a.getWaktuKirim().compareTo(b.getWaktuKirim()));

            updateChatHistory(messages);

        } catch (SQLException e) {
            System.err.println("Error loading chat: " + e.getMessage());
            showDummyMessages();
        }
    }

    private void updateChatHistory(List<ChatMessage> messages) {
        chatHistoryPanel.removeAll();

        if (messages == null || messages.isEmpty()) {
            JLabel emptyLabel = new JLabel("Belum ada pesan dengan " + partnerName + ". Kirim pesan sekarang!");
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setBorder(new EmptyBorder(20, 10, 20, 10));
            chatHistoryPanel.add(emptyLabel);
        } else {
            for (ChatMessage msg : messages) {
                addMessageBubble(msg);
            }
        }

        chatHistoryPanel.revalidate();
        chatHistoryPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollPane scrollPane = (JScrollPane)
                    SwingUtilities.getAncestorOfClass(JScrollPane.class, chatHistoryPanel);
            if (scrollPane != null) {
                scrollPane.getVerticalScrollBar().setValue(
                        scrollPane.getVerticalScrollBar().getMaximum()
                );
            }
        });
    }

    private void addMessageBubble(ChatMessage message) {
        JPanel bubblePanel = new JPanel();
        bubblePanel.setLayout(new BoxLayout(bubblePanel, BoxLayout.Y_AXIS));
        bubblePanel.setBorder(new EmptyBorder(3, 5, 3, 5));

        boolean isMine = message.getIdPengirim() == currentUser.getIdUser();

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(isMine ? new Color(0xDC, 0xF8, 0xFF) : Color.WHITE);
        messagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        messagePanel.setMaximumSize(new Dimension(400, 100));

        String senderName = isMine ? "👤 Saya" : "👤 " + message.getNamaPengirim();
        if (senderName.equals("👤 null") || senderName.equals("👤 ")) {
            senderName = "👤 User " + message.getIdPengirim();
        }

        JLabel senderLabel = new JLabel(senderName);
        senderLabel.setFont(new Font("Arial", Font.BOLD, 11));
        senderLabel.setForeground(Color.GRAY);
        messagePanel.add(senderLabel, BorderLayout.NORTH);

        JTextArea msgArea = new JTextArea(message.getIsiPesan());
        msgArea.setEditable(false);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setBackground(messagePanel.getBackground());
        msgArea.setBorder(null);
        msgArea.setFont(new Font("Arial", Font.PLAIN, 13));
        int height = Math.min(60, msgArea.getText().length() / 3 * 15 + 10);
        msgArea.setPreferredSize(new Dimension(300, height));
        messagePanel.add(msgArea, BorderLayout.CENTER);

        JLabel timeLabel = new JLabel(
                message.getWaktuKirim() != null ?
                        message.getWaktuKirim().format(timeFormatter) : ""
        );
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        timeLabel.setForeground(Color.GRAY);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timePanel.setBackground(messagePanel.getBackground());
        timePanel.add(timeLabel);
        messagePanel.add(timePanel, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new FlowLayout(
                isMine ? FlowLayout.RIGHT : FlowLayout.LEFT
        ));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(messagePanel);

        bubblePanel.add(wrapper);
        chatHistoryPanel.add(bubblePanel);
    }

    private void sendMessage() {
        String text = messageField.getText().trim();
        if (text.isEmpty()) {
            return;
        }

        if (receiverId == 0) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada penerima!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        ChatMessage message = new ChatMessage();
        message.setJenisChat(jenisChat);
        message.setIdKunjungan(null);
        message.setIdResep(null);
        message.setIdPengirim(currentUser.getIdUser());
        message.setIdPenerima(receiverId);
        message.setIsiPesan(text);
        message.setStatusBaca(StatusBaca.TERKIRIM);

        boolean sent = false;

        if (useSocket) {
            sent = chatClient.sendMessage(message);
        }

        if (!sent) {
            try {
                sent = chatController.kirimPesan(message);
            } catch (SQLException e) {
                System.err.println("Error saving message: " + e.getMessage());
            }
        }

        if (sent) {
            messageField.setText("");
            message.setNamaPengirim(currentUser.getNamaLengkap());
            addMessageBubble(message);
            chatHistoryPanel.revalidate();
            chatHistoryPanel.repaint();

            SwingUtilities.invokeLater(() -> {
                JScrollPane scrollPane = (JScrollPane)
                        SwingUtilities.getAncestorOfClass(JScrollPane.class, chatHistoryPanel);
                if (scrollPane != null) {
                    scrollPane.getVerticalScrollBar().setValue(
                            scrollPane.getVerticalScrollBar().getMaximum()
                    );
                }
            });
        } else {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengirim pesan!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startPolling() {
        pollingTimer = new Timer(3000, e -> {
            try {
                int unread = chatController.getJumlahPesanBaru(currentUser.getIdUser());
                if (unread > 0) {
                    statusLabel.setText("🔴 " + unread + " pesan baru");
                    statusLabel.setForeground(Color.RED);
                    loadChatHistory();
                } else {
                    statusLabel.setText("🟢 Online (polling)");
                    statusLabel.setForeground(Color.GREEN);
                }
            } catch (SQLException ex) {
                System.err.println("Polling error: " + ex.getMessage());
            }
        });
        pollingTimer.start();
    }

    private void setupAutoRefresh() {
        Timer refreshTimer = new Timer(5000, e -> {
            if (chatClient.isConnected() || !useSocket) {
                loadChatHistory();
            }
        });
        refreshTimer.start();
    }

    public void receiveMessage(ChatMessage message) {
        if (message.getJenisChat() != jenisChat) {
            return;
        }
        if (message.getIdPengirim() != receiverId && message.getIdPenerima() != receiverId) {
            return;
        }

        if (message.getNamaPengirim() == null || message.getNamaPengirim().isEmpty()) {
            message.setNamaPengirim("User " + message.getIdPengirim());
        }

        addMessageBubble(message);
        chatHistoryPanel.revalidate();
        chatHistoryPanel.repaint();

        try {
            chatController.tandaiDibaca(message.getIdChat());
        } catch (SQLException e) {
            System.err.println("Error marking read: " + e.getMessage());
        }

        statusLabel.setText("🔵 Pesan baru diterima");
        statusLabel.setForeground(new Color(52, 152, 219));

        Timer resetTimer = new Timer(3000, ev -> {
            statusLabel.setText("🟢 Online");
            statusLabel.setForeground(Color.GREEN);
        });
        resetTimer.setRepeats(false);
        resetTimer.start();

        SwingUtilities.invokeLater(() -> {
            JScrollPane scrollPane = (JScrollPane)
                    SwingUtilities.getAncestorOfClass(JScrollPane.class, chatHistoryPanel);
            if (scrollPane != null) {
                scrollPane.getVerticalScrollBar().setValue(
                        scrollPane.getVerticalScrollBar().getMaximum()
                );
            }
        });
    }

    private void showDummyMessages() {
        chatHistoryPanel.removeAll();

        JPanel bubblePanel = new JPanel();
        bubblePanel.setLayout(new BoxLayout(bubblePanel, BoxLayout.Y_AXIS));
        bubblePanel.setBorder(new EmptyBorder(3, 5, 3, 5));

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(new Color(240, 240, 240));
        messagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        messagePanel.setMaximumSize(new Dimension(400, 60));

        JLabel senderLabel = new JLabel("🤖 Sistem");
        senderLabel.setFont(new Font("Arial", Font.BOLD, 11));
        senderLabel.setForeground(Color.GRAY);
        messagePanel.add(senderLabel, BorderLayout.NORTH);

        JLabel msgLabel = new JLabel("Selamat datang di Chat SIREKAM! Kirim pesan untuk memulai percakapan.");
        msgLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        messagePanel.add(msgLabel, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(messagePanel);

        bubblePanel.add(wrapper);
        chatHistoryPanel.add(bubblePanel);

        chatHistoryPanel.revalidate();
        chatHistoryPanel.repaint();
    }

    public void cleanup() {
        if (pollingTimer != null) {
            pollingTimer.stop();
        }
        chatClient.disconnect();
    }
}