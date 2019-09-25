import java.io.IOException;

public class Manager {
    private Server server;

    private int serverPort;
    private int clientConnectionNumber;

    //    private List<Pair<ByteBuffer, Integer>> clientResponse;
//    private SerializableFunction<Integer, Integer>[] clientFunctions;
    Manager(String address, int var){
        server=new Server(address,serverPort,var,clientConnectionNumber);
        try {
            server.start();
        }
        catch (IOException exp){
            exp.printStackTrace();
        }
    }
}
