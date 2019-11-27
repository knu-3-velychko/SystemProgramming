public class DekkersAlgorithm {
    private static Integer counter = 0;

    private static DekkerLock dekkerLock;

    public static void run(int step) throws InterruptedException {
        dekkerLock = new DekkerLock();

        dekker(step);
        System.out.println("Dekker's Algorithm");
        System.out.println("Expected value: " + step * 2);
        System.out.println("Real value: " + counter);
        System.out.println();
    }


    public static void dekker(int step) throws InterruptedException {
        counter = 0;
        Thread thread1 = new Thread(dekkerIncrementCounter(step));
        Thread thread2 = new Thread(dekkerIncrementCounter(step));

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    private static Runnable dekkerIncrementCounter(int step) {
        return () -> {
            dekkerLock.registerThread();
            for (int i = 0; i < step; i++) {
                dekkerLock.lock();
                counter++;
                dekkerLock.unlock();
            }
            dekkerLock.unregisterThread();
        };
    }


}


