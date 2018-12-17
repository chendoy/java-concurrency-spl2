package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo implements Serializable {

	private String bookTitle;
	private int amountInInventory;
	private int price;

	/**
	 * Basic constructor for BookInventoryInfo that takes trivial parameters
	 */
	public BookInventoryInfo(String bookTitle, int amount, int price)
	{
		this.bookTitle=bookTitle;
		this.amountInInventory=amount;
		this.price=price;
	}

	/**
	 * Retrieves the title of this book.
	 * <p>
	 * @return The title of this book.
	 */
	public String getBookTitle() {
		return bookTitle;
	}

	/**
	 * Retrieves the amount of books of this type in the inventory.
	 * <p>
	 * @return amount of available books.
	 */
	public int getAmountInInventory() {
		return amountInInventory;
	}

	/**
	 * Retrieves the price for  book.
	 * <p>
	 * @return the price of the book.
	 */
	public int getPrice() {
		return price;
	}

	public void decreaseAmountByOne() {amountInInventory--;}

	//delete after debugg
	public void printbookInventoryInfo() {
		System.out.println(" ----START Printing BookInventoryInfo of "+getBookTitle()+" ---");
		System.out.println("book title: "+ getBookTitle());
		System.out.println("amount : " + getAmountInInventory());
		System.out.println("price : " + getPrice()	);
		System.out.println(" ----END Printing BookInventoryInfo of "+getBookTitle()+" ---");
	}

}