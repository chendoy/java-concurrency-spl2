package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Events.OrderBookEvent;
import bgu.spl.mics.application.services.APIService;
import bgu.spl.mics.application.services.SellingService;

/**
 * Passive data-object representing a receipt that should
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt {

	private BookInventoryInfo bookInventoryInfo;
	private Customer customer;
	private SellingService sellerService; //the microService that handles the selling operation
	private APIService apiService; // the microService that puarch a book (puarch book event)
	private  int issuedTick;
	private OrderBookEvent orderBookEvent;


	public OrderReceipt(BookInventoryInfo bookInventoryInfo, Customer customer, SellingService sellerService, APIService webApiService, OrderBookEvent orderBookEvent) {
		this.customer=customer;
		this.bookInventoryInfo=bookInventoryInfo;
		this.sellerService =sellerService;
		this.apiService =webApiService;
		this.orderBookEvent=orderBookEvent;
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
		return sellerService.getName();
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
		return bookInventoryInfo.getBookTitle();
	}

	/**
	 * Retrieves the price the customer paid for the book.
	 */
	public int getPrice() {
		return bookInventoryInfo.getPrice();
	}

	/**
	 * Retrieves the tick in which this receipt was issued.
	 */
	public int getIssuedTick() {
		//not sure about it
		return issuedTick;
	}

	/**
	 * Retrieves the tick in which the customer sent the purchase request.
	 */
	public int getOrderTick() {
		apiService.getOrderdBookTick(orderBookEvent);
	}

	/**
	 * Retrieves the tick in which the treating selling service started
	 * processing the order.
	 */
	public int getProcessTick() {
		return sellerService.getStartProcessTickTime(orderBookEvent);
	}
}