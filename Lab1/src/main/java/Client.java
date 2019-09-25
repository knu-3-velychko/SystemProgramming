import com.sun.org.apache.bcel.internal.generic.ObjectType;
import spos.lab1.demo.DoubleOps;
import spos.lab1.demo.IntOps;

import java.io.IOException;
import java.lang.reflect.Method;
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
        if (!parseArgs(args)) {
            System.out.println("Invalid arguments.");
            System.exit(0);
        }

        try {
            start();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void start() throws IOException, InterruptedException {
        clientSocket = SocketChannel.open(address);
        if (clientSocket.isConnected()) {
            System.out.println("Can't connect to server.");
            System.exit(0);
        }

        ByteBuffer buffer = ByteBuffer.allocate(256);
        int a = clientSocket.read(buffer);

        //TODO

        Object computationResult = function.apply(fCase);
        buffer.flip();
        if (computationResult instanceof Integer) {
            buffer.asIntBuffer().put((Integer) computationResult);
        } else if (computationResult instanceof Double) {
            buffer.asDoubleBuffer().put((Double) computationResult);
        } else {
            System.out.println("Wrong functions.");
            System.exit(0);
        }

        clientSocket.write(buffer);
        System.out.println("Message send.");
        clientSocket.close();
    }

    private static boolean parseArgs(String[] args) {
        if (args.length != 5) return false;

        address = new InetSocketAddress(args[0], Integer.parseInt(args[1]));

        switch (args[2]) {
            case "int":
                parseIntFunction(args[3]);
                break;
            case "double":
                parseDoubleFunction(args[3]);
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
                        return 0.0;
                    }
                };
                break;
            case "g":
                function = i -> {
                    try {
                        return DoubleOps.funcG(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return 0.0;
                    }
                };
                break;
            default:
                return false;
        }
        return true;
    }
}
