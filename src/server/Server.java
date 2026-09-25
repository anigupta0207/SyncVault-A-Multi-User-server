package server;

import java.io.*;
import java.net.*;

public class
Server {

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

                new Thread(() -> handleClient(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {

        try {

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
                    );

            BufferedReader keyboard =
                    new BufferedReader(
                            new InputStreamReader(System.in)
                    );

            // SERVER RECEIVER THREAD
            Thread receiverThread = new Thread(() -> {

                try {

                    String message;

                    while ((message = input.readLine()) != null) {

                        System.out.println(
                                "\nCLIENT: " + message
                        );

                        System.out.print("SERVER: ");
                    }

                } catch (IOException e) {

                    System.out.println(
                            "Client disconnected."
                    );
                }
            });

            receiverThread.start();

            // SERVER SENDER
            while (true) {

                System.out.print("SERVER: ");

                String message = keyboard.readLine();

                if (message == null) {
                    break;
                }

                output.println(message);

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }

            clientSocket.close();

        } catch (IOException e) {

            System.out.println(
                    "Connection error: "
                            + e.getMessage()
            );
        }
    }
}