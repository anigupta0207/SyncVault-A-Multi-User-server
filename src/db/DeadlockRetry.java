
package db;

import java.sql.SQLException;

public final class DeadlockRetry {

    private static final int MAX_ATTEMPTS = 3;

    private DeadlockRetry() {
    }

    public static boolean isDeadlock(SQLException e) {
        SQLException current = e;

        while (current != null) {
            if (current.getErrorCode() == 1213
                    || "40001".equals(current.getSQLState())) {
                return true;
            }

            current = current.getNextException();
        }

        return false;
    }

    public static int getMaxAttempts() {
        return MAX_ATTEMPTS;
    }
}
