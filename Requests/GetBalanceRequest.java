package Requests;

public class GetBalanceRequest extends Request{
		public int accountId;
		
		public GetBalanceRequest(int accountId){
			super("GetBalance");
			this.accountId = accountId;
		}
		public String toString(){
			return this.timestamp+" "+this.id+" "+this.requestType + " AccountId= "+accountId;
		}
}; 