package bgu.spl.mics.application.passiveObjects;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */

public class Inventory {

	private ConcurrentHashMap<String,BookInventoryInfo> booksInInventory;
	private HashMap<String,Integer> booksAmountMap;
	private int numOfDistinctBooks;

	private static class InventoryHolder {
		private static Inventory instance = new Inventory();
	}

	private Inventory() {
		booksInInventory=new ConcurrentHashMap<>();
		booksAmountMap=new LinkedHashMap<>();
		numOfDistinctBooks=0;

	}

		/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
		return InventoryHolder.instance;
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		for (BookInventoryInfo bii:inventory) {
			booksInInventory.put(bii.getBookTitle(),bii);
			booksAmountMap.put(bii.getBookTitle(),bii.getAmountInInventory());

			numOfDistinctBooks++;
		}
		System.out.println(booksAmountMap.toString());


	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public synchronized OrderResult take (String book) {
		if(booksInInventory.get(book).getAmountInInventory()!=0) { //book is available
			booksInInventory.get(book).decreaseAmountByOne();
			Integer amount=booksAmountMap.remove(book);
			booksAmountMap.put(book,amount-1);
			return OrderResult.SUCCESSFULLY_TAKEN;
		}
		else
			return OrderResult.NOT_IN_STOCK;
	}
	
	
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public synchronized int checkAvailabiltyAndGetPrice(String book) {
			if(booksInInventory.containsKey(book))
				return booksInInventory.get(book).getPrice();
		return -1;
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a 
     * Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename){
		try{
			FileOutputStream fileOut=new FileOutputStream(filename);
			ObjectOutputStream out=new ObjectOutputStream(fileOut);
			out.writeObject(booksAmountMap);
			out.close();
			fileOut.close();
		}
		catch(Exception e) {
			System.out.println("Error: "+e.getMessage());
		}

	}

}
