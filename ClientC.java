import java.rmi.RMISecurityManager;
import java.rmi.Naming;
import java.util.Date;
import Requests.*;
import Responses.*;

/*  Usage   -  java DateClient Server-Host-DNS-name:rmiregistry-port-number-on-that-host
    E.g.       java DateClient deca.cs.umn.edu:60000
               Server is running on deca.cs.umn.edu and the rmiregistry on that host is on port 60000
*/


public class ClientC {
  public static void main (String args[]) throws Exception {
    if (args.length != 1)
      throw new RuntimeException ("Syntax: ClientA <hostname>");
    //System.setProperty("java.rmi.server.codebase","file:/root/ds/hw2/rmiserver/");
    //System.setSecurityManager (new RMISecurityManager ());
    RMIServer server = (RMIServer) Naming.lookup ("//" + args[0] + "/RMIServer");
    System.out.println("Step 1:");
    NewAccountResponse person1 = (NewAccountResponse)(server.submitRequest(new NewAccountRequest("person1","a","b")));
    NewAccountResponse person2 = (NewAccountResponse)(server.submitRequest(new NewAccountRequest("person2","a","b")));
    System.out.println("The result of creating an account for person1 is "+person1.status+" with id "+person1.accountId);
    System.out.println("The result of creating an account for person2 is "+person2.status+" with id "+person2.accountId);


    System.out.println("Step 2:");
    System.out.println("The result of depositing 100 into person1's account is "+((DepositResponse)server.submitRequest(new DepositRequest(person1.accountId,100))).status);
    System.out.println("The result of depositing 100 into person2's account is "+((DepositResponse)server.submitRequest(new DepositRequest(person2.accountId,100))).status);
       
    System.out.println("Step 3:");
    System.out.println("The balance of first person is :" + ((GetBalanceResponse) server.submitRequest(new GetBalanceRequest(person1.accountId))).balance);
    System.out.println("The balance of second person is :" + ((GetBalanceResponse)server.submitRequest(new GetBalanceRequest(person2.accountId))).balance);
    
    System.out.println("Step 4:");
    System.out.println("Status of transfering 100 from person1 to person2 is "+((TransferResponse) server.submitRequest(new TransferRequest(person1.accountId,person2.accountId,100))).status);
    
    System.out.println("Step 5:");
    System.out.println("The balance of first person is :" + ((GetBalanceResponse)server.submitRequest(new GetBalanceRequest(person1.accountId))).balance);
    System.out.println("The balance of second person is :" + ((GetBalanceResponse)server.submitRequest(new GetBalanceRequest(person2.accountId))).balance);
    
    System.out.println("Step 6:");
    System.out.println("The result for withdrawing 100 from person1 is "+server.submitRequest(new WithdrawRequest(person1.accountId,100)).status);
    System.out.println("The result for withdrawing 100 from person2 is "+server.submitRequest(new WithdrawRequest(person2.accountId,100)).status);

    System.out.println("Step 7:");
    System.out.println("Status of transfering 100 from person1 to person2 is "+server.submitRequest(new TransferRequest(person1.accountId,person2.accountId,100)).status);
    
    System.out.println("Step 8:");
    System.out.println("The balance of first person is :" +  ((GetBalanceResponse)server.submitRequest(new GetBalanceRequest(person1.accountId))).balance);
    System.out.println("The balance of second person is :" + ((GetBalanceResponse)server.submitRequest(new GetBalanceRequest(person2.accountId))).balance);
    server.submitRequest(new HaltRequest());
  }
}

