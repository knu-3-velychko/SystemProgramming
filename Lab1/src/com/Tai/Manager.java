package com.Tai;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Manager {
    private InetSocketAddress address;
    private int maxConnection;
    private List<Process> clientProcesses;

    private boolean promptEnabled;
    private boolean calculationsEnabled;

    private AtomicInteger currentConnection;

    private Selector selector;
    private ServerSocketChannel serverSocket;

    private int fCase;
    private String type;
    private String path;

    private static final long delta = 1000;
    private Scanner sc;
    long lastPromptTime;

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
    }

    void start() throws IOException {
        selector = Selector.open();

        serverSocket = ServerSocketChannel.open().bind(address);
        serverSocket.configureBlocking(false);

        int ops = serverSocket.validOps();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        compute("f");
        compute("g");

        //keep server running
        while (calculationsEnabled) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            prompt();
            selector.select();

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectedKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = it.next();

                if (key.isAcceptable()) {
                    register(serverSocket, selector);
                } else if (key.isReadable()) {
                    SocketChannel clientSocket = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    clientSocket.read(buffer);
                    String result = new String(buffer.array()).trim();
                    System.out.println(result);
                    clientSocket.close();
                }
            }
        }
    }

    private static void register(ServerSocketChannel serverSocket, Selector selector) throws IOException {
        SocketChannel client = serverSocket.accept();
        if (client != null) {
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            System.out.println("here");
        }
    }

    void stop() throws IOException {
        serverSocket.close();
    }

    void compute(String function) {
        System.out.println(path);
        ProcessBuilder clientBuilder =
                new ProcessBuilder("java", "-jar", path + "\\" + "Lab1-Client.jar",
                        address.getHostString(),
                        Integer.toString(address.getPort()),
                        type,
                        function,
                        Integer.toString(fCase));
//                new ProcessBuilder("java", "-cp", path, "com.Tai.Client",
//                        address.getHostString(),
//                        Integer.toString(address.getPort()),
//                        type,
//                        function,
//                        Integer.toString(fCase));

        try {
            clientProcesses.add(clientBuilder.start());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prompt() {
        if (promptEnabled && (System.currentTimeMillis() - lastPromptTime) > delta) {
            lastPromptTime = System.currentTimeMillis();
            System.out.println("Computation taking too long. Would you like to:");
            System.out.println("(a) continue");
            System.out.println("(b) continue without prompt");
            System.out.println("(c) cancel");
            boolean correct = false;
            while (!correct) {
                while (!sc.hasNextLine()) ;
                String line = sc.nextLine().toLowerCase();
                correct = true;
                switch (line) {
                    case "a": {
                        lastPromptTime = System.currentTimeMillis();
                        break;
                    }
                    case "b": {
                        promptEnabled = false;
                        break;
                    }
                    case "c": {
                        calculationsEnabled = false;
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

    void killClientProcesses() {
        for (Process i : clientProcesses) {
            i.destroy();
        }
    }

    public void quit() {
        killClientProcesses();
        System.out.flush();
    }
}