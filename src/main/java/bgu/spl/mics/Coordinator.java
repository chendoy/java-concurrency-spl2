package bgu.spl.mics;

import java.util.concurrent.CountDownLatch;

/*
coordinator is a helper class that is used in order to coordinate between the threads and the time service in BookStoreRunner
 */
public class Coordinator implements Runnable {
    private CountDownLatch latchObject;

    public Coordinator(CountDownLatch latchObject) {
        this.latchObject = latchObject;
    }

    public void run() {
        try {
            latchObject.await();
        } catch (InterruptedException e) {
            return;
        }
    }
}