package server;

import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) {

        int port = 5050;

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("=================================");
            System.out.println("     SyncVault Server Started");
            System.out.println("=================================");
            System.out.println("Server IP: 100.93.142.78");
            System.out.println("Port: " + port);
            System.out.println("Waiting for clients...\n");

            while (true) {

                Socket clientSocket = serverSocket.accept();

                System.out.println(
                        "Client connected: "
                                + clientSocket.getInetAddress()
                );

                handleClient(clientSocket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {

        try (
                BufferedReader input =
                        new BufferedReader(
                                new InputStreamReader(
                                        clientSocket.getInputStream()
                                )
                        );

                PrintWriter output =
                        new PrintWriter(
                                clientSocket.getOutputStream(),
                                true
                        )
        ) {

            output.println("Connected to SyncVault Server.");
            output.println("Type 'exit' to disconnect.");

            String message;

            while ((message = input.readLine()) != null) {

                System.out.println(
                        "Client [" +
                                clientSocket.getInetAddress() +
                                "] : " +
                                message
                );

                if (message.equalsIgnoreCase("exit")) {

                    output.println("Disconnected from SyncVault Server.");
                    break;
                }

                // Temporary response
                output.println("Server received: " + message);
            }

            System.out.println(
                    "Client disconnected: "
                            + clientSocket.getInetAddress()
            );

        } catch (IOException e) {

            System.out.println("Client connection error: "
                    + e.getMessage());

        } finally {

            try {
                clientSocket.close();
            } catch (IOException ignored) {
            }
        }
    }
}