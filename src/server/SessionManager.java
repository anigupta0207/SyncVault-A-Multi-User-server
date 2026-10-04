package server;

import auth.AuthenticatedUser;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager {

    private static final ConcurrentHashMap<String, AuthenticatedUser> sessions =
            new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static String createSession(AuthenticatedUser user) {

        String token = UUID.randomUUID().toString();

        sessions.put(token, user);

        return token;
    }

    public static AuthenticatedUser getUser(String token) {

        if (token == null || token.isBlank()) {
            return null;
        }

        return sessions.get(token);
    }

    public static void removeSession(String token) {

        if (token != null) {
            sessions.remove(token);
        }
    }

    public static boolean isValid(String token) {

        return getUser(token) != null;
    }
}