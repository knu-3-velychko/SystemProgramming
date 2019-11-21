import java.util.Comparator;

public class ProcessComparator implements Comparator {
    @Override
    public int compare(Object o, Object t1) {
        sProcess process1 = (sProcess) o;
        sProcess process2 = (sProcess) t1;
        return process1.ioblocking - process2.ioblocking;
    }
}
