package bgu.spl.mics;

import java.util.concurrent.CountDownLatch;

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