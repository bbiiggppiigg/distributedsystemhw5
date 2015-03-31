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
	}
	public static void Debug(String s){
	//	System.out.println(s);
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
				if(this.pid > this.pid)
					return 1;
				else
					return -1;
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

	public static void buildPeerConnections(){
		System.out.println("Building Peer Connections");
		int i =0 ; 
		peers = new RMIBroadcastServer[numPeers];
		for (i =0 ; i < numPeers ;i++){
			if(i == id - 1) continue;
			while(true){
				try{
					peers[i] = (RMIBroadcastServer) Naming.lookup ("//" + peerInfo[i] + "/RMIBroadcastServer");
				}catch(Exception e){
					System.out.println("Failed to connect to server of id "+ (i+1) +" with "+e);
					System.out.println("Retry in 10 seconds");
					try{
						Thread.sleep(1000);
					}catch(Exception e2){

					}finally{
						continue;
					}
					
				}
				break;
			}

		}
		System.out.println("Peer Connections Built Successfully");
	};
	
	public void executeRequest(Request r,int pid) throws RemoteException{
		writeLog(pid+" Process  "+calendar.getTime()+" "+r.timestamp);
		//Debug(pid+" Process  "+calendar.getTime()+" "+r.timestamp);
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
				//Debug("Before Transfer");
				//accountManager.printAccountsBalance();
				transfer((TransferRequest) r);
				//Debug("After Transfer");
				//accountManager.printAccountsBalance();
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
		
		
		if(pq.peek()!=null){
			pq.peek().notify();
		}
	};
	public void halt(){
		System.out.println("Printing Balance Info :");
		accountManager.printAccountsBalance();
		System.out.println("Printing Pending Requests");
		while(pq.peek()!=null){
			System.out.println(pq.poll().req);
		}
		try{
		Naming.unbind("RMIServer");
		Naming.unbind("RMIBroadcastServer");


        // Unexport; this will also remove us from the RMI runtime
        UnicastRemoteObject.unexportObject(this, true);
		}catch(Exception e){

		}
		System.exit(1);
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
				break;
			default:
				throw new RemoteException("Unsupported Request");	
		}
		
		pq.poll();
		if(pq.peek()!=null)
			pq.peek().notify();
		
		return ret;
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
			}catch(RemoteException e ){
				
			}
		}
		long getTimestamp(){
			return this.timestamp;
		}
	}

	public Response submitRequest(Request r) throws RemoteException{
		assignTimestamp(r);
		writeLog(id+" CLNT-REQ "+calendar.getTime()+" "+ r);
		long ts = calendar.getTimeInMillis();	

		Ticket t = new Ticket(r.timestamp,id,r);
		pq.add(t);
		while(t!=pq.peek()){
			try{
				t.wait();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}

		int i =0 ;
		for ( i =0 ;i < numPeers ; i++){
			if(i== id-1) continue;
		//	System.out.println("Submitting Request to process "+(i+1));
			//writeLog("Submitting Request to process "+(i+1));
			setLamportClock(peers[i].requestBCast(r,id));	
		}
		for ( i =0 ;i < numPeers ; i++){
			if(i== id-1) continue;
				try{
					peers[i].executeRequest(r,id);
				}catch(Exception e){
					e.printStackTrace();
					continue;
				}			
		}
		// while not head of queue || r.time
		// wait;
		
		
		Response ret = processRequest(r);
		recordPerformance( calendar.getTimeInMillis() - ts);

		return ret;

	}
	public long requestBCast(Request r, int pid) throws RemoteException{
		
		long tempClock = setLamportClock(r.timestamp);
		writeLog(pid+" SRV-REQ  "+calendar.getTime()+" "+ r);
		Ticket t = new Ticket(r.timestamp, pid , r);
		//pq.add(t);
		if(pq.size()==0)
			return tempClock;
		else{
			while(pq.peek().compareTo(t)<0){
				System.out.println(r);
				try{
					pq.peek().wait();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		/*if(t==pq.peek()){
			pq.poll();
		}*/
		
		return tempClock;
	}
	public static void main (String args[]) throws Exception {
		
		accountManager = new AccountManager();
	    //System.setSecurityManager (new RMISecurityManager ());
	    server = new RMIServerImpl ();
	    System.out.println("Before Binding");
	    if(args.length != 2 ){
	    	throw new Exception("Usage : <config file name> <server id>");
	    }else{
	    	readFileByLine(args[0]);
	    	server.id = Integer.parseInt(args[1]);
	    	System.out.println((peerInfo[id-1].split(":"))[1]);
	       	Registry localRegistry = LocateRegistry.getRegistry(Integer.parseInt((peerInfo[Integer.parseInt(args[1])-1].split(":"))[1]));
	       	localRegistry.bind ("RMIServer", server);	
	    	localRegistry.bind ("RMIBroadcastServer", server);	
	    	
	    	buildPeerConnections();
	    	System.out.println("Server "+(id)+" waiting for request");
	    }
  }
}
