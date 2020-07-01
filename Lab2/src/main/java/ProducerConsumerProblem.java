import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerConsumerProblem {
    private static final int MAX = 1000;
    private AtomicInteger length = new AtomicInteger(0);
    private AtomicBoolean sleepingConsumer = new AtomicBoolean(true);
    private AtomicBoolean sleepingProducer = new AtomicBoolean(false);

    private AtomicInteger[] buffer = new AtomicInteger[1000];

    public void run() throws InterruptedException {
        Thread consumer = new Thread(new Consumer(buffer, length));
        Thread producer = new Thread(new Producer(buffer, length));
//        Thread consumer = new Thread(new WastefulConsumer(sleepingConsumer, sleepingConsumer, buffer, length));
//        Thread producer = new Thread(new WastefulProducer(sleepingProducer, sleepingConsumer, buffer, length));
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }

}

class Consumer implements Runnable {
    private AtomicInteger[] buffer;
    private AtomicInteger length;

    public Consumer(AtomicInteger[] buffer, AtomicInteger length) {
        this.buffer = buffer;
        this.length = length;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (length.get() == 0) {
                System.out.println("Consumer : Nothing to consume!");
                continue;
            }

            System.out.println("Consumer : Consumed \u001b[36;1m"
                    + buffer[length.getAndDecrement() - 1] + "\u001b[0m. Length = " + length.get());

        }
    }
}

class Producer implements Runnable {
    private static final int MAX = 900;
    private AtomicInteger[] buffer;
    private AtomicInteger length;
    static Random random = new Random(System.currentTimeMillis());

    public Producer(AtomicInteger[] buffer, AtomicInteger length) {
        this.buffer = buffer;
        this.length = length;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (length.get() == MAX - 1) {
                System.out.println("Producer: Buffer is full!");
                continue;
            }
            AtomicInteger newValue = new AtomicInteger(random.nextInt(MAX) + 100);
            buffer[length.getAndIncrement()] = newValue;
            System.out.println("Producer : Produced \u001b[36;1m"
                    + newValue.get() + "\u001b[0m. Length = " + length.get());

        }
    }
}

class WastefulConsumer implements Runnable {
    private AtomicBoolean isSleeping;
    private AtomicBoolean anotherSleeping;
    private AtomicInteger[] buffer;
    private AtomicInteger length;

    public WastefulConsumer(AtomicBoolean isSleeping, AtomicBoolean anotherSleeping,
                            AtomicInteger[] buffer, AtomicInteger length) {
        this.isSleeping = isSleeping;
        this.anotherSleeping = anotherSleeping;
        this.buffer = buffer;
        this.length = length;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (length.get() == 0) {
                System.out.println("Nothing to consume!");
                isSleeping.set(true);
                anotherSleeping.set(false);
            }

            if (!isSleeping.get()) {
                System.out.println("Consumer : Consumed \u001b[36;1m"
                        + buffer[length.getAndDecrement() - 1] + "\u001b[0m. Length = " + length);
            }
        }
    }
}

class WastefulProducer implements Runnable {
    private static final int MAX = 900;
    private AtomicBoolean isSleeping;
    private AtomicBoolean anotherSleeping;
    private AtomicInteger[] buffer;
    private AtomicInteger length;
    static Random random = new Random(System.currentTimeMillis());

    public WastefulProducer(AtomicBoolean isSleeping, AtomicBoolean anotherSleeping,
                            AtomicInteger[] buffer, AtomicInteger length) {
        this.isSleeping = isSleeping;
        this.anotherSleeping = anotherSleeping;
        this.buffer = buffer;
        this.length = length;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (length.get() == MAX - 1) {
                System.out.println("Producer: Buffer is full!");
                isSleeping.set(true);
                anotherSleeping.set(false);
            }

            if (!isSleeping.get()) {
                AtomicInteger newValue = new AtomicInteger(random.nextInt(MAX) + 100);
                buffer[length.getAndIncrement()] = newValue;
                System.out.println("Producer : Produced \u001b[36;1m"
                        + newValue.get() + "\u001b[0m. Length = " + length);
            }
        }
    }
}