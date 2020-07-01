public class Main {
    public static void main(String[] args) {
        int step = 100000;
        try {
            // TAI
//            DekkersAlgorithm.run(step);
//
//            RaceCondition.simulateRaceCondition(step);
//            EmptyLock emptyLock = new EmptyLock(2);
//            RaceCondition.checkLock(emptyLock, step);
//            DekkerLock dekkerLock = new DekkerLock();
//            RaceCondition.checkLock(dekkerLock, step);
//
//            // VLAD
            long[] res = BakeryLockAlgorithm.simulateBakeryLock();
//            for (long re : res) System.out.print(re + " | ");

//            ProducerConsumerProblem pcProblem = new ProducerConsumerProblem();
//            pcProblem.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
