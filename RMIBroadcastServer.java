import java.rmi.Remote;
import java.rmi.RemoteException;
import Requests.*;
import Responses.*;
/***
	RMIBroadcastServer is the interface to allow server-server communication
***/
public interface RMIBroadcastServer extends Remote{ 
	/***
		requestBcast is called when a message is broadcasted to this particular server
		the return value will be a timestamp according to the local lamport clock value.
	***/
	public long requestBCast(Request r,int pid) throws RemoteException;
	/***
		executeRequest is called when a server finds that their request is acked by all other process and therefore
		send this request to execute directly by all other servers.
		The return value of these execution is ignored because only the server facing the client has to report 
		the results.
	***/
	public void executeRequest(Request r ,int pid) throws RemoteException;
};