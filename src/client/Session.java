package client;

import auth.AuthenticatedUser;

public final class Session {

    private static AuthenticatedUser currentUser;

    private Session() {
    }

    public static void setUser(AuthenticatedUser user) {
        currentUser = user;
    }

    public static AuthenticatedUser getUser() {
        return currentUser;
    }

    public static String getToken() {

        if (currentUser == null) {
            return null;
        }

        return currentUser.getSessionToken();
    }

    public static boolean isLoggedIn() {
        return currentUser != null
                && currentUser.getSessionToken() != null;
    }

    public static boolean hasRole(String role) {

        return isLoggedIn()
                && currentUser.getRole().equalsIgnoreCase(role);
    }

    public static void clear() {
        currentUser = null;
    }
}