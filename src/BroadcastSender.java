import java.io.IOException;
import java.net.*;
import java.util.*;

public class BroadcastSender {

    private static InetSocketAddress socketAddress = new InetSocketAddress( 8889);

    public static void sendBroadCast(String serverName, InetAddress inetAddress, Integer port, boolean enquire, String enquiryServerName){
        try {
            DatagramSocket socket = new DatagramSocket(null);
                    socket.setReuseAddress(true);
                    socket.bind(socketAddress);
                    socket.setBroadcast(true);
            String senderAddress = inetAddress.getHostAddress();
            senderAddress = senderAddress.replaceAll(",", ".").replaceAll(" ", "");
            String broadCastMessage;
            if(enquire){
                broadCastMessage = "SEND_ADDRESS:" + enquiryServerName;
            }
            else {
                broadCastMessage = serverName + ":" + senderAddress + "/" + port;
            }
                byte[] buffer = broadCastMessage.getBytes();

                DatagramPacket packet
                        = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), 8889);

                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            List<InetAddress> inetAddressArrayList = listAllBroadcastAddresses();
            for(InetAddress inetAdd : inetAddressArrayList){
                packet = new DatagramPacket(buffer, buffer.length, inetAdd, 8889);

                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces
                = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(InterfaceAddress::getBroadcast)
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }

}
