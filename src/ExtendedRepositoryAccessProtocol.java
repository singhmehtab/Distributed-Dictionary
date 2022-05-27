import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class ExtendedRepositoryAccessProtocol extends RepositoryAccessProtocol{
    private static HashMap<String, String[]> addressMap = new HashMap<>();

    public ExtendedRepositoryAccessProtocol(Dictionary dictionary, Socket socket, String connectionName, HashMap<String, String[]> addressMap) {
        super(dictionary, socket, connectionName);
        this.addressMap = addressMap;
    }

    @Override
    public void run() {
        printWriter.println("Hi!");
        printWriter.println("Your Connection name is " + connectionName + ".");
        Set<String> currentlyAvailableServers = addressMap.keySet();
//        printWriter.println("Currently Available Servers Are -> ");
//        for(String serverName : currentlyAvailableServers){
//            printWriter.println(serverName);
//        }
        printWriter.flush();
        while(!stop) {
            String data = scanner.nextLine();
            String[] clientCommand = data.split(" ");
            String operation = clientCommand[0];
            if(clientCommand[0].contains("RESET") && clientCommand[0].split("\\.").length>1){
                handleRemoteRepositoryRequest(clientCommand[0].split("\\."), clientCommand, "RESET");
                continue;
            }
            switch (operation) {
                case "ADD": {
                    if (checkAndRespond(3, clientCommand)) {
                        continue;
                    }
                    String[] clientCommandAdvancedSplit = advancedSplit(clientCommand[1]);
                    if(clientCommandAdvancedSplit.length == 2){
                        handleRemoteRepositoryRequest(clientCommandAdvancedSplit, clientCommand, "ADD");
                    }
                    else {
                        dictionary.add(clientCommand[1], Integer.parseInt(clientCommand[2]));
                        printWriter.println("OK");
                        printWriter.flush();
                    }
                    break;
                }
                case "SET": {
                    if (checkAndRespond(3, clientCommand)) {
                        continue;
                    }
                    String[] clientCommandAdvancedSplit = advancedSplit(clientCommand[1]);
                    if(clientCommandAdvancedSplit.length == 2){
                        handleRemoteRepositoryRequest(clientCommandAdvancedSplit,clientCommand, "SET");
                    }
                    else {
                        dictionary.set(clientCommand[1], Integer.parseInt(clientCommand[2]));
                        printWriter.println("OK");
                        printWriter.flush();
                    }
                    break;
                }
                case "DELETE": {
                    if (checkAndRespond(2, clientCommand)) {
                        continue;
                    }
                    String[] clientCommandAdvancedSplit = advancedSplit(clientCommand[1]);
                    if(clientCommandAdvancedSplit.length == 2){
                        handleRemoteRepositoryRequest(clientCommandAdvancedSplit, clientCommand, "DELETE");
                    }
                    else {
                        dictionary.delete(clientCommand[1]);
                        printWriter.println("OK");
                        printWriter.flush();
                    }
                    break;
                }
                case "LIST": {
                    if (!clientCommand[1].contains("KEYS")) {
                        printWriter.println(getCommandInvalidText());
                        printWriter.flush();
                        continue;
                    }
                    String[] clientCommandAdvancedSplit = advancedSplit(clientCommand[1]);
                    if(clientCommandAdvancedSplit.length == 2){
                        handleRemoteRepositoryRequest(clientCommandAdvancedSplit, clientCommand, "LIST KEYS");
                    }
                    else {
                        String keys = dictionary.listKeys();
                        printWriter.println(keys);
                        printWriter.flush();
                    }
                    break;
                }
                case "GET": {
                    if (checkAndRespond(3, clientCommand)) {
                        continue;
                    }
                    String[] clientCommandAdvancedSplit = advancedSplit(clientCommand[1]);
                    if(clientCommandAdvancedSplit.length==2){
                        if(clientCommandAdvancedSplit[1].equals("VALUE")){
                            handleRemoteRepositoryRequest(clientCommandAdvancedSplit, clientCommand, "GET VALUE");
                        }
                        else if(clientCommandAdvancedSplit[1].equals("VALUES")){
                            handleRemoteRepositoryRequest(clientCommandAdvancedSplit, clientCommand, "GET VALUES");
                        }
                        else {
                            printWriter.println(getCommandInvalidText());
                            printWriter.flush();
                        }
                    }
                    else {
                        if (clientCommand[1].equals("VALUE")) {
                            printWriter.println(dictionary.getValue(clientCommand[2]));
                        } else if (clientCommand[1].equals("VALUES")) {
                            printWriter.println(dictionary.getValues(clientCommand[2]));
                        } else printWriter.println(getCommandInvalidText());
                        printWriter.flush();
                    }
                    break;
                }
                case "SUM":{
                    if(checkAndRespond(2, clientCommand)){continue;}
                    printWriter.println(dictionary.sum(clientCommand[1]));
                    printWriter.flush();
                    break;
                }
                case "MAX":{
                    if(checkAndRespond(2, clientCommand)){continue;}
                    printWriter.println(dictionary.max(clientCommand[1]));
                    printWriter.flush();
                    break;
                }
                case "DSUM": {
                    if (clientCommand.length == 2) {
                        printWriter.println(dictionary.sum(clientCommand[1]));
                        printWriter.flush();
                    }
                    else if(clientCommand[2].equals("INCLUDING")){
                        int sum = Integer.parseInt(dictionary.sum(clientCommand[1]));
                        for(int i=3;i<clientCommand.length;i++){
                            String serverName = clientCommand[i];
                            String[] remoteRepositoryAddress = addressMap.get(serverName);
                            if(remoteRepositoryAddress == null) {
                                printWriter.println("ERR Non-existence or ambiguous repository " + serverName);
                                printWriter.flush();
                                break;
                            }
                            try {
                                Socket socket = new Socket(remoteRepositoryAddress[0], Integer.parseInt(remoteRepositoryAddress[1]));
                                Scanner tempInputStream = new Scanner(socket.getInputStream());
                                PrintWriter tempWriter = new PrintWriter(socket.getOutputStream());
                                tempWriter.println("SUM " + clientCommand[1]);
                                tempWriter.flush();
                                tempInputStream.nextLine();
                                tempInputStream.nextLine();
                                String response = tempInputStream.nextLine();
                                if(response.equals("error") || response.equals("INVALID_COMMAND")){
                                    printWriter.println("error");
                                    printWriter.flush();
                                    break;
                                }
                                sum += Integer.parseInt(response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        printWriter.println(sum);
                        printWriter.flush();
                    }
                    else {
                        printWriter.println(getCommandInvalidText());
                        printWriter.flush();
                    }
                    break;
                }
                case "DMAX": {
                    if (clientCommand.length == 2) {
                        printWriter.println(dictionary.max(clientCommand[1]));
                        printWriter.flush();
                        break;
                    }
                    else if(clientCommand[2].equals("INCLUDING")){
                        int max = Integer.parseInt(dictionary.max(clientCommand[1]));
                        for(int i=3;i<clientCommand.length;i++){
                            String serverName = clientCommand[i];
                            String[] remoteRepositoryAddress = addressMap.get(serverName);
                            if(remoteRepositoryAddress == null) {
                                printWriter.println("ERR Non-existence or ambiguous repository " + serverName);
                                printWriter.flush();
                                break;
                            }
                            try {
                                Socket socket = new Socket(remoteRepositoryAddress[0], Integer.parseInt(remoteRepositoryAddress[1]));
                                Scanner tempInputStream = new Scanner(socket.getInputStream());
                                PrintWriter tempWriter = new PrintWriter(socket.getOutputStream());
                                tempWriter.println("MAX " + clientCommand[1]);
                                tempWriter.flush();
                                tempInputStream.nextLine();
                                tempInputStream.nextLine();
                                String response = tempInputStream.nextLine();
                                if(response.equals("error")){
                                    printWriter.println("error");
                                    printWriter.flush();
                                    break;
                                }
                                max = Math.max(max, Integer.parseInt(response));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        printWriter.println(max);
                        printWriter.flush();
                    }
                    else {
                        printWriter.println(getCommandInvalidText());
                        printWriter.flush();
                        continue;
                    }
                    break;
                }
                case "RESET": {
                    if(clientCommand.length == 2){
                        String[] clientCommandAdvancedSplit = advancedSplit(clientCommand[1]);
                        handleRemoteRepositoryRequest(clientCommandAdvancedSplit, clientCommand, "RESET");
                    }
                    else {
                        dictionary.resetAll();
                        printWriter.println("OK");
                        printWriter.flush();
                    }
                    break;
                }
                case "EXIT": {
                    this.stop = true;
                    return;
                }
                default: {
                    printWriter.println("INVALID COMMAND");
                    printWriter.flush();
                    break;
                }
            }
        }
    }

    public void performRemoteRepositoryAction(String[] remoteRepositoryAddress, String[] keyServerSplit, String[] clientCommand, String action) throws IOException {
        Socket socket = new Socket(remoteRepositoryAddress[0], Integer.parseInt(remoteRepositoryAddress[1]));
        Scanner tempInputStream = new Scanner(socket.getInputStream());
        PrintWriter tempWriter = new PrintWriter(socket.getOutputStream());
        tempWriter.println(buildCommand(clientCommand, keyServerSplit, action));
        tempWriter.flush();
        tempInputStream.nextLine();
        tempInputStream.nextLine();
        String response = tempInputStream.nextLine();
        if(response.equals("error")){
            printWriter.println("error");
            printWriter.flush();
        }
        else{
            printWriter.println(response);
            printWriter.flush();
        }
    }

    public String buildCommand(String[] clientCommand, String[] keyServerSplit, String action){
        switch (action){
            case "ADD": {
                return clientCommand[0] + " " + keyServerSplit[1] + " " + clientCommand[2];
            }
            case "SET":{
                return clientCommand[0] + " " + keyServerSplit[1] + " " + clientCommand[2];
            }
            case "DELETE":{
                return clientCommand[0] + " " + keyServerSplit[1];
            }
            case "LIST KEYS":{
                return action;
            }
            case "GET VALUE":{
                return action + " " + clientCommand[2];
            }
            case "GET VALUES":{
                return action + " " + clientCommand[2];
            }
            case "RESET":{
                return action;
            }
            default: return "";
        }
    }

    private boolean checkAndRespond(Integer requiredLength, String[] clientCommand){
        if(clientCommand.length != requiredLength){
            printWriter.println(getCommandInvalidText());
            printWriter.flush();
            return true;
        }
        return false;
    }

    private String[] advancedSplit(String command){
        return command.split("\\.");
    }

    private void handleRemoteRepositoryRequest(String[] clientCommandAdvancedSplit, String [] clientCommand, String action){
        String serverName = clientCommandAdvancedSplit[0];
        String[] remoteRepositoryAddress = addressMap.get(serverName);
        if(remoteRepositoryAddress == null) {
            try {
                BroadcastSender.sendBroadCast(null, InetAddress.getLocalHost(), socket.getPort(), true, serverName);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        remoteRepositoryAddress = addressMap.get(serverName);
        if(remoteRepositoryAddress == null) {
            printWriter.println("ERR Non-existence or ambiguous repository " + serverName);
            printWriter.flush();
            return;
        }
        try {
            performRemoteRepositoryAction(remoteRepositoryAddress,clientCommandAdvancedSplit, clientCommand, action);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
