
package file;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public final class FileLockManager {

    private static final ConcurrentHashMap<Integer, ReentrantLock> LOCKS =
            new ConcurrentHashMap<>();

    private FileLockManager() {
    }

    public static boolean acquireLock(int fileId) {
        if (fileId <= 0) {
            return false;
        }

        ReentrantLock lock =
                LOCKS.computeIfAbsent(fileId, id -> new ReentrantLock());

        // Do not wait indefinitely for a busy file.
        return lock.tryLock();
    }

    public static void releaseLock(int fileId) {
        ReentrantLock lock = LOCKS.get(fileId);

        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    public static boolean isLocked(int fileId) {
        ReentrantLock lock = LOCKS.get(fileId);
        return lock != null && lock.isLocked();
    }
}
