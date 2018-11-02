package com.thread.threadTermination;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ThreadPoolShutdownNow
 */
public class ThreadPoolShutdownNow {

    static volatile int count = 0;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            // executeֱ��ִ������
            // submit�ɵõ�������
            count++;
            System.out.println("add thread-" + count);
            executorService.execute(() -> {
                while (true) {
                    System.out.println("thread-" + count + " running");
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
            });

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

            // shutdown������ֹͣ���̳߳��м����������
            executorService.shutdownNow();
            System.out.println("thread stop");
        }
    }
}