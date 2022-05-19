import java.util.Scanner;

public class SingleRepositoryExample {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Welcome to Single Repository Access Dictionary");
        System.out.println("Server is going to start");
        SingleRepositoryDictionaryServer singleRepositoryDictionaryServer = new SingleRepositoryDictionaryServer();
        singleRepositoryDictionaryServer.start();
        Thread.sleep(500);
        System.out.println("Type and Press exit to stop the server");
        Scanner sc = new Scanner(System.in);
        if(exitInputEntered(sc)){
            System.out.println("Server is turning off");
            singleRepositoryDictionaryServer.turnOfAllChildThreads();
            singleRepositoryDictionaryServer.setStop(true);
            singleRepositoryDictionaryServer.interrupt();
        }
    }

    public static boolean exitInputEntered(Scanner scanner){
        while (true) {
            String sc = scanner.nextLine();
            if (sc.equals("exit"))
                return true;
            else{
                System.out.println("Wrong input entered");
            }
        }
    }

}
