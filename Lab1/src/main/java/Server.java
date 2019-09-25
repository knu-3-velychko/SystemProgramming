import jdk.internal.net.http.common.Pair;
import jdk.javadoc.internal.doclets.toolkit.taglets.SeeTaglet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private String address;
    private int port;
    private int variable;
    private int maxConnection;

    private final List<Pair<ByteBuffer, Integer>> clientResponse;

    private AtomicInteger currentConnection;

    private Selector selector;
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
        selector = Selector.open();

        currentConnection.compareAndSet(0, 0);
        serverSocket = ServerSocketChannel.open().bind(new InetSocketAddress(address, port));
        serverSocket.configureBlocking(false);

        int ops = serverSocket.validOps();
        SelectionKey selectionKey = serverSocket.register(selector, ops);

        //keep server running
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();

            while (it.hasNext()) {
                SelectionKey currentKey = it.next();
                if(currentKey.isAcceptable()){
                    SocketChannel clientSocket=serverSocket.accept();
                    clientSocket.configureBlocking(false);
                    clientSocket.register(selector, SelectionKey.OP_READ);
                }
                else if(currentKey.isReadable()){
                    SocketChannel clientSocket=(SocketChannel)currentKey.channel();
                    ByteBuffer buffer=ByteBuffer.allocate(256);
                    clientSocket.read(buffer);
                    String result= new String(buffer.array()).trim();

                    if(Double.parseDouble(result)==0.0){
                        clientSocket.close();
                    }
                }
            }

            it.remove();
        }
    }

    void stop() throws IOException {
        serverSocket.close();
    }

    private synchronized void write(SocketChannel socketChannel, final int var) {

    }

    private synchronized void read(SocketChannel socketChannel) {

    }

}
