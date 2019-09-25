import jdk.internal.net.http.common.Pair;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private InetSocketAddress address;
    private int maxConnection;

    private final List<Pair<ByteBuffer, Integer>> clientResponse;

    private AtomicInteger currentConnection;

    private Selector selector;
    private ServerSocketChannel serverSocket;

    Server(String bindAddress, int bindPort, int connectionNumber) {
        address = new InetSocketAddress(bindAddress, bindPort);
        maxConnection = connectionNumber;
        clientResponse = new ArrayList<>();
        currentConnection = new AtomicInteger(0);
    }

    void start() throws IOException {
        selector = Selector.open();

        currentConnection.compareAndSet(0, 0);
        serverSocket = ServerSocketChannel.open().bind(address);
        serverSocket.configureBlocking(false);

        int ops = serverSocket.validOps();
        SelectionKey selectionKey = serverSocket.register(selector, ops);

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
                }
            }

            it.remove();
        }
    }

    private static void register(ServerSocketChannel serverSocket, Selector selector) throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    void stop() throws IOException {
        serverSocket.close();
    }

}
