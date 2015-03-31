package Requests;


public class WithdrawRequest extends Request{
		public int accountId;
		public int amount;
		
		public WithdrawRequest(int accountId,int amount){
			super("Withdraw");
			this.accountId = accountId;
			this.amount = amount;	
		}
		public String toString(){
			return this.timestamp+" "+this.requestType+" AccountId = "+ accountId +" Amount = "+amount;
		}
};