import java.rmi.RMISecurityManager;
import java.rmi.Naming;
import java.util.Date;
import java.rmi.RemoteException;
import java.util.*;
import java.io.*;
import Requests.*;
import Responses.*;


/*  Usage   -  java DateClient Server-Host-DNS-name:rmiregistry-port-number-on-that-host
    E.g.       java DateClient deca.cs.umn.edu:60000
               Server is running on deca.cs.umn.edu and the rmiregistry on that host is on port 60000
*/


public class ClientE  extends Thread{
    protected static Thread threads[];
    protected static int iterationCount;
    public RMIServer server;
    public static String [] peerInfo ;
    public final String filename= "clientLogfile";
    public static int numPeers;
    public int server_id;
    public static Calendar calendar = Calendar.getInstance();
    ClientE(int id){
        server_id = id;
        try{
            server = (RMIServer) Naming.lookup ("//" + peerInfo[server_id] + "/RMIServer");
        }catch(Exception e){
            e.printStackTrace();
        }
    
    }

    public synchronized void writeLog(String content){
        try{
            FileWriter fw = new FileWriter("logs/"+filename+(server_id+1),true);
            fw.write(content+"\n");
            fw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void readFileByLine(String fileName) throws Exception {
      try {
       File file = new File(fileName);
       Scanner scanner = new Scanner(file);
       numPeers = Integer.parseInt(scanner.nextLine());
       peerInfo = new String [numPeers];
       int i = 0;
       while (scanner.hasNextLine()) {
            peerInfo[i]  = scanner.nextLine();  
            i++;
       }
       scanner.close();
      } catch (FileNotFoundException e) {
       e.printStackTrace();
      } 
    }
    public void run(){

        /*Each client thread will perform the following operations 100 times and terminate.
        􏰀 It will randomly pick two accounts and transfer 10 from one account to the other.
        􏰀 It will write to the client logfile a record indicating the operation request and server process
        ID.
        ID “REQ” Physical-clock-time Operation-name Parameters
        􏰀 It will also write a log record when a response is received. ID “RSP” Physical-clock-time Response status
*/
        try{
            int i;
            Random rand = new Random();
          
            for(i=0;i<100;i++){
                int  n1 = rand.nextInt(10)+1,n2;
                do{
                    n2 = rand.nextInt(10)+1;
                }while(n2==n1);
                Request r = new TransferRequest(n1,n2,10);
                writeLog((server_id+1)+ " REQ "+calendar.getTime()+" "+r);
                TransferResponse  tr = (TransferResponse) server.submitRequest(r);
                writeLog((server_id+1)+ " RSP "+calendar.getTime()+" "+tr);
            }
        }catch(RemoteException e){
            e.printStackTrace();
        }
    }

    public static void main (String args[]) throws Exception {
        if (args.length != 1)
            throw new RuntimeException ("Syntax: ClientB <hostname> 100 10");
        int i;
        readFileByLine(args[0]);
        ClientE clients [] = new ClientE[numPeers];
        Random rand = new Random();
        for(i =0 ; i < numPeers ;i ++){
            clients[i] = new ClientE(i);
            clients[i].start();
        }
        for(i =0 ; i < numPeers ;i ++){
            clients[i].join();
        }
        clients[0].server.submitRequest(new HaltRequest());

    }
}

