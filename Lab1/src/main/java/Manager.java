import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Manager {
    private int serverPort;
    private String serverAddress;
    private int clientConnectionNumber;

    private Server server;

    private List<Process> clientProcesses;

    private boolean promptEnabled;

    Manager(int port, String address, int clientConnectionNumber, boolean promptEnabled) {
        serverPort = port;
        serverAddress = address;
        this.clientConnectionNumber = clientConnectionNumber;

        this.promptEnabled = promptEnabled;
        clientProcesses = new ArrayList<>();

        server = new Server(serverAddress, serverPort, clientConnectionNumber);
    }

    void start() {

        String classPath = Manager.class.getClassLoader().getResource(".").toString();
        ProcessBuilder clientBuilder = new ProcessBuilder("java", "-cp", classPath, "Client",
                serverAddress,
                Integer.toString(serverPort),
                "int",
                "f",
                Integer.toString(0));
//        try {
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            Process clientProcess = clientBuilder.start();
            clientProcesses.add(clientProcess);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
