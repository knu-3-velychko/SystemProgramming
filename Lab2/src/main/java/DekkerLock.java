import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class DekkerLock extends Lockable {
    private AtomicLong turn;
    private AtomicBoolean[] flags;

    DekkerLock() {
        super(2);
        flags = new AtomicBoolean[2];
        for (int i = 0; i < flags.length; i++)
            flags[i] = new AtomicBoolean(false);
        turn=new AtomicLong();
    }

    @Override
    public void lock() {
        int id = getID();
        int nextID = 1 - id;

        flags[id].set(true);
        while (flags[nextID].get()) {
            if (turn.get() == nextID) {
                flags[id].set(false);
                while (turn.get() == nextID) ;
                flags[id].set(true);
            }
        }

    }

    @Override
    public void unlock() {
        int id = getID();
        int nextID = 1 - id;
        turn.set(nextID);
        flags[id].set(false);
    }


    @Override
    public boolean tryLock() {
        int id = getID();
        int nextID = 1 - id;

        flags[id].set(true);
        while (flags[nextID].get() == true) {
            if (turn.get() == nextID) {
                flags[id].set(false);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {
        int id = getID();
        int nextID = 1 - id;

        long currentTime = System.currentTimeMillis();
        long endTime = currentTime + timeUnit.toMillis(l);

        flags[id].set(true);
        while (flags[nextID].get() == true) {
            if (turn.get() == nextID) {
                flags[id].set(false);
                while (turn.get() == nextID) {
                    if (currentTime == endTime)
                        return false;
                    currentTime++;
                }
                flags[id].set(true);
            }
        }
        return true;
    }

}
