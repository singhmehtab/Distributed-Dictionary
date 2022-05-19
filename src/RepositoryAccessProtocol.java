import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;

public class RepositoryAccessProtocol extends Thread{

    private Dictionary dictionary;
    private boolean stop = false;
    private Socket socket;
    private Scanner scanner;
    private PrintWriter printWriter;
    private String connectionName;

    public RepositoryAccessProtocol(Dictionary dictionary, Socket socket, String connectionName){
        this.dictionary = dictionary;
        this.socket = socket;
        this.connectionName = connectionName;
        try {
            scanner = new Scanner(socket.getInputStream());
            printWriter = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Failed to Initialize reader and writer");
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        printWriter.println("Hi!\nYour Connection name is " + connectionName + ".");
        printWriter.flush();
        while(!stop){
            String data = scanner.nextLine();
            String[] strings = data.split(" ");
            String operation = strings[0];
           switch (operation){
               case "ADD":{
                   if(strings.length != 3){printWriter.println(getCommandInvalidText());printWriter.flush();continue;}
                   dictionary.add(strings[1], Integer.parseInt(strings[2]));
                   printWriter.println("OK");
                   printWriter.flush();
                   break;
               }
               case "SET":{
                   if(strings.length != 3){printWriter.println(getCommandInvalidText());printWriter.flush();continue;}
                   dictionary.set(strings[1], Integer.parseInt(strings[2]));
                   printWriter.println("OK");
                   printWriter.flush();
                   break;
               }
               case "DELETE":{
                   if(strings.length != 2){ printWriter.println(getCommandInvalidText());printWriter.flush();continue;}
                   dictionary.delete(strings[1]);
                   printWriter.println("OK");
                   printWriter.flush();
                   break;
               }
               case "LIST":{
                   if(!strings[1].equals("KEYS")){ printWriter.println(getCommandInvalidText());printWriter.flush();continue;}
                   Set<String> keys = dictionary.listKeys();
                   for(String s : keys){
                       printWriter.println(s);
                   }
                   printWriter.flush();
                   break;
               }
               case "GET":{
                   if(strings.length != 3){ printWriter.println(getCommandInvalidText());printWriter.flush();continue;}
                   if(strings[1].equals("VALUE")){
                       printWriter.println(dictionary.getValue(strings[2]));
                   }
                   else if(strings[1].equals("VALUES")){
                       printWriter.println(dictionary.getValues(strings[2]));
                   }
                   else printWriter.println("Command format not valid");
                   printWriter.flush();
                   break;
               }
               case "SUM":{
                   if(strings.length != 2){ printWriter.println(getCommandInvalidText());printWriter.flush();continue;}
                   printWriter.println(dictionary.sum(strings[1]));
                   printWriter.flush();
                   break;
               }
               case "MAX":{
                   if(strings.length != 2){ printWriter.println(getCommandInvalidText());printWriter.flush();continue;}
                   printWriter.println(dictionary.max(strings[1]));
                   printWriter.flush();
                   break;
               }
               case "RESET":{
                   dictionary.resetAll();
                   printWriter.println("OK");
                   printWriter.flush();
                   break;
               }
               case "EXIT":{
                   this.stop = true;
                   return;
               }
               default:{
                   printWriter.println("INVALID COMMAND");
                   printWriter.flush();
                   break;
               }
           }
        }
    }

    public void setStop(boolean flag){
        this.stop = flag;
    }

    private String getCommandInvalidText(){
        return "Command format not valid";
    }

}
