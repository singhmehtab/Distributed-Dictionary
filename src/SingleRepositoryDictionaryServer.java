import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SingleRepositoryDictionaryServer extends Thread{

    protected ServerSocket serverSocket;
    protected boolean stop;
    protected ArrayList<RepositoryAccessProtocol> threadArrayList;
    protected Dictionary dictionary;
    protected String myName;

    public SingleRepositoryDictionaryServer(String myName, int port){
        try {
            this.serverSocket = new ServerSocket(port);
            stop = false;
            this.threadArrayList = new ArrayList<>();
            this.dictionary = new Dictionary();
            this.myName = myName;
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
