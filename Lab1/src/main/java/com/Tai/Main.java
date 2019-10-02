import spos.lab1.demo.Conjunction;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("It works!");
        Manager manager = new Manager(1052, "localhost", 2, true);
        manager.start();
    }
}
