package com.Tai;

import spos.lab1.demo.Conjunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Manager {
    private InetSocketAddress address;
    private int maxConnection;
    private List<Process> clientProcesses;

    private boolean promptEnabled;
    private boolean calculationsEnabled;

    private int connections = 0;

    private Selector selector;
    private ServerSocketChannel serverSocket;

    private int fCase;
    private String type;
    private String path;

    private static final long delta = 1000;
    private Scanner sc;
    private long lastPromptTime;
    private long startTime;

    private HashMap<String, Double> results;
    private HashMap<String, Long> time;

    NonblockingBufferedReader reader;

    Manager(String bindAddress, int bindPort, int connectionNumber, int fCase, String type, boolean promptEnabled) {
        address = new InetSocketAddress(bindAddress, bindPort);
        maxConnection = connectionNumber;

        this.promptEnabled = promptEnabled;
        calculationsEnabled = true;
        clientProcesses = new ArrayList<>();

        this.fCase = fCase;
        this.type = type;

        String classPath = System.getProperty("java.class.path");
        String[] classpathEntries = classPath.split(";");
        path = classpathEntries[0];

        sc = new Scanner(System.in);
        lastPromptTime = System.currentTimeMillis();
        startTime = System.currentTimeMillis();

        results = new HashMap<>();
        time = new HashMap<>();

        reader = new NonblockingBufferedReader(new BufferedReader(new InputStreamReader(System.in)));
    }

    void start() throws IOException {
        selector = Selector.open();

        serverSocket = ServerSocketChannel.open().bind(address);
        serverSocket.configureBlocking(false);

        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        compute("f");
        compute("g");

        //keep server running
        while (calculationsEnabled) {
            selector.select();

            Set<SelectionKey> selectedKeys = selector.selectedKeys();

            for (SelectionKey key : selectedKeys) {

                if (key.isAcceptable()) {
                    register(serverSocket, selector);
                } else if (key.isReadable()) {
                    SocketChannel clientSocket = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    clientSocket.read(buffer);
                    String result = new String(buffer.array()).trim();
                    addResult(result);
                    //clientSocket.close();
                }
            }

            if (connections <= 0)
                calculationsEnabled = false;
            prompt();

        }
    }

    private void register(ServerSocketChannel serverSocket, Selector selector) throws IOException {
        SocketChannel client = serverSocket.accept();
        if (client != null) {
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            connections++;
        }
    }

    void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compute(String function) {
        ProcessBuilder clientBuilder =
                new ProcessBuilder("java", "-jar", "C:\\Users\\Taya\\Documents\\Projects\\3course\\SystemProgramming\\Lab\\out\\production\\Lab" + "\\" + "Lab1-Client.jar",
                        address.getHostString(),
                        Integer.toString(address.getPort()),
                        type,
                        function,
                        Integer.toString(fCase));

        try {
            clientProcesses.add(clientBuilder.start());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prompt() {
        promptA();
        promptB();
    }

    private void promptA() {
        if (!calculationsEnabled)
            return;
        try {
            String line = reader.readLine();
            if (line != null) {
                if (line.equals("a") || line.equals("b") || line.equals("c"))
                    return;
                if (line.equals("q"))
                    calculationsEnabled = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void promptB() {
        if (promptEnabled && (System.currentTimeMillis() - lastPromptTime) > delta) {
            lastPromptTime = System.currentTimeMillis();
            System.out.println("Computation taking too long. Would you like to:");
            System.out.println("(a) continue");
            System.out.println("(b) continue without prompt");
            System.out.println("(c) cancel");
            boolean correct = false;
            while (!correct) {
                while (!sc.hasNextLine()) ;
                String line = sc.nextLine();
                line = line.toLowerCase();
                correct = true;
                switch (line) {
                    case "a": {
                        lastPromptTime = System.currentTimeMillis();
                        System.out.println("Continue");
                        break;
                    }
                    case "b": {
                        promptEnabled = false;
                        System.out.println("Prompt disabled");
                        break;
                    }
                    case "c":
                    case "q": {
                        calculationsEnabled = false;
                        System.out.println("Canceled");
                        break;
                    }
                    default: {
                        correct = false;
                        System.out.println("Incorrect response: " + line);
                    }
                }
            }
        }
    }

    private void killClientProcesses() {
        for (Process i : clientProcesses) {
            i.destroy();
        }
    }

    private void quit() {
        killClientProcesses();
        stop();
        System.out.flush();
    }

    private void addResult(String result) {
        String[] args = result.split(" ");
        if (args.length < 2)
            return;
        connections--;
        String name = args[0];
        double value = Double.parseDouble(args[1]);
        results.put(name, value);
        Long t = System.currentTimeMillis() - startTime;
        time.put(name, t);
        if (value == 0.0)
            calculationsEnabled = false;
    }


    HashMap<String, Double> getResults() {
        return results;
    }

    HashMap<String, Long> getTime() {
        return time;
    }


    boolean getConjunction() {
        try {
            if (results.getOrDefault("f", 1.0) == 0.0 || results.getOrDefault("g", 1.0) == 0.0)
                return false;
            return (Conjunction.funcF(fCase) & Conjunction.funcG(fCase));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
