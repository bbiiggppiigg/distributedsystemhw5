package Responses;

public class TransferResponse extends Response{
		private static final long serialVersionUID = 00000000006L;
		public TransferResponse(String status){
			super(status);
			responseType = "Transfer";
		}
		public String toString(){
			return responseType +" status =  "+status;
		}
};