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

    private AtomicInteger currentConnection;

    private Selector selector;
    private ServerSocketChannel serverSocket;

    private int fCase;
    private String type;
    private String path;

    Manager(String bindAddress, int bindPort, int connectionNumber, int fCase, String type, boolean promptEnabled) {
        address = new InetSocketAddress(bindAddress, bindPort);
        maxConnection = connectionNumber;

        this.promptEnabled = promptEnabled;
        clientProcesses = new ArrayList<>();

        this.fCase = fCase;
        this.type = type;

        String classPath = System.getProperty("java.class.path");
        String[] classpathEntries = classPath.split(";");
        path = classpathEntries[0];
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
        while (true) {
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
        ProcessBuilder clientBuilder =
                new ProcessBuilder("java", "-cp", path, "com.Tai.Client",
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
}