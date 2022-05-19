import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SingleRepositoryDictionaryServer extends Thread{

    private ServerSocket serverSocket;
    private boolean stop;
    private ArrayList<RepositoryAccessProtocol> threadArrayList;
    private Dictionary dictionary;

    public SingleRepositoryDictionaryServer(){
        try {
            this.serverSocket = new ServerSocket(4555);
            stop = false;
            this.threadArrayList = new ArrayList<>();
            this.dictionary = new Dictionary();
        } catch (IOException e) {
            System.out.println("Server Socket not Initialized");
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        System.out.println("Single repository Access server has started");
        while (!stop){
            try {
                Socket s = serverSocket.accept();
                String connectionId = String.valueOf((int)Math.random() * 100) + s.getPort();
                RepositoryAccessProtocol repositoryAccessProtocol = new RepositoryAccessProtocol(dictionary, s, connectionId);
                threadArrayList.add(repositoryAccessProtocol);
                repositoryAccessProtocol.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setStop(boolean flag){
        this.stop = flag;
    }

    public void turnOfAllChildThreads(){
        for(RepositoryAccessProtocol thread: threadArrayList){
            thread.setStop(true);
        }
    }

}
