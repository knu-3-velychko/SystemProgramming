import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BakeryLock extends Lockable {
    private AtomicBoolean[] isEntered;
    private int[] numbers;
    private int size;

    BakeryLock(int maxCount) {
        super(maxCount);
        this.size = maxCount;
        isEntered = new AtomicBoolean[maxCount];
        numbers = new int[maxCount];
        Arrays.fill(isEntered, new AtomicBoolean(false));
        Arrays.fill(numbers, 0);
    }


    @Override
    public void lock() {
        int id = this.getID();
        isEntered[id] = new AtomicBoolean(true);

        int maxItem = 0;
        for (int i = 0; i < size; i++)
            maxItem = Math.max(maxItem, numbers[i]);

        numbers[id] = maxItem + 1;

        isEntered[id] = new AtomicBoolean(false);

        for (int i = 0; i < size; i++) {
            while (isEntered[i].get())
                Thread.yield();
            while ((numbers[i] != 0)
                    && ((numbers[id] > numbers[i])
                    || (numbers[id] == numbers[i] && id > i))
            )
                Thread.yield();
        }
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long l, TimeUnit timeUnit) {
        return false;
    }

    @Override
    public void unlock() {
        int id = this.getID();
        numbers[id] = 0;
    }
}
