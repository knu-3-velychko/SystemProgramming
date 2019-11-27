public class RaceCondition {
    private static Integer counter = 0;

    public static void simulateRaceCondition(int step) throws InterruptedException {
        createRaceCondition(step);
        System.out.println("Race condition");
        System.out.println("Expected value: " + step * 2);
        System.out.println("Real value: " + counter);
        System.out.println();
    }

    public static void createRaceCondition(int step) throws InterruptedException {
        Thread thread1 = new Thread(incrementCounter(step));
        Thread thread2 = new Thread(incrementCounter(step));

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    public static void checkLock(FixnumLockable lock, int step) throws InterruptedException {
        System.out.println("Race condition check");
        if (isRaced(lock, step))
            System.out.println(lock.getClass().getName() + " has race condition");
        else
            System.out.println(lock.getClass().getName() + " has no race condition");
    }

    private static boolean isRaced(FixnumLockable lock, int step) throws InterruptedException {
        RaceIncrementCounter incrementCounter = new RaceIncrementCounter(lock, step);

        Thread thread1 = new Thread(incrementCounter);
        Thread thread2 = new Thread(incrementCounter);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        return incrementCounter.getCounter() != 2 * step;
    }


    private static Runnable incrementCounter(int step) {
        return () -> {
            for (int i = 0; i < step; i++) {
                counter = counter + 1;
            }
        };
    }
}
