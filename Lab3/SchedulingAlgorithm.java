// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.Comparator;
import java.util.Vector;
import java.io.*;

public class SchedulingAlgorithm {

    public static Results run(int runtime, Vector<sProcess> processVector, Results result, String logFile) {
        int i = 0;
        int comptime = 0;
        int previousProcess = -1;
        int currentProcess = 0;
        int size = processVector.size();
        int completed = 0;
        String resultsFile = logFile;
        Vector<sProcess> vector = getVector(processVector);
        Comparator cmp = new ProcessPriorityComparator();

        result.schedulingType = "Batch (Nonpreemptive)";
        result.schedulingName = "Shortest job first";
        try {
            //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
            //OutputStream out = new FileOutputStream(resultsFile);
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));

            processVector.sort(cmp);

            sProcess process = processVector.elementAt(currentProcess);
            out.println("Process: " + process.number + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + " " + process.priority + ")");
            while (comptime < runtime) {
                if (process.cpudone == process.cputime) {
                    completed++;
                    out.println("Process: " + process.number + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + " " + process.priority + ")");
                    if (completed == size) {
                        result.compuTime = comptime;
                        out.close();
                        return result;
                    }
                    vector = getVector(processVector);
                    if (vector.isEmpty())
                        break;
                    currentProcess++;
                    if (currentProcess >= vector.size())
                        currentProcess = 0;
                    process = vector.elementAt(currentProcess);

                    if (currentProcess == previousProcess && process.cpudone >= process.cputime) {
                        out.println("Process: " + process.number + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + " " + process.priority + ")");
                        break;
                    }
                    out.println("Process: " + process.number + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + process.priority + ")");
                }
                if (process.ioblocking == process.ionext) {
                    out.println("Process: " + process.number + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + " " + process.priority + ")");
                    process.numblocked++;
                    process.ionext = 0;
                    previousProcess = currentProcess;

                    vector = getVector(processVector);
                    if (vector.isEmpty())
                        break;
                    currentProcess++;
                    if (currentProcess >= vector.size())
                        currentProcess = 0;
                    process = vector.elementAt(currentProcess);

                    if (currentProcess == previousProcess && process.cpudone >= process.cputime) {
                        out.println("Process: " + process.number + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + " " + process.priority + ")");
                        break;
                    }

                    out.println("Process: " + process.number + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + " " + process.priority + ")");
                }
                process.cpudone++;
                if (process.ioblocking > 0) {
                    process.ionext++;
                }
                comptime++;
            }
            out.close();
        } catch (IOException e) { /* Handle exceptions */ }
        result.compuTime = comptime;
        return result;
    }


    private static Vector<sProcess> getVector(Vector<sProcess> processVector) {
        Vector<sProcess> vector = new Vector<>();
        for (sProcess process : processVector) {
            if (process.cputime > process.cpudone)
                vector.add(process);
        }
        return vector;
    }
}
