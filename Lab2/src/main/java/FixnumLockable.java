import java.util.concurrent.locks.Lock;

public interface FixnumLockable extends Lock {
    @Override
     void lock();

    @Override
    void unlock();

    Integer getID();

    void registerThread();

    void registerThread(long id);

    void unregisterThread();

    void unregisterThread(long id);

    void reset();
}
