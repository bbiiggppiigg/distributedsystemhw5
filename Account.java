
class Account{
	int balance;
	String firstname;
	String lastname;
	String address;
	Account(String fn,String ln,String addr){
		firstname = fn;
		lastname = ln;
		address = addr;
		balance =0 ;
	}
	public void deposit(int amount){
		if(amount<0){
			throw new RuntimeException("Amount less than 0");
		}else{
			balance= balance+ amount;
		}
	}
	public void withdraw(int amount){
		if(amount<=0){
			throw new RuntimeException("Amount less than 0");
		}else{
			if(balance<amount){
				throw new RuntimeException("Balance less than the amount to withdraw");
			}else{
				balance = balance - amount;
			}
		}
	}
	public int getBalance(){
		return balance;
	}

};
