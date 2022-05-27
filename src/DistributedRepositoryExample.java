import java.net.SocketException;
import java.net.UnknownHostException;


public class DistributedRepositoryExample {

    public static void main(String[] args) throws SocketException, UnknownHostException {
        System.out.println("Welcome to Distributed Repository Access Dictionary");
        System.out.println("Servers are going to start");
        DistributedRepositoryDictionaryServer distributedRepositoryDictionaryServer1 = new DistributedRepositoryDictionaryServer("Tom", 4560);
        DistributedRepositoryDictionaryServer distributedRepositoryDictionaryServer2 = new DistributedRepositoryDictionaryServer("Cat", 4561);
        DistributedRepositoryDictionaryServer distributedRepositoryDictionaryServer3 = new DistributedRepositoryDictionaryServer("Charlie", 4562);
        distributedRepositoryDictionaryServer1.start();
        distributedRepositoryDictionaryServer2.start();
        distributedRepositoryDictionaryServer3.start();
    }

}
