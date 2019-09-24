import jdk.internal.net.http.common.Pair;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private String address;
    private int port;
    private int variable;
    private int maxConnection;

    private final List<Pair<ByteBuffer, Integer>> clientResponse;

    private AtomicInteger currentConnection;

    private SocketChannel clientSocket;
    private ServerSocketChannel serverSocket;

    Server(String bindAddress, int bindPort, int var, int connectionNumber) {
        address = bindAddress;
        port = bindPort;
        variable = var;
        maxConnection = connectionNumber;
        clientResponse = new ArrayList<>();
        currentConnection = new AtomicInteger(0);
    }

    synchronized AtomicInteger getCurrentConnection() {
        return currentConnection;
    }

    void start() throws IOException {
        currentConnection.compareAndSet(0, 0);
        serverSocket = ServerSocketChannel.open().bind(new InetSocketAddress(address, port));
        serverSocket.accept();
    }

    private synchronized void write(SocketChannel socketChannel, final int var) {

    }

    private synchronized void read(SocketChannel socketChannel) {

    }
}
