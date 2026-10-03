package server;

import Admin.AdminLoginService;
import auth.AuthenticatedUser;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Base64;

public class Server {
    private static final int PORT = 5050;
    private static final String TAB = String.valueOf('\t');

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("=================================");
            System.out.println("     SyncVault Server Started");
            System.out.println("=================================");
            System.out.println("Server IP: 100.93.142.78");
            System.out.println("Port: " + PORT);
            System.out.println("Waiting for clients...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new Thread(() -> handleClient(clientSocket), "syncvault-client").start();
            }
        } catch (IOException e) {
            System.err.println("Could not start SyncVault server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (Socket socket = clientSocket;
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {
            String firstMessage = input.readLine();
            if (firstMessage == null) return;
            if (firstMessage.startsWith("AUTH" + TAB)) {
                handleAuthentication(firstMessage, output);
                return;
            }

            // Preserve the original interactive chat flow for non-login clients.
            System.out.println("CLIENT: " + firstMessage);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            Thread receiverThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = input.readLine()) != null) {
                        System.out.println("CLIENT: " + message);
                        System.out.print("SERVER: ");
                    }
                } catch (IOException e) {
                    System.out.println("Client disconnected.");
                }
            }, "syncvault-chat-receiver");
            receiverThread.start();

            String message;
            while (true) {
                System.out.print("SERVER: ");
                message = keyboard.readLine();
                if (message == null) break;
                output.println(message);
                if (message.equalsIgnoreCase("exit")) break;
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        }
    }

    private static void handleAuthentication(String message, PrintWriter output) {
        try {
            String[] fields = message.split(TAB, -1);
            if (fields.length != 3) { output.println("AUTH_FAIL"); return; }
            String email = decode(fields[1]);
            String password = decode(fields[2]);
            AuthenticatedUser user = new AdminLoginService().authenticateUser(email, password);
            if (user == null) { output.println("AUTH_FAIL"); return; }
            output.println(String.join(TAB, "AUTH_OK", String.valueOf(user.getUserId()),
                    encode(user.getName()), encode(user.getEmail()), encode(user.getRole())));
        } catch (SQLException e) {
            System.err.println("The SyncVault server could not authenticate against its database.");
            output.println("DB_ERROR");
        } catch (IllegalArgumentException e) {
            output.println("AUTH_FAIL");
        }
    }

    private static String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
