package com.thread.threadTermination;

/**
 * threadTerminationInterrupt
 */
public class ThreadTerminationInterrupt {

    public static void main(String[] args) {

        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("thread running");
                // try {
                // Thread.sleep(1000);
                // } catch (Exception e) {
                // // TODO: handle exception
                // e.printStackTrace();
                // }
                
                // If the interrupt exception is captured here, the thread will not terminate.
                // 倘若此处捕获中断异常，则线程不会终止
            }
        });
        thread.start();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            // TODO: handle exception
        }

        thread.interrupt();
        System.out.println("thread stop");

    }
}