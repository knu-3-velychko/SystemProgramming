package com.Tai;

import spos.lab1.demo.DoubleOps;
import spos.lab1.demo.IntOps;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.function.Function;

public class Client {
    private static InetSocketAddress address;
    private static SocketChannel clientSocket;

    private static Function<Integer, Object> function;
    private static int fCase;
    private static String name;

    interface FunctionInt {
        int operation(int a);
    }

    interface FunctionDouble {
        double operation(int a);
    }

    //address port type(int/double) function(f or g) case(0-5)
    public static void main(String[] args) {
        System.out.println("Client works!");

        //String[] arg = {"localhost", "1052", "int", "f", "0"};
        if (!parseArgs(args)) {
            System.out.println("Invalid arguments.");
            System.exit(0);
        }

        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void start() throws IOException {
        clientSocket = SocketChannel.open(address);
        if (!clientSocket.isConnected()) {
            System.out.println("Can't connect to server.");
            System.exit(0);
        }

        ByteBuffer buffer = ByteBuffer.allocate(256);
        byte[] message = new byte[256];
        Object computationResult = function.apply(fCase);
        buffer.flip();

        String result = null;
        if (computationResult instanceof Integer) {
            result = ((Integer) computationResult).toString();
        } else if (computationResult instanceof Double) {
            result = ((Double) computationResult).toString();
        } else {
            System.out.println("Wrong functions.");
            System.exit(0);
        }

        message = (name + " " + result).getBytes();

        buffer = ByteBuffer.wrap(message);
        clientSocket.write(buffer);
        System.out.println("Message send.");
        buffer.clear();

        clientSocket.close();
    }

    private static boolean parseArgs(String[] args) {
        if (args.length != 5) return false;

        address = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
        name = args[3];

        switch (args[2]) {
            case "int":
                if (!parseIntFunction(name)) return false;
                break;
            case "double":
                if (!parseDoubleFunction(name)) return false;
                break;
            default:
                return false;
        }

        fCase = Integer.parseInt(args[4]);
        if (fCase < 0 || fCase > 5) return false;

        return true;
    }


    //TODO: find how to refactor this
    private static boolean parseIntFunction(String arg) {
        switch (arg) {
            case "f":
                function = i -> {
                    try {
                        return IntOps.funcF(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return 0;
                    }
                };
                break;
            case "g":
                function = i -> {
                    try {
                        return IntOps.funcG(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return 0;
                    }
                };
                break;
            default:
                return false;
        }
        return true;
    }

    private static boolean parseDoubleFunction(String arg) {
        switch (arg) {
            case "f":
                function = i -> {
                    try {
                        return DoubleOps.funcF(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return 0;
                    }
                };
                break;
            case "g":
                function = i -> {
                    try {
                        return DoubleOps.funcG(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return 0;
                    }
                };
                break;
            default:
                return false;
        }
        return true;
    }
}
