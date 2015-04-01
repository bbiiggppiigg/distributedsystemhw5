package Responses;

public class DepositResponse extends Response{
		private static final long serialVersionUID = 00000000003L;
		public DepositResponse(String status){
			super(status);
			responseType = "Deposit";
	

		}
		public String toString(){
			return responseType+" status = "+status;
		}
};