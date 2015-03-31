import java.rmi.Remote;
import java.rmi.RemoteException;
import Requests.*;
import Responses.*;

public interface RMIServer extends Remote{
	public NewAccountResponse newAccount(NewAccountRequest r) throws RemoteException;
	public DepositResponse deposit(DepositRequest r) throws RemoteException;
	public GetBalanceResponse getBalance(GetBalanceRequest r) throws RemoteException;
	public TransferResponse transfer(TransferRequest r) throws RemoteException;
	public WithdrawResponse withdraw(WithdrawRequest r) throws RemoteException;
	public Response submitRequest(Request r) throws RemoteException;
	public Response processRequest(Request r) throws RemoteException;

}

