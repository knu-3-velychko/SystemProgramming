package com.Tai;

//import spos.lab1.demo.DoubleOps;
//import spos.lab1.demo.IntOps;

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

    interface FunctionInt {
        int operation(int a);
    }

    interface FunctionDouble {
        double operation(int a);
    }

    //java -jar Module.jar <args>
    //address port type(int/double) function(f or g) case(0-5)
    public static void main(String[] args) {
        System.out.println("Client works!");

        if (!parseArgs(args)) {
            System.out.println("Invalid arguments.");
            System.exit(0);
        }

        try {
            start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        while (true) ;
    }

    private static void start() throws IOException, InterruptedException {
        clientSocket = SocketChannel.open(address);
        if (!clientSocket.isConnected()) {
            System.out.println("Can't connect to server.");
            System.exit(0);
        }

        ByteBuffer buffer = ByteBuffer.allocate(256);
        byte[] message = new byte[256];
        Object computationResult = function.apply(fCase);
        buffer.flip();

        if (computationResult instanceof Integer) {
            message = ((Integer) computationResult).toString().getBytes();
        } else if (computationResult instanceof Double) {
            message = ((Double) computationResult).toString().getBytes();
        } else {
            System.out.println("Wrong functions.");
            System.exit(0);
        }

        buffer = ByteBuffer.wrap(message);
        clientSocket.write(buffer);
        System.out.println("Message send.");
        buffer.clear();
        clientSocket.read(buffer);
        Thread.sleep(2000);
        //clientSocket.close();
    }

    private static boolean parseArgs(String[] args) {
        if (args.length != 5) return false;

        address = new InetSocketAddress(args[0], Integer.parseInt(args[1]));

        switch (args[2]) {
            case "int":
                if (!parseIntFunction(args[3])) return false;
                break;
            case "double":
                if (!parseDoubleFunction(args[3])) return false;
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
                    //try {
                    return 3;
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        return 0;
//                    }
                };
                break;
            case "g":
                function = i -> {
                    //  try {
                    return 5;
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        return 0;
//                    }
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
                    //try {
                    return 3.0;
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        return 0.0;
//                    }
                };
                break;
            case "g":
                function = i -> {
                    //try {
                    return 5.0;
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        return 0.0;
//                    }
                };
                break;
            default:
                return false;
        }
        return true;
    }
}