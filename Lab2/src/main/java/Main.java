public class Main {
    public static void main(String[] args) {
        int step = 100000;
        try {
            DekkersAlgorithm.run(step);

            RaceCondition.simulateRaceCondition(step);
            EmptyLock emptyLock = new EmptyLock(2);
            RaceCondition.checkLock(emptyLock, step);
            DekkerLock dekkerLock = new DekkerLock();
            RaceCondition.checkLock(dekkerLock, step);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
