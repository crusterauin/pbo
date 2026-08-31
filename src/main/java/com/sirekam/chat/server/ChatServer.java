package com.sirekam.chat.server;

import com.sirekam.chat.util.ChatProtocol;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {

    private static final int PORT = 6789;
    private static volatile boolean running = true;
    private static final List<ClientHandler> clients =
            Collections.synchronizedList(new ArrayList<>());
    private static final ExecutorService threadPool =
            Executors.newCachedThreadPool();

    public static void main(String[] args) {
        System.out.println("🚀 CHAT SERVER SIREKAM STARTING...");
        System.out.println("📡 Listening on port: " + PORT);
        System.out.println("=========================================");
        System.out.println("🔹 Using: Socket + Multithreading");
        System.out.println("=========================================");

        // Thread untuk monitoring server
        Thread monitorThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (running) {
                String cmd = scanner.nextLine();
                if (cmd.equalsIgnoreCase("quit") || cmd.equalsIgnoreCase("exit")) {
                    System.out.println("🛑 Shutting down server...");
                    running = false;
                    shutdown();
                    break;
                } else if (cmd.equalsIgnoreCase("status")) {
                    System.out.println("📊 Connected clients: " + clients.size());
                    synchronized (clients) {
                        for (ClientHandler c : clients) {
                            System.out.println("   - User ID: " + c.getUserId());
                        }
                    }
                } else if (cmd.equalsIgnoreCase("help")) {
                    System.out.println("Commands: status, quit, exit, help");
                }
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.start();

        // Main loop - menerima koneksi
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("✅ New client connected: " +
                            clientSocket.getRemoteSocketAddress());

                    ClientHandler handler = new ClientHandler(clientSocket);
                    clients.add(handler);
                    threadPool.submit(handler);

                } catch (SocketException e) {
                    if (running) {
                        System.err.println("Socket error: " + e.getMessage());
                    }
                } catch (IOException e) {
                    if (running) {
                        System.err.println("IO error: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }

        System.out.println("👋 Chat Server stopped.");
    }

    private static void shutdown() {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.close();
            }
            clients.clear();
        }
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }

    public static void broadcast(String message, int senderId) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client.getUserId() != senderId) {
                    client.sendMessage(message);
                }
            }
        }
    }

    // ============ METHOD INI YANG DIPANGGIL CLIENTHANDLER ============
    public static void sendToClient(int receiverId, String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client.getUserId() == receiverId) {
                    client.sendMessage(message);
                    return;
                }
            }
        }
    }
    // ==================================================================

    public static void removeClient(ClientHandler handler) {
        clients.remove(handler);
        System.out.println("👋 Client removed: " + handler.getUserId());
    }
}