package com.thread.threadCommunication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TestThread
 * 
 * Threads execute sequentially.
 * 
 * Three threads, each with a sequential print threadName, such as ABCABCABC...
 */
public class ThreadOrderExecute {

    public static void main(String[] args) {
        int max = 10;
        Object lock = new Object();
        Thread A = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.print("A");

            }
        });
        Thread B = new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.print("B");

            }
        });
        Thread C = new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.print("C");

            }
        });
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        for (int i = 0; i < max; i++) {
            executorService.submit(A);
            executorService.submit(B);
            executorService.submit(C);
        }

        executorService.shutdown();
    }

    private static void printNumber(String threadName) {
        int i = 0;
        while (i++ < 10) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(threadName + " print: " + i);
        }
    }

}