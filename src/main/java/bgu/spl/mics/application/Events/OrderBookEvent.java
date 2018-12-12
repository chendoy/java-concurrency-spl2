package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;

public class OrderBookEvent implements Event<OrderBookEvent> {

    private String bookName;

    public OrderBookEvent(String bookName){
        this.bookName=bookName;
    }
}
