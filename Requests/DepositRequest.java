package Requests;


public class DepositRequest extends Request{
		public int accountId;
		public int amount;
		public DepositRequest(int accountId,int amount){
			super("Deposit");
			this.accountId = accountId;
			this.amount = amount;
			
		}
		public String toString(){
			return this.timestamp+" "+this.id+" "+this.requestType+" "+"AccoutId = "+accountId+" Amount = "+amount;
		}
};