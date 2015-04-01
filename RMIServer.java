import java.rmi.Remote;
import java.rmi.RemoteException;
import Requests.*;
import Responses.*;
/***
	This is the interface implemented for client server interaction
***/

public interface RMIServer extends Remote{
	public NewAccountResponse newAccount(NewAccountRequest r) throws RemoteException;
	public DepositResponse deposit(DepositRequest r) throws RemoteException;
	public GetBalanceResponse getBalance(GetBalanceRequest r) throws RemoteException;
	public TransferResponse transfer(TransferRequest r) throws RemoteException;
	public WithdrawResponse withdraw(WithdrawRequest r) throws RemoteException;
	
	/***
		submitRequest is for the client to initially submit a request
		this request will be broadcast by the RMIBroadcastServer interface and later process by
		the processRequest method below
	***/
	public Response submitRequest(Request r) throws RemoteException;
	
	/***
		processRequest is the method used for executing the request in its local queue.
		
	***/
	public Response processRequest(Request r) throws RemoteException;

}

