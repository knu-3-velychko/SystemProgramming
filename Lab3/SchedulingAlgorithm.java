// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.Vector;
import java.io.*;

public class SchedulingAlgorithm {

    public static Results Run(int runtime, Vector processVector, Results result) {
        int i = 0;
        int comptime = 0;
        int previousProcess = -1;
        int currentProcess = selectProcess(processVector, previousProcess);
        int size = processVector.size();
        int completed = 0;
        String resultsFile = "Summary-Processes";

        result.schedulingType = "Batch (Nonpreemptive)";
        result.schedulingName = "Shortest job first";
        try {
            //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
            //OutputStream out = new FileOutputStream(resultsFile);
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
            sProcess process = (sProcess) processVector.elementAt(currentProcess);
            out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
            while (comptime < runtime) {
                if (process.cpudone == process.cputime) {
                    completed++;
                    out.println("Process: " + currentProcess + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
                    if (completed == size) {
                        result.compuTime = comptime;
                        out.close();
                        return result;
                    }
                    //TODO
                    currentProcess = selectProcess(processVector, previousProcess);
//                    for (i = size - 1; i >= 0; i--) {
//                        process = (sProcess) processVector.elementAt(i);
//                        if (process.cpudone < process.cputime) {
//                            currentProcess = i;
//                        }
//                    }
                    process = (sProcess) processVector.elementAt(currentProcess);
                    out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
                }
                if (process.ioblocking == process.ionext) {
                    out.println("Process: " + currentProcess + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
                    process.numblocked++;
                    process.ionext = 0;
                    previousProcess = currentProcess;
                    currentProcess = selectProcess(processVector, previousProcess);
//                    for (i = size - 1; i >= 0; i--) {
//                        process = (sProcess) processVector.elementAt(i);
//                        if (process.cpudone < process.cputime && previousProcess != i) {
//                            currentProcess = i;
//                        }
//                    }
                    process = (sProcess) processVector.elementAt(currentProcess);
                    out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
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

    private static int selectProcess(Vector processVector, int previousProcess) {
        int size = processVector.size();
        int current = getFirstWaitingProcess(processVector, previousProcess);
        sProcess process = (sProcess) processVector.get(0);
        for (int i = 0; i < size; i++) {
            sProcess tmp = (sProcess) processVector.get(i);
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

    private static int getFirstWaitingProcess(Vector processVector, int previousProcess) {
        int size = processVector.size();
        for (int i = 0; i < size; i++) {
            if (isValid(processVector, i, previousProcess))
                return i;
        }
        return previousProcess;
    }

    private static boolean isValid(Vector processVector, int process, int previousProcess) {
        return process != previousProcess && isWaitingProcess((sProcess) processVector.get(process));
    }

    private static boolean isWaitingProcess(sProcess process) {
        return process.cpudone < process.cputime && process.ioblocking != process.ionext;
    }
}
