package com.thread.threadInterrupt;

import java.util.concurrent.TimeUnit;

/**
 * NotInterruptRunningThread
 * 
 * Threads in a non blocking state require us to manually detect interrupt and end the program.
 */
public class NotInterruptRunningThread {

    public static void main(String[] args) throws InterruptedException {
        
        Thread thread = new Thread(){
            @Override
            public void run(){
                int i = 100;
                while(i-->0){
                    System.out.println("Not interrupted");
                }
                System.out.println("Running End");
            }
        };
        
        thread.start();
        TimeUnit.SECONDS.sleep(2);
        
        // Not interrupt the thread in running state
        thread.interrupt();

        /**
         * output(loop):
             Not interrupted
             Not interrupted
             Not interrupted
             ......
         */

    }
}


