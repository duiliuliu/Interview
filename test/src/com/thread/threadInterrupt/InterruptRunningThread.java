package com.thread.threadInterrupt;

import java.util.concurrent.TimeUnit;

/**
 * InterruptRunningThread
 * 
 * Threads in a non blocking state require us to manually detect interrupt and end the program.
 */
public class InterruptRunningThread {

    public static void main(String[] args) throws InterruptedException {
        
        Thread thread_1 = new Thread(){
            @Override
            public void run(){
                while(true){
                    //  Detemines whether the current thread is interrupted
                    if(this.isInterrupted()){
                        System.out.println("interrupt thread");
                        break;
                    }
                    System.out.println("waiting interrupt");
                }
                System.out.println("Out of loop, thread interrupt");
            }
        };

        thread_1.start();
        TimeUnit.SECONDS.sleep(2);

        // interrupt the thread in running state
        thread_1.interrupt();

        /**
         * output:
             interrupt thread
             loop:
                waiting interrupt
                waiting interrupt
                ......
             Out of loop, thread interrupt
         */

    }
}