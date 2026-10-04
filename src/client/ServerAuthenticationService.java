package client;

import auth.AuthenticatedUser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Sends login details to the SyncVault server; MySQL credentials stay server-side. */
public class ServerAuthenticationService {
    private static final String DEFAULT_HOST = "100.93.142.78";
    private static final int DEFAULT_PORT = 5050;

    public AuthenticatedUser authenticate(String email, String password) throws IOException {
        String host = System.getenv().getOrDefault("SYNCVAULT_SERVER_HOST", DEFAULT_HOST);
        int port;
        try {
            port = Integer.parseInt(System.getenv().getOrDefault("SYNCVAULT_SERVER_PORT", String.valueOf(DEFAULT_PORT)));
        } catch (NumberFormatException e) {
            throw new IOException("SYNCVAULT_SERVER_PORT must be a valid port number.", e);
        }

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 5000);
            socket.setSoTimeout(7000);
            try (PrintWriter output = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
                 BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                output.println("AUTH\t" + encode(email) + "\t" + encode(password));
                String response = input.readLine();
                if (response == null) throw new IOException("The SyncVault server closed the connection without a response.");
                String[] fields = response.split("\\t", -1);
                if (fields[0].equals("AUTH_FAIL")) return null;
                if (fields[0].equals("DB_ERROR")) throw new IOException("The SyncVault server could not access its MySQL database.");
                if (fields.length != 6 || !fields[0].equals("AUTH_OK")) {
                    throw new IOException("The SyncVault server returned an unsupported login response.");
                }
                return new AuthenticatedUser(
                        Integer.parseInt(fields[1]),
                        decode(fields[2]),
                        decode(fields[3]),
                        decode(fields[4]),
                        decode(fields[5])
                );}
        }
    }

    private static String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) throws IOException {
        try { return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8); }
        catch (IllegalArgumentException e) { throw new IOException("The server sent invalid login data.", e); }
    }
}
