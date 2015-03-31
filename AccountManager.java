

import java.util.HashMap;
class AccountManager{
	private static Integer accountNum = 0 ;
	private static HashMap<Integer,Account> accounts;
	AccountManager(){
		accounts = new HashMap<Integer,Account>();
	}
	static int newAccount(String firstName, String lastName, String address){
		int id;
		synchronized(accountNum){
			id = accountNum;
			accountNum++;
		}
		accounts.put(id,new Account(firstName,lastName,address));
		return id;
	}
	static Account getAccountById(int id){
		return accounts.get(id);
	}
	public static void printAccountsBalance(){
		int i =0 ;
		for (i=0;i<accountNum;i++){
				System.out.println("Account Id "+i +" Balance " +accounts.get(i).getBalance());
		}
	};
}