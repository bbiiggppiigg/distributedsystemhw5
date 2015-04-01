package Responses;

import java.io.Serializable;


public class Response implements Serializable{
	private static long serialVersionUID = 00000000001L;
	public String status;
	public String responseType;
	public Response(String status){
		//System.out.println("Status = "+st);
		this.status = status;
	
	}
	public void setStatus(String status){
		this.status = status;
	}
	public String toString(){
		switch(responseType){
			case "Deposit":
				return ((DepositResponse) this).toString();
			case "GetBalance":
					
				return ((GetBalanceResponse) this).toString();
				
			case "NewAccount":
				return ((NewAccountResponse) this).toString();
				
			case "Transfer":
				return ((TransferResponse) this).toString();
				
			case "Withdraw":
				return ((WithdrawResponse) this).toString();
				
			case "Halt":
				return ((HaltResponse) this).toString();
			default:
				return "";
		}
	}

}