package Responses;

public class WithdrawResponse extends Response{
		private static final long serialVersionUID = 00000000007L;
		public WithdrawResponse(String status){
			super(status);
			responseType = "Withdraw";
		}
		public String toString(){
			return responseType +" status =  "+status;
		}
};