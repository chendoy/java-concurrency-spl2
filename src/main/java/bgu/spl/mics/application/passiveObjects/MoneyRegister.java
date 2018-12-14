package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {

	private ConcurrentHashMap<Integer,OrderReceipt> receiptsList;
	private int numOfReceipts;

	private static class MoneyRegisterHolder{
		private static MoneyRegister instance=new MoneyRegister();
	}

	private MoneyRegister(){
		receiptsList=new ConcurrentHashMap<>();
		numOfReceipts=0;
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {
		return MoneyRegisterHolder.instance;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		receiptsList.put(numOfReceipts,r);
		numOfReceipts++;
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		int totalEarnings=0;
		for(int i=0;i<numOfReceipts;i++)
			totalEarnings+=receiptsList.get(i).getPrice();
		return totalEarnings;
	}

	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */

	public void chargeCreditCard(Customer c, int amount) {
		c.charge(amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.
     */
	public void printOrderReceipts(String filename) {
		try{
			FileOutputStream fileOut=new FileOutputStream(filename);
			ObjectOutputStream out=new ObjectOutputStream(fileOut);
			out.writeObject(receiptsList);
			out.close();
			fileOut.close();
		}
		catch(Exception e) {
			System.out.println("Error in receipts printing: "+e.toString());
		}
	}
}
