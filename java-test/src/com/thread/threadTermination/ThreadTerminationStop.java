package com.thread.threadStop;

/**
 * ThreadTerminationStop
 */
public class ThreadTerminationStop {

    public static void main(String[] args) {

        Thread thread = new Thread(() -> {
            while (true) {
                System.out.println("thread running");
            }
        });

        thread.start();

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            // TODO: handle exception
        }

        thread.stop();
        System.out.println("thread stop");

        /**
         * ???? ???????? ?????????????????????????????????
         * 
         * ???????????main??????????????????????
         */
    }

}