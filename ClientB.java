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


public class ClientB  extends Thread{
    protected static Thread threads[];
    public static int accounts[] = new int[100];
    protected static int iterationCount;
    public static RMIServer server;
    public final String filename= "clientLogfile";
    public synchronized void writeLog(String content){
        try{
            FileWriter fw = new FileWriter(filename,true);
            fw.write("Thread ID:"+Thread.currentThread().getId()+" "+content+"\n");
            fw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void run(){
        try{
            int i;
            Random rand = new Random();
          
            for(i=0;i<iterationCount;i++){
                int  n1 = rand.nextInt(100),n2;
                do{
                    n2 = rand.nextInt(100);
                }while(n2==n1);
                
                TransferResponse  tr = server.transfer(new TransferRequest(accounts[n1],accounts[n2],10));
                
                if(!tr.status.equals("OK")){
                  writeLog("Respone = "+tr.status+" from account :"+n1+" to account :"+n2);
                }
            }
        }catch(RemoteException e){
            e.printStackTrace();
        }
    }
    public static int getTotalBalance() throws RemoteException{
        int i,total =0 ;
        for ( i =0; i < 100; i++){
            total = total +  server.getBalance(new GetBalanceRequest(accounts[i])).balance;
        }
        return total;
    }
    public static void main (String args[]) throws Exception {
        if (args.length != 3)
            throw new RuntimeException ("Syntax: ClientB <hostname>");
        //System.setSecurityManager (new RMISecurityManager ());
        int i;
        int threadcount = Integer.parseInt(args[1]);
        iterationCount = Integer.parseInt(args[2]);
        threads = new Thread[threadcount];

        server = (RMIServer) Naming.lookup ("//" + args[0] + "/RMIServer");
        
        System.out.println("Step 1:");
        for(i =0 ;i < 100; i++){
            accounts[i] =  server.newAccount(new NewAccountRequest("person1","a","b")).accountId;       
        }
        
        System.out.println("Step 2:");
        for(i=0;i < 100; i++){
            server.deposit(new DepositRequest(accounts[i],100));
        }
        
        System.out.println("Step 3:");
        int total = getTotalBalance();
        System.out.println("Total Amount = "+total);
    

        System.out.println("Step 4:");
        for(i=0;i<threadcount;i++){
            threads[i] = new ClientB();
            threads[i].start();
        }
        System.out.println("Step 5:");
        
        for(i=0;i<threadcount;i++){
            threads[i].join();
        }
    
        System.out.println("Step 6:");
        total = getTotalBalance();
        System.out.println("Total Amount = "+total);
    

    }
}

