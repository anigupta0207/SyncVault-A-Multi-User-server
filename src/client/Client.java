package client;

import java.io.*;
import java.net.*;

public class Client {

    public static void main(String[] args) {

        String serverIp = "100.93.142.78";
        int port = 5050;

        try {

            Socket socket = new Socket(serverIp, port);

            BufferedReader serverInput =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );

            PrintWriter serverOutput =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );

            BufferedReader keyboard =
                    new BufferedReader(
                            new InputStreamReader(
                                    System.in
                            )
                    );

            System.out.println("Connected to SyncVault Server.");

            /*
             * THREAD 1
             * Continuously listens for messages from server.
             */
            Thread receiverThread = new Thread(() -> {

                try {

                    String serverMessage;

                    while ((serverMessage = serverInput.readLine()) != null) {

                        System.out.println(
                                "\nSERVER: " + serverMessage
                        );

                        System.out.print("You: ");
                    }

                } catch (IOException e) {

                    System.out.println(
                            "Disconnected from server."
                    );
                }
            });

            receiverThread.start();

            /*
             * MAIN THREAD
             * Continuously sends messages to server.
             */
            while (true) {

                System.out.print("You: ");

                String message = keyboard.readLine();

                if (message == null) {
                    break;
                }

                serverOutput.println(message);

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }

            socket.close();

        } catch (IOException e) {

            System.out.println("Connection failed.");
            e.printStackTrace();
        }
    }
}