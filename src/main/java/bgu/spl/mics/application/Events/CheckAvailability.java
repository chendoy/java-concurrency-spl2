package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;

public class CheckAvailability implements Event<Integer> {

    private String bookName;
    private Integer available;

    public CheckAvailability(String bookName) {
        this.bookName=bookName;
        available=null;
    }

    public String getBookName() {
        return bookName;
    }

    public Integer getAvailable() {
        return available;
    }
}
