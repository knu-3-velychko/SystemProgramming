import java.util.Random;

public class BakeryLockAlgorithm {
    private static final int THREADS = 4;
    private static int NUMBERS = 100_000;
    private static BakeryLock bakeryLock = new BakeryLock(THREADS);
    private static long[] testResults = new long[THREADS * NUMBERS];

    private static int tested = 0;
    private static int testedIndex = 0;

    public static void simulateBakeryLock() throws InterruptedException {
        int[] parameters = new int[THREADS];
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < THREADS; i++) {
            if (random.nextBoolean())
                parameters[i] = -1;
            else
                parameters[i] = 1;
        }

        Thread[] threads = new Thread[THREADS];

        for (int i = 0; i < THREADS; i++)
            threads[i] = new Thread(function(parameters[i]));
        for (int i = 0; i < THREADS; i++)
            threads[i].join();
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
