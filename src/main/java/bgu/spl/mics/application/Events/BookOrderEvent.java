package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.services.APIService;

public class BookOrderEvent implements Event<OrderReceipt> {

    private String bookName;
    private Customer customer;



    private APIService apiService;

    public OrderReceipt getOrderReceipt() {
        return orderReceipt;
    }

    private OrderReceipt orderReceipt;

    public BookOrderEvent(String bookName, Customer customer,APIService apiService){
        this.bookName=bookName;
        this.customer=customer;
        this.apiService=apiService;
        orderReceipt=null;
    }
    public void setRecipt(OrderReceipt orderReceipt) {
        this.orderReceipt=orderReceipt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getBookName() {
        return bookName;
    }
    public int getEventTick() {
        return apiService.getOrderdBookTick(this);
    }
}
