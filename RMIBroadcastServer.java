import java.rmi.Remote;
import java.rmi.RemoteException;
import Requests.*;
import Responses.*;

public interface RMIBroadcastServer extends Remote{ 
	public long requestBCast(Request r,int pid) throws RemoteException;
	public void executeRequest(Request r ,int pid) throws RemoteException;
};