import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

public class DistributedRepositoryDictionaryServer extends SingleRepositoryDictionaryServer{

    private static HashMap<String, String[]> addressMap = new HashMap<>();

    public DistributedRepositoryDictionaryServer(String myName, int port) throws UnknownHostException, SocketException {
        super(myName, port);
//        addressMap = new HashMap<>();
        BroadcastSender.sendBroadCast(myName, InetAddress.getLocalHost(), port, false, null);
        BroadcastListener broadcastListener = new BroadcastListener(addressMap, myName, serverSocket.getInetAddress(), port);
        broadcastListener.start();
    }

    @Override
    public void run(){
        System.out.println("Distributed repository Access " + myName + " server has started");
        while (!stop){
            try {
                Socket s = serverSocket.accept();
                String connectionId = String.valueOf((int)Math.random() * 100) + s.getPort();
                ExtendedRepositoryAccessProtocol repositoryAccessProtocol = new ExtendedRepositoryAccessProtocol(dictionary, s, connectionId, addressMap);
                threadArrayList.add(repositoryAccessProtocol);
                repositoryAccessProtocol.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
