package Responses;

public class GetBalanceResponse extends Response{
		private static final long serialVersionUID = 00000000004L;
		public int balance;
		public GetBalanceResponse(String status,int balance){
			super(status);
			this.balance = balance;
			responseType = "GetBalance";
	
		}
		public String toString(){
			return responseType+" status = "+status+ " balance = "+balance;
		}
}; 