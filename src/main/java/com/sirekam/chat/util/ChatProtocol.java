package com.sirekam.chat.util;

import com.sirekam.model.ChatMessage;
import com.sirekam.model.enums.JenisChat;
import com.sirekam.model.enums.StatusBaca;
import java.time.LocalDateTime;

public class ChatProtocol {

    public static final String DELIMITER = "|";

    public static String encode(ChatMessage message) {
        return String.join(DELIMITER,
                "CHAT",
                message.getJenisChat().getValue(),
                message.getIdKunjungan() != null ? String.valueOf(message.getIdKunjungan()) : "null",
                message.getIdResep() != null ? String.valueOf(message.getIdResep()) : "null",
                String.valueOf(message.getIdPengirim()),
                String.valueOf(message.getIdPenerima()),
                message.getIsiPesan()
        );
    }

    public static ChatMessage decode(String data) {
        String[] parts = data.split("\\" + DELIMITER);
        if (parts.length < 7 || !parts[0].equals("CHAT")) {
            return null;
        }

        ChatMessage message = new ChatMessage();
        message.setJenisChat(JenisChat.valueOf(parts[1].toUpperCase()));

        if (!parts[2].equals("null")) {
            message.setIdKunjungan(Integer.parseInt(parts[2]));
        }
        if (!parts[3].equals("null")) {
            message.setIdResep(Integer.parseInt(parts[3]));
        }
        message.setIdPengirim(Integer.parseInt(parts[4]));
        message.setIdPenerima(Integer.parseInt(parts[5]));
        message.setIsiPesan(parts[6]);
        message.setWaktuKirim(LocalDateTime.now());
        message.setStatusBaca(StatusBaca.TERKIRIM);

        return message;
    }

    public static String encodeAck(int chatId) {
        return "ACK|" + chatId;
    }

    public static String encodeError(String message) {
        return "ERROR|" + message;
    }

    public static String encodeLogin(int userId) {
        return "LOGIN|" + userId;
    }

    public static String encodeLoginOk(int userId) {
        return "LOGIN_OK|" + userId;
    }

    public static boolean isAck(String data) {
        return data != null && data.startsWith("ACK|");
    }

    public static boolean isError(String data) {
        return data != null && data.startsWith("ERROR|");
    }

    public static boolean isLoginOk(String data) {
        return data != null && data.startsWith("LOGIN_OK|");
    }

    public static int getChatIdFromAck(String data) {
        if (isAck(data)) {
            try {
                return Integer.parseInt(data.substring(4));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }
}