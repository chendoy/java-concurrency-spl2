package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.Events.BookOrderEvent;

import java.io.Serializable;


/**
 * Passive data-object representing a receipt that should
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable {
	private String bookName;
	private int price;
	private Customer customer;
	//private SellingService sellerService; //the microService that handles the selling operation
	private int orderTick; // the microService that puarch a book (puarch book event)
	private int startProcessTick;
	private String sellerName;
	private  int issuedTick;
	private BookOrderEvent bookOrderEvent;


	public OrderReceipt(String bookName,int price, Customer customer, int startProcessTick,String sellerName,int orderTick,int issuedTick) {
		this.customer=customer;
		this.bookName=bookName;
		this.orderTick =orderTick;
		this.bookOrderEvent = bookOrderEvent;
		this.price=price;
		this.startProcessTick=startProcessTick;
		this.sellerName=sellerName;
		this.issuedTick=issuedTick;
	}
	/**
	 * Retrieves the orderId of this receipt.
	 */
	public int getOrderId() {
		// TODO Implement this
		return 0;
	}

	/**
	 * Retrieves the name of the selling service which handled the order.
	 */
	public String getSeller() {
		return sellerName;
	}

	/**
	 * Retrieves the ID of the customer to which this receipt is issued to.
	 * <p>
	 * @return the ID of the customer
	 */
	public int getCustomerId() {
		return customer.getId();
	}

	/**
	 * Retrieves the name of the book which was bought.
	 */
	public String getBookTitle() {
		return bookName;
	}

	/**
	 * Retrieves the price the customer paid for the book.
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * Retrieves the tick in which this receipt was issued.
	 */
	public int getIssuedTick() {
		return issuedTick;
	}

	/**
	 * Retrieves the tick in which the customer sent the purchase request.
	 */
	public int getOrderTick() {
		return orderTick;
	}

	/**
	 * Retrieves the tick in which the treating selling service started
	 * processing the order.
	 */
	public int getProcessTick() {
		return startProcessTick;
	}

	public Customer getCustomer() {
		return customer;
	}
}