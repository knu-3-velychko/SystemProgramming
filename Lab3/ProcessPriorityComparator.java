import java.util.Comparator;

public class ProcessPriorityComparator implements Comparator {

    @Override
    public int compare(Object o, Object t1) {
        sProcess process1 = (sProcess) o;
        sProcess process2 = (sProcess) t1;
        if (process1.priority == process2.priority && process1.ioblocking == process2.ioblocking)
            return 0;
        float priority1 = (float) process1.priority / process1.ioblocking;
        float priority2 = (float) process2.priority / process2.ioblocking;
        if (priority2 - priority1 > 0.0f)
            return 1;
        else return -1;
    }
}
