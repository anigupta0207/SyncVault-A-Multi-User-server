package Teacher;

import auth.AuthenticatedUser;
import dashboard.DashboardShell;

import javax.swing.*;
import java.util.function.Function;

public class TeacherDashboard
        extends DashboardShell {

    public TeacherDashboard(
            AuthenticatedUser user,
            Function<String, JPanel> pageRenderer,
            Runnable logoutAction
    ) {

        super(
                user.getName(),
                user.getEmail(),
                "Teacher",

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