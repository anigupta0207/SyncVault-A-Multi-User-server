package Student;

import auth.AuthenticatedUser;
import dashboard.DashboardShell;

import javax.swing.*;
import java.util.function.Function;

public class StudentDashboard
        extends DashboardShell {

    public StudentDashboard(
            AuthenticatedUser user,
            Function<String, JPanel> pageRenderer,
            Runnable logoutAction
    ) {

        super(
                user.getName(),
                user.getEmail(),
                "Student",

                new String[]{
                        "Overview",
                        "Files",
                        "Requests",
                        "Submissions",
                        "File versions",
                        "Settings"
                },

                new String[]{
                        "▦",
                        "▤",
                        "⇄",
                        "⇧",
                        "◷",
                        "⚙"
                },

                pageRenderer,
                logoutAction
        );
    }
}