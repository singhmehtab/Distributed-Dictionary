import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class BroadcastListener extends Thread{

    private HashMap<String, String[]> addressMap;
    private boolean stop;
    private DatagramSocket socket;
    private static InetSocketAddress socketAddress = new InetSocketAddress(8889);
    private String myName;
    private InetAddress tcpInetAddress;
    private Integer myPort;

    public BroadcastListener(HashMap<String, String[]> addressMap, String myName, InetAddress tcpInetAddress, Integer myPort) throws SocketException {
        this.addressMap = addressMap;
        stop = false;
        socket = new DatagramSocket(null);
        socket.setBroadcast(true);
        socket.setReuseAddress(true);
        socket.bind(socketAddress);
        this.myName = myName;
        this.tcpInetAddress = tcpInetAddress;
        this.myPort = myPort;
    }

    @Override
    public void run(){
        while(!stop){
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String dataReceived = new String(packet.getData(), 0, packet.getLength());
                dataReceived = dataReceived.replaceAll("\0", "");
                String[] splitData = dataReceived.split(":");
                if(splitData[0].equals("SEND_ADDRESS") && splitData[1].equals(myName)){
                    String myAddress = tcpInetAddress.getHostAddress();
                    myAddress = myAddress.replaceAll(",", ".").replaceAll(" ", "");
                    String broadCastMessage = myName + ":" + myAddress + "/" + myPort;
                    socket.send(new DatagramPacket(broadCastMessage.getBytes(StandardCharsets.UTF_8), broadCastMessage.getBytes(StandardCharsets.UTF_8).length, packet.getAddress(), packet.getPort()));
                }
                else {
                    String[] address = splitData[1].split("/");
                    addressMap.put(splitData[0], address);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setStop(boolean flag){
        this.stop = false;
    }

}
