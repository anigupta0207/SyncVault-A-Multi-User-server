package Admin;

import auth.AuthenticatedUser;
import dashboard.DashboardShell;

import javax.swing.*;
import java.util.function.Function;

public class AdminDashboard
        extends DashboardShell {

    public AdminDashboard(
            AuthenticatedUser user,
            Function<String, JPanel> pageRenderer,
            Runnable logoutAction
    ) {

        super(
                user.getName(),
                user.getEmail(),
                "Admin",

                new String[]{
                        "Overview",
                        "Files",
                        "Requests",
                        "Team & roles",
                        "Audit log",
                        "Settings"
                },

                new String[]{
                        "▦",
                        "▤",
                        "⇄",
                        "♧",
                        "☷",
                        "⚙"
                },

                pageRenderer,
                logoutAction
        );
    }
}