// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

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

        result.schedulingType = "Batch (Nonpreemptive)";
        result.schedulingName = "Shortest job first";
        try {
            //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
            //OutputStream out = new FileOutputStream(resultsFile);
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));

            processVector.sort(new ProcessPriorityComparator());

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
                    //TODO

                    currentProcess++;
                    if (currentProcess >= size)
                        currentProcess = 0;
                    process = processVector.elementAt(currentProcess);
                    while (process.cpudone > process.cputime && currentProcess < size - 1) {
                        currentProcess++;
                        process = processVector.elementAt(currentProcess);
                    }

                    process = processVector.elementAt(currentProcess);
                    out.println("Process: " + process.number + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + process.priority + ")");
                }
                if (process.ioblocking == process.ionext) {
                    out.println("Process: " + process.number + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + " " + process.priority + ")");
                    process.numblocked++;
                    process.ionext = 0;
                    previousProcess = currentProcess;

                    currentProcess++;
                    if (currentProcess >= size)
                        currentProcess = 0;
                    process = processVector.elementAt(currentProcess);
                    while (process.cpudone > process.cputime && currentProcess < size - 1) {
                        process = processVector.elementAt(currentProcess);
                        currentProcess++;
                    }

                    process = processVector.elementAt(currentProcess);
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

    private static int selectProcess(Vector<sProcess> processVector, int previousProcess) {
        int size = processVector.size();
        int current = getFirstWaitingProcess(processVector, previousProcess);
        sProcess process = processVector.get(0);
        for (int i = 0; i < size; i++) {
            sProcess tmp = processVector.get(i);
            if (isValid(processVector, i, previousProcess)) {
                if (cmp(tmp, process)) {
                    process = tmp;
                    current = i;
                }
            }
        }
        return current;
    }

    private static boolean cmp(sProcess process1, sProcess process2) {
        return process1.ioblocking < process2.ioblocking;
        //return process1.priority / process1.ioblocking > process2.priority / process2.ioblocking;
    }

    private static int getFirstWaitingProcess(Vector<sProcess> processVector, int previousProcess) {
        int size = processVector.size();
        for (int i = 0; i < size; i++) {
            if (isValid(processVector, i, previousProcess))
                return i;
        }
        return previousProcess;
    }

    private static boolean isValid(Vector<sProcess> processVector, int process, int previousProcess) {
        return process != previousProcess && isWaitingProcess(processVector.get(process));
    }

    private static boolean isWaitingProcess(sProcess process) {
        return process.cpudone < process.cputime && process.ioblocking != process.ionext;
    }
}
