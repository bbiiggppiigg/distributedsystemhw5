package Responses;

public class NewAccountResponse extends Response{
	private static final long serialVersionUID = 00000000002L;
	public int accountId;
	public NewAccountResponse(String status,int accountId){
		super(status);
		this.accountId = accountId;
		responseType = "NewAccount";
	}
	public String toString(){
		return responseType+ " status = "+status+" accountId = "+accountId;
	}
}