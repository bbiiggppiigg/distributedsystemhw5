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

}