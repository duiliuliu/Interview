package com.thread.threadInterrupt;

import java.util.concurrent.TimeUnit;

/**
 * NotInterruptSynchronizBlock
 */
public class NotInterruptSynchronizBlock implements Runnable {

    public synchronized void f() {
        System.out.println("Trying to call f()");
        while(true) // Never releases lock
            Thread.yield();
    }

     /**
     * Create a new thread in the constructor and start the object lock.
     */
    public NotInterruptSynchronizBlock() {
        // The thread already holds the current instance lock.
        new Thread() {
            public void run() {
                f(); // Lock acquired by this thread
            }
        }.start();
    }
    public void run() {
        // Interruption judgment
        while (true) {
            if (Thread.interrupted()) {
                System.out.println("Interrupt Thread!!");
                break;
            } else {
                f();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException  {
        NotInterruptSynchronizBlock sync = new NotInterruptSynchronizBlock();
        Thread t = new Thread(sync);
        // After invoking the f () method, the current instance lock is not waiting.
        t.start();
        TimeUnit.SECONDS.sleep(1);
        // Interrupt thread, unable to take effect
        t.interrupt();
    }
}