package Responses;

public class HaltResponse extends Response{
		private static final long serialVersionUID = 00000000005L;
		public HaltResponse(String status){
			super(status);
			responseType = "Halt";
	
		}
		public String toString(){
			return  responseType+ " status = "+status;
		}
}; 