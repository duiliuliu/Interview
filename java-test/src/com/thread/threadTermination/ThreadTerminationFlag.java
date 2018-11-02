package com.thread.threadTermination;

/**
 * threadTerminationFlag
 */
public class ThreadTerminationFlag {

    static volatile boolean flag = true;

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            while (flag) {
                System.out.println("thread running");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            // TODO: handle exception
        }

        flag = false;
        System.out.println("thread stop");
    }
}