package server;

import auth.AuthenticatedUser;

public final class Authorization {

    private Authorization() {
    }

    public static AuthenticatedUser requireAuthentication(
            String token
    ) {

        AuthenticatedUser user =
                SessionManager.getUser(token);

        if (user == null) {
            return null;
        }

        return user;
    }

    public static boolean hasRole(
            AuthenticatedUser user,
            String role
    ) {

        return user != null
                && user.getRole().equalsIgnoreCase(role);
    }
}