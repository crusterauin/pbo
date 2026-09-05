package com.sirekam.chat.server;

import com.sirekam.chat.util.ChatProtocol;
import com.sirekam.dao.ChatDAO;
import com.sirekam.model.ChatMessage;
import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private int userId;
    private volatile boolean connected;

    private static final ChatDAO chatDAO = new ChatDAO();

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );
        this.writer = new PrintWriter(
                socket.getOutputStream(), true
        );
        this.connected = true;
        this.socket.setSoTimeout(30000);
    }

    @Override
    public void run() {
        try {
            // Step 1: Login (client kirim userId pertama)
            String firstLine = reader.readLine();
            if (firstLine != null && firstLine.startsWith("LOGIN|")) {
                String[] parts = firstLine.split("\\|");
                this.userId = Integer.parseInt(parts[1]);
                System.out.println("Client logged in as user: " + userId);
                writer.println("LOGIN_OK|" + userId);
            } else {
                System.out.println("Invalid login from: " +
                        socket.getRemoteSocketAddress());
                return;
            }

            // Step 2: Terus mendengarkan pesan dari client
            String inputLine;
            while (connected && (inputLine = reader.readLine()) != null) {
                processMessage(inputLine);
            }

        } catch (SocketTimeoutException e) {
            System.out.println("Client timeout: " + socket.getRemoteSocketAddress());
        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            close();
        }
    }

    private void processMessage(String inputLine) {
        try {
            // Decode pesan dari client
            ChatMessage message = ChatProtocol.decode(inputLine);
            if (message == null) {
                System.out.println("Invalid message format: " + inputLine);
                writer.println("ERROR|Invalid message format");
                return;
            }

            // Simpan ke database
            int chatId = chatDAO.insert(message);
            if (chatId > 0) {
                message.setIdChat(chatId);
                System.out.println("Message saved: " + message.getIsiPesan());

                // Kirim ke penerima (jika online)
                ChatServer.sendToClient(message.getIdPenerima(), inputLine);

                // Kirim konfirmasi ke pengirim
                writer.println("ACK|" + chatId);
            } else {
                writer.println("ERROR|Failed to save message");
            }

        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            writer.println("ERROR|" + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        if (connected && writer != null) {
            writer.println(message);
        }
    }

    public int getUserId() {
        return userId;
    }

    public void close() {
        connected = false;
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing client: " + e.getMessage());
        }
        System.out.println("Client disconnected: " + userId);
        ChatServer.removeClient(this);
    }
}