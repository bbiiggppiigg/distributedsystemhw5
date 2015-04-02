package Requests;
public class HaltRequest extends Request{
	public HaltRequest(){
		super("Halt");
	}
	public String toString(){
		return this.timestamp+" "+this.id+ " Halt";
	}
}