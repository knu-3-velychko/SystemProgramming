import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BakeryLockAlgorithm {
    private static final int THREADS = 4;
    private static int NUMBERS = 1_000;
    private static BakeryLock bakeryLock = new BakeryLock(THREADS);
    private static long[] testResults = new long[THREADS * NUMBERS];

    private static int tested = 0;
    private static int testedIndex = 0;

    public static long[] simulateBakeryLock() throws InterruptedException {
        int[] parameters = new int[THREADS];
        List<Integer> parList = new ArrayList<>();
        for (int i = 0; i < THREADS / 2; i++) {
            parList.add(1);
            parList.add(-1);
        }
        Collections.shuffle(parList);
        for (int i = 0; i < THREADS; i++) {
            parameters[i] = parList.get(i);
        }

        Thread[] threads = new Thread[THREADS];

        for (int i = 0; i < THREADS; i++)
            threads[i] = new Thread(function(parameters[i]));
        for (int i = 0; i < THREADS; i++) {
            threads[i].start();
            threads[i].join();
        }

        return testResults;
    }

    private static Runnable function(int value) {
        return () -> {
            for (int i = 0; i < NUMBERS; i++) {
                bakeryLock.registerThread();
                bakeryLock.lock();
                tested += value;
                testResults[testedIndex] = tested;
                testedIndex++;
                bakeryLock.unlock();
                bakeryLock.unregisterThread();
            }
        };
    }
}
