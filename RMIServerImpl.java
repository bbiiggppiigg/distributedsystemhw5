import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.Naming;
import java.rmi.registry.*;
import java.io.*;
import java.util.*;
import Requests.*;
import Responses.*;

//sudo java HW2ServerImpl 5555 -Djava.rmi.server.codebase=file:/Users/bbiiggppiigg/rmi
public class RMIServerImpl extends UnicastRemoteObject implements RMIServer , RMIBroadcastServer{
	private static RMIServerImpl server;
	public static int id;
	public static long lamportClock;
	public static AccountManager accountManager;
	public final String filename= "serverLogfile";
	public static String peerInfo[] ;
	public static int numPeers;
	public static RMIBroadcastServer peers[];
	public static PriorityQueue<Ticket> pq = new PriorityQueue<Ticket>();
	public static int requestCount;
	public static long respondTime;
	public static Calendar calendar = Calendar.getInstance();
	public synchronized void recordPerformance(long duration){
		respondTime = respondTime + duration;
		requestCount = requestCount+1;
	//	System.out.println("RespondTime = "+respondTime+" Duration = "+duration);
	}
	
	class Ticket implements Comparable<Ticket>{
		public long timestamp;
		public int pid;
		public Request req;
		Ticket(long timestamp, int id,Request req){
			this.timestamp = timestamp;
			this.pid = id;
			this.req = req;
		}
		public int compareTo(Ticket t ){
			if(this.timestamp > t.timestamp)
				return 1;
			else if (this.timestamp < t.timestamp)
				return -1;
			else{
				if(this.pid < t.pid)
					return -1;
				else if(this.pid == t.pid)
					return 0;
				else
					return 1;
			}
		}
	};

	public synchronized void writeLog(String content) {
		try{
		FileWriter fw = new FileWriter(filename+id,true);
     	fw.write(content+"\n");
		fw.close();
		}catch(IOException e){

		}
	}

	public synchronized long setLamportClock(long timestamp){
		this.lamportClock  = Math.max(lamportClock,timestamp);
		return this.lamportClock;
	};
	public synchronized void assignTimestamp(Request r){
		r.timestamp = ++lamportClock;
	}
	public synchronized void increaseClock(long timestamp){
		lamportClock = timestamp+1;
	}
	public synchronized void increaseClock(){
		lamportClock++;
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
	RMIServerImpl() throws RemoteException{
		lamportClock = 0;
		requestCount = 0;
		respondTime  = 0;
	}
	public NewAccountResponse newAccount(NewAccountRequest nar) throws RemoteException{
		NewAccountResponse narr = new NewAccountResponse("Fail",-1); 
	    try{
	      int id = accountManager.newAccount(nar.firstname,nar.lastname,nar.address);
	      narr = new NewAccountResponse("OK",id);
	    }catch(Exception e){
	      narr.setStatus(e.toString());
    	}finally{
    			//writeLog("Request of type "+nar.requestType+", firstname = "+nar.firstname+" lastname = "+nar.lastname+" address= "+nar.address+" status = "+narr.status);
    	}
		return narr;
	};
	public DepositResponse deposit(DepositRequest dr) throws RemoteException{
		DepositResponse dp =  new DepositResponse("OK");
	    try{
	      Account account = accountManager.getAccountById(dr.accountId);
	      if(account!=null){
	        synchronized(account){
	          account.deposit(dr.amount);
	        }
	      }else{
	        throw new RuntimeException("Account ID not found");
	      }
	    }catch(Exception e){
	      dp.setStatus(e.toString());
	    }finally{
	    		//writeLog("Handling request of type "+dr.requestType+", account ID = "+dr.accountId+" amount = "+dr.amount+ " status = "+dp.status);
	    }
	    return dp;
	};
	public GetBalanceResponse getBalance(GetBalanceRequest gbr) throws RemoteException{
		GetBalanceResponse gbrr = new GetBalanceResponse("Fail",-1);
	    int balance;
	    try{
	      Account account = accountManager.getAccountById(gbr.accountId);
	      if(account!=null){
	        synchronized(account){
	          balance = account.getBalance();
	        }
	      }else{
	        throw new RuntimeException("Account Id not found");
	      }
	      gbrr = new GetBalanceResponse("OK",balance);
	    }catch(Exception e){
	      gbrr.setStatus(e.toString());
	    }finally{
	    		//writeLog("Handling request of type "+gbr.requestType+", account ID = "+gbr.accountId+" status = "+gbrr.status);
	    }
		return gbrr;
	};
	public TransferResponse transfer(TransferRequest tr) throws RemoteException{
		TransferResponse trr =  new TransferResponse("OK");
	    try{
	      Account source = accountManager.getAccountById(tr.sourceId);
	      Account target = accountManager.getAccountById(tr.targetId);
	      if(source!=null){
	        if(target!=null){
	          if(tr.sourceId<tr.targetId){
				  	synchronized(source){
			            	synchronized(target){
			              		source.withdraw(tr.amount);
			              		target.deposit(tr.amount);
			            	}
			          	}
				}else{
					synchronized(target){
						synchronized(source){
							source.withdraw(tr.amount);
							target.deposit(tr.amount);
						}
					}
				}
	        }else{
	          throw new RuntimeException("Target ID not FOUND");
	        }
	      }else{
	        throw new RuntimeException("Source ID not FOUND");
	      }
	    }catch(Exception e){
	      trr.setStatus(e.toString());
	    }finally{
	    //		writeLog("Handling request of type "+tr.requestType+", from account ID = "+tr.sourceId+"to accountId"+tr.targetId+" amount = "+tr.amount+" status = "+trr.status);
	    }
		return trr;
	};
	public WithdrawResponse withdraw(WithdrawRequest wr) throws RemoteException{
		WithdrawResponse wrr = new WithdrawResponse("OK");
	    try{
	      Account account = accountManager.getAccountById((wr.accountId));
	      if(account!=null){
	        synchronized(account){
	          account.withdraw(wr.amount);
	        }
	      }else{
	        throw new RuntimeException("Account ID not Found");
	      }
	    }catch(Exception e){
	      wrr.setStatus(e.toString());
	    }finally{
	    	//	writeLog("Handling request of type "+wr.requestType+", account ID = "+wr.accountId+" amount = "+wr.amount+" status = "+wrr.status);		
	    	
	    }
		return wrr;
	};
	class PeerThread extends Thread{
		int id ;
		PeerThread(int i){
			id = i;
		}
		public void run(){
			while(true){
				try{
					peers[id] = (RMIBroadcastServer) Naming.lookup ("//" + peerInfo[id] + "/RMIBroadcastServer");
				}catch(Exception e){
					System.out.println("Failed to connect to server of id "+ (id+1) +" with "+e);
					System.out.println("Retry in 1 seconds");
					try{
						Thread.sleep(1000);
					}catch(Exception e2){
						e2.printStackTrace();

					}finally{
						continue;
					}
					
				}
				break;
			}	
		}
	}

	public void buildPeerConnections(){
		PeerThread pt [] = new PeerThread[numPeers];
		System.out.println("Building Peer Connections");
		int i =0 ; 
		
		peers = new RMIBroadcastServer[numPeers];
		for (i =0 ; i < numPeers ;i++){
			if(i == id - 1) continue;
			pt[i] = new PeerThread(i);
			pt[i].start();
		}
		for (i =0 ; i < numPeers ;i++){
			if(i == id - 1) continue;
			try{
				pt[i].join();
			}catch(Exception e){
			}
		}
		System.out.println("Peer Connections Built Successfully");
	};
	
	public void executeRequest(Request r,int pid) throws RemoteException{
		writeLog(pid+" Process  "+calendar.getTime()+" "+r.timestamp);
		switch(r.requestType){
			case "Deposit":
				deposit((DepositRequest) r);
				break;
			case "GetBalance":
				getBalance((GetBalanceRequest) r);
				break;
			case "NewAccount":
				newAccount((NewAccountRequest) r);
				break;
			case "Transfer":
				transfer((TransferRequest) r);
				break;
			case "Withdraw":
				withdraw((WithdrawRequest) r);
				break;
			case "Halt":
				halt();
				break;
			default:
				throw new RemoteException("Unsupported Request");	
		}
		
		/*synchronized(pq){
			pq.notifyAll();
		}*/
	};

	public void halt(){
		System.out.println("Printing Balance Info :");
		accountManager.printAccountsBalance();
		System.out.println("Printing Pending Requests");
		while(pq.peek()!=null){
			System.out.println(pq.poll().req);
		}
		if(requestCount != 0 )
			System.out.println("The average respone time for "+ requestCount + " requests is "+ (respondTime/requestCount) +" nanoseconds. ");
		else
			System.out.println("This server has no request from the client");

		new HaltThread().start();		
	}
	public Response processRequest(Request r) throws RemoteException{
		Response ret = null;
		writeLog(id+" Process  "+calendar.getTime()+" "+r.timestamp);
		
		switch(r.requestType){
			case "Deposit":
				ret= deposit((DepositRequest) r);
				break;
			case "GetBalance":
				ret= getBalance((GetBalanceRequest) r);
				break;
			case "NewAccount":
				ret= newAccount((NewAccountRequest) r);
				break;
			case "Transfer":
				ret= transfer((TransferRequest) r);
				break;
			case "Withdraw":
				ret= withdraw((WithdrawRequest) r);
				break;
			case "Halt":
				halt();
				ret=  new HaltResponse("Halt");
				break;
			default:
				throw new RemoteException("Unsupported Request");	
		}
		
		synchronized(pq){
			pq.poll();
			pq.notifyAll();
		}
		return ret;
	}
	class HaltThread extends Thread{
		HaltThread(){

		}
		public void run(){
			try{
				Thread.sleep(3000);
				System.exit(1);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	class BCastThread extends Thread{
		int id ;
		Request r ;
		int pid ;
		long timestamp;
		BCastThread(int id,Request r ,int pid ){
			this.id = id;
			this.r = r;
			this.pid = pid;
		}
		public void run(){
			try{
				timestamp = peers[id].requestBCast(r,id);
			}catch(Exception e ){
				e.printStackTrace();
			}
		}
		long getTimestamp(){
			return this.timestamp;
		}
	}
	class BCastExecuteThread extends Thread{
		int id ;
		Request r ;
		int pid ;
		BCastExecuteThread(int id,Request r ,int pid ){
			this.id = id;
			this.r = r;
			this.pid = pid;
		}
		public void run(){
			try{
				peers[id].executeRequest(r,id);
			}catch(Exception e ){
				e.printStackTrace();
			}
		}
	}


	public Response submitRequest(Request r) throws RemoteException{
		BCastThread bct [] = new BCastThread[numPeers];
		BCastExecuteThread bcet [] = new BCastExecuteThread[numPeers];
		
		long ts = System.nanoTime();	
		
		synchronized(pq){	
			assignTimestamp(r);
			Ticket t = new Ticket(r.timestamp,id,r);
			
			pq.add(t);
			while(pq.size()!=0 && pq.peek().compareTo(t)!=0){
				try{
					pq.wait();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		writeLog(id+" CLNT-REQ "+calendar.getTime()+" "+ r);
		int i =0 ;
		/*
			Start Multiple Threads to Broadcast the Request to All Replicated Systems
		*/

		for ( i =0 ;i < numPeers ; i++){
			if(i== id-1) continue;
			bct[i]= new BCastThread(i,r,id); 
			bct[i].start();
		}
		for ( i =0 ;i < numPeers ; i++){
			if(i== id-1) continue;
			try{
				bct[i].join();
			}catch(Exception e){
				e.printStackTrace();
			}

		}
		for ( i =0 ;i < numPeers ; i++){
			if(i== id-1) continue;
			setLamportClock(bct[i].getTimestamp());
		}
		//writeLog(id+" SERVER-ACK "+calendar.getTime()+" "+ r);
		

		/*
			After Getting the Response from all other process, send the request to all otehr process for 

		*/

		for ( i =0 ;i < numPeers ; i++){
			if(i== id-1) continue;
			bcet[i]= new BCastExecuteThread(i,r,id); 
			bcet[i].start();
		}

		for ( i =0 ;i < numPeers ; i++){
			if(i== id-1) continue;
			try{
				bcet[i].join();
			}catch(Exception e){
				e.printStackTrace();
			}

		}
		
		/*
			As the server facing client, prepare the response for client and measure the respone time data.
		*/
		
		Response ret = processRequest(r);
		recordPerformance( System.nanoTime() - ts);
		return ret;

	}
	

	public long requestBCast(Request r, int pid) throws RemoteException{
		
		long tempClock = setLamportClock(r.timestamp);
		writeLog(pid+" SRV-REQ  "+calendar.getTime()+" "+ r);
		Ticket t = new Ticket(r.timestamp, pid , r);
		/*
			If there is currently no request in the local queue, simply ack
			Otherwise, wait on the peek of the queue until you have a smaller timestamp, then return the lamportclock value
		*/
		synchronized(pq){
			while(pq.size()!=0 && pq.peek().compareTo(t)<0){
				try{
					pq.wait();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		//writeLog(pid+" ACK-ING  "+calendar.getTime()+" "+r);
		return tempClock;
	}

	class InitialThread extends Thread{
		public void run(){
			try{
				NewAccountResponse resp =(NewAccountResponse) submitRequest(new NewAccountRequest("p","a","b"));
				submitRequest(new DepositRequest(resp.accountId,1000));;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public void initialization(){
		System.out.println("Start of Initialization");
		InitialThread its[] = new InitialThread[1000];
		int i;
			for(i=0;i<10;i++){
				its[i] = new InitialThread();
				its[i].start();
			}
			for(i=0;i<10;i++){
				try{
					its[i].join();
				}catch(Exception e){
					e.printStackTrace();
				}
	    }
	   	System.out.println("End of Initialization");

	}
	public static void main (String args[]) throws Exception {
		accountManager = new AccountManager();
	    server = new RMIServerImpl ();
	    System.out.println("Before Binding");
	    if(args.length != 2 ){
	    	throw new Exception("Usage : <config file name> <server id>");
	    }else{
	    	readFileByLine(args[0]);
	    	server.id = Integer.parseInt(args[1]);
	       	Registry localRegistry = LocateRegistry.getRegistry(Integer.parseInt((peerInfo[Integer.parseInt(args[1])-1].split(":"))[1]));
	       	localRegistry.bind ("RMIServer", server);	
	    	localRegistry.bind ("RMIBroadcastServer", server);	
	    	server.buildPeerConnections();
	    	
	    	if(server.id==1){
	    		server.initialization();
	    	}
	    	System.out.println("Server "+(id)+" waiting for request");
	    }
  }
}
