import java.util.Map;

import java.util.HashMap;
class AccountManager{
	private static Integer accountNum = 1 ;
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
		for(Map.Entry<Integer,Account> it : accounts.entrySet()){
			System.out.println("Account Id "+it.getKey() +" Balance " +it.getValue().getBalance());

		}

		
	};
}