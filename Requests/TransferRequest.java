package Requests;


public class TransferRequest extends Request{
		public int sourceId;
		public int targetId;
		public int amount;
		
		public TransferRequest(int sourceId,int targetId,int amount){
			super("Transfer");
			this.sourceId = sourceId;
			this.targetId = targetId;
			this.amount = amount;
		}
		public String toString(){
			return this.timestamp+" "+this.id+" "+this.requestType +" Source "+sourceId+" Target "+this.targetId+" Amount "+this.amount;
		}
};