class RaceIncrementCounter implements Runnable {

    private FixnumLockable lock;
    private int step;
    private static Integer counter = 0;

    RaceIncrementCounter(FixnumLockable lock, int step) {
        counter = 0;
        this.lock = lock;
        this.step = step;
    }

    @Override
    public void run() {
        lock.registerThread();
        for (int i = 0; i < step; i++) {
            lock.lock();
            counter = counter + 1;
            lock.unlock();
        }
        lock.unregisterThread();
    }

    public Integer getCounter() {
        return counter;
    }
}
