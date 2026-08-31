package com.sirekam.chat.client;

import com.sirekam.chat.util.ChatProtocol;
import com.sirekam.model.ChatMessage;
import com.sirekam.model.User;
import com.sirekam.model.enums.JenisChat;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ChatClient {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private User currentUser;
    private ChatClientGUI gui;

    private final ExecutorService messageListener =
            Executors.newSingleThreadExecutor();
    private volatile boolean connected = false;

    // Singleton
    private static ChatClient instance;

    private ChatClient() {}

    public static ChatClient getInstance() {
        if (instance == null) {
            instance = new ChatClient();
        }
        return instance;
    }

    public boolean connect(String serverAddress, int port, User user) {
        try {
            this.socket = new Socket(serverAddress, port);
            this.reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            this.writer = new PrintWriter(
                    socket.getOutputStream(), true
            );
            this.currentUser = user;
            this.connected = true;

            // Kirim login ke server
            writer.println("LOGIN|" + user.getIdUser());

            // Tunggu response
            String response = reader.readLine();
            if (response != null && response.startsWith("LOGIN_OK")) {
                System.out.println("✅ Connected to chat server as: " + user.getNamaLengkap());

                // Start message listener
                startMessageListener();
                return true;
            }

        } catch (IOException e) {
            System.err.println("❌ Failed to connect: " + e.getMessage());
        }
        return false;
    }

    private void startMessageListener() {
        messageListener.submit(() -> {
            String line;
            try {
                while (connected && (line = reader.readLine()) != null) {
                    handleIncomingMessage(line);
                }
            } catch (IOException e) {
                if (connected) {
                    System.err.println("⚠️ Connection lost: " + e.getMessage());
                    connected = false;
                    SwingUtilities.invokeLater(() -> {
                        if (gui != null) {
                            JOptionPane.showMessageDialog(gui,
                                    "Koneksi ke server terputus!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }
            }
        });
    }

    private void handleIncomingMessage(String line) {
        if (line.startsWith("ACK|")) {
            System.out.println("✅ Message delivered: " + line);
            return;
        }

        if (line.startsWith("ERROR|")) {
            System.err.println("❌ Server error: " + line);
            return;
        }

        // Pesan chat
        ChatMessage message = ChatProtocol.decode(line);
        if (message != null && gui != null) {
            SwingUtilities.invokeLater(() -> {
                gui.receiveMessage(message);
            });
        }
    }

    public boolean sendMessage(ChatMessage message) {
        if (!connected || writer == null) {
            return false;
        }

        try {
            String encoded = ChatProtocol.encode(message);
            writer.println(encoded);
            return true;
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            return false;
        }
    }

    public void setGUI(ChatClientGUI gui) {
        this.gui = gui;
    }

    public boolean isConnected() {
        return connected;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void disconnect() {
        connected = false;
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
        messageListener.shutdown();
        System.out.println("👋 Disconnected from chat server");
    }
}