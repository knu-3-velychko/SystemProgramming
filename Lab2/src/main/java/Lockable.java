import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public abstract class Lockable implements FixnumLockable {
    private long[] threadsID;   //thread id is positive long
    private int size;

    Lockable(int maxCount) {
        threadsID = new long[maxCount];
        size = maxCount;
        reset();
    }

    public synchronized Integer getID() {
        long id = Thread.currentThread().getId();
        for (int i = 0; i < size; i++) {
            if (threadsID[i] == id) {
                return i;
            }
        }
        assert false;
        return null;
    }

    public synchronized void registerThread() {
        registerThread(Thread.currentThread().getId());
    }

    public synchronized void registerThread(long id) {
        for (int i = 0; i < size; i++) {
            if (threadsID[i] <= 0) {
                threadsID[i] = id;
                return;
            }
        }
        assert (false);
    }

    public synchronized void unregisterThread() {
        unregisterThread(Thread.currentThread().getId());
    }

    public synchronized void unregisterThread(long id) {
        for (int i = 0; i < size; i++) {
            if (threadsID[i] == id) {
                threadsID[i] = -1;
                return;
            }
        }
        assert (false);
    }

    public synchronized void reset() {
        for (int i = 0; i < size; i++) {
            threadsID[i] = -1;
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
    }


    @Override
    public Condition newCondition() {
        return null;
    }
}
