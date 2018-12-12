package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;

public class BookOrderEvent implements Event<BookOrderEvent> {

    private String bookName;

    public BookOrderEvent(String bookName){
        this.bookName=bookName;
    }
}
