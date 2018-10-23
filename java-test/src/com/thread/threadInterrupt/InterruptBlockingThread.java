package com.thread.threadInterrupt;

import java.util.concurrent.TimeUnit;

/**
 * InterruptBlockingThread
 */
public class InterruptBlockingThread{

    public static void main(String[] args) throws InterruptedException {
        
        Thread thread = new Thread(){
            @Override
            public void run(){
                // while in try block, you can quit the run loop by breaking the exception
                try {
                    while(true){
                        // the current thread is in a blocking state. Exceptions must be captired and not thrown out
                        TimeUnit.SECONDS.sleep(2);
                    }
                } catch (InterruptedException e) {
                    //TODO: handle exception
                    System.out.println("Interruted When Sleep");
                    boolean interrupt = this.isInterrupted();
                    // Interrupt state reset
                    System.out.println("interrupt:"+interrupt);
                }
            }
        };

        thread.start();
        TimeUnit.SECONDS.sleep(2);

        // Interrupt the thread in blocked state
        thread.interrupt();

         /**
         * output:
           Interruted When Sleep
           interrupt:false
         */
    }
}