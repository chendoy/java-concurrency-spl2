package bgu.spl.mics;


import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    private  Future<Boolean> avialble_Book;

    @Test
    public void get() {
        avialble_Book=new Future<>();
        //try to use get without resolve (expecting return null)
        assertEquals(null,avialble_Book.get());
        //now we will resolve it and it shouldn't return null
        avialble_Book.resolve(true);
        assertNotEquals(null,avialble_Book.get());
    }
    @Test
    public void resolve() {
        avialble_Book=new Future<>();
        avialble_Book.resolve(true);
        //checks if it handle appropriate value for resolve
        assertNotEquals(null,avialble_Book.get());
        avialble_Book=new Future<>();
        //checks if we handle appropriate illegal values (e.g we cant accept null resolving)
        avialble_Book.resolve(null);
        assertNotEquals(null,avialble_Book.get());
    }

    @Test
    public void isDone() {
        avialble_Book=new Future<>();
        //the object has been resolved if it has a value (not null)
        avialble_Book.resolve(false); //resolve it with false value
        assertEquals(true,avialble_Book.isDone());
        avialble_Book=new Future<>();
        assertEquals(false,avialble_Book.isDone());
    }

    //the second get
    @Test
    public void get1() {
        avialble_Book=new Future<>(); //not resolved
        assertEquals(null,avialble_Book.get(1, TimeUnit.MILLISECONDS));

    }
}