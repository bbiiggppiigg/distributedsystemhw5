package Requests;

public class NewAccountRequest extends Request{
		public String firstname;
		public String lastname;
		public String address;
		
		public NewAccountRequest(String firstname,String lastname,String address){
			super("NewAccount");
			this.firstname = firstname;
			this.lastname = lastname;
			this.address = address;
		}
		public String toString(){
			return this.timestamp+" "+this.requestType + " Firstname = "+firstname+ "Lastname = "+lastname+" Address= "+address;
		}
};
