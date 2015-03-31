package Requests;

import java.io.Serializable;

public class Request implements Serializable{
	private static final long serialVersionUID = 7526471155622776147L;
	public String requestType;
	public boolean isBCast;
	public long timestamp;
	
	public Request(String type){
		requestType = type;
		timestamp = -1;
	}
	public String toString(){
		switch(requestType){
			case "Deposit":
				return ((DepositRequest) this).toString();
			case "GetBalance":
					
				return ((GetBalanceRequest) this).toString();
				
			case "NewAccount":
				return ((NewAccountRequest) this).toString();
				
			case "Transfer":
				return ((TransferRequest) this).toString();
				
			case "Withdraw":
				return ((WithdrawRequest) this).toString();
				
			case "Halt":
				return ((HaltRequest) this).toString();
			default:
				return "";
		}
	}
};
