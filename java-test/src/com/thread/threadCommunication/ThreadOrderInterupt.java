package com.thread.threadCommunication;

/**
 * ThreadOrderInterupt
 * 
 * �߳�A��B��c�����߳�AֹͣʱҪ��B��CҲֹͣ
 * 
 * 1. wait���� 2. synchronized{ Thread.yield()}
 */
public class ThreadOrderInterupt {

    static int max = 10;

    public static synchronized void f(String threadname) {
        System.out.println(threadname + " Trying to call f()");
        while (true) // Never releases lock
        {
            Thread.yield();
        }
    }

    public static void printThread(String threadname) {
        System.out.println(threadname + " is running");
    }

    public void test1() {
        volatile boolean flag = true;

        Thread aThread = new Thread(() -> {
            System.out.println("aThread start");
            for (int i = 0; i < max; i++) {
                printThread("aThread");
            }
        });

        Thread bThread = new Thread(() -> {
            System.out.println("bThread start");
            while (true) {
                printThread("bThread");
                if (!flag) {
                    try {
                        wait();
                    } catch (Exception e) {
                        System.out.println("bThread stop");
                    }
                }
            }
        });

        Thread cThread = new Thread(() -> {
            System.out.println("cThread start");
            while (true) {
                printThread("cThread");
                while (!flag) {
                    try {
                        wait();
                    } catch (Exception e) {
                        System.out.println("cThread stop");
                    }
                }
            }
        });

        aThread.start();
        bThread.start();
        cThread.start();
    }

    public static void test2() {

        Thread aThread = new Thread(() -> {
            System.out.println("aThread start");
            for (int i = 0; i < max; i++) {
                f("aThread");
            }
        });

        Thread bThread = new Thread(() -> {
            System.out.println("bThread start");
            for (int i = 0; i < max; i++) {
                f("bThread");
            }
        });

        Thread cThread = new Thread(() -> {
            System.out.println("cThread start");
            for (int i = 0; i < max; i++) {
                f("cThread");
            }
        });

        aThread.start();
        bThread.start();
        cThread.start();
    }

    public static void main(String[] args) {
        new ThreadOrderInterupt().test1();

        /**
         * wait()�ǵ�ǰ�̷߳����Ըö����ռ��
         * 
         * sleep�ǵ�ǰ�߳��ó�cpu
         */
    }
}