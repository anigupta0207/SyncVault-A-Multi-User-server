package auth;

/** User details returned after the server authenticates a SyncVault account. */
public final class AuthenticatedUser {
    private final int userId;
    private final String name;
    private final String email;
    private final String role;

    public AuthenticatedUser(int userId, String name, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
