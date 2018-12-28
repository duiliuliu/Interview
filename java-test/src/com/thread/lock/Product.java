package com.thread.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Product 结合Condition实现生产者消费者，分析AbstractQueuedSynchronizer的内部机制
 */
public class Product {

    private int size;
    private int capacity;
    private Lock lock;
    private Condition fullCondition;
    private Condition emptyCondition;

    public Product(int capacity) {
        this.capacity = capacity;
        lock = new ReentrantLock();
        fullCondition = lock.newCondition();
        emptyCondition = lock.newCondition();
    }

    public void produce(int n) {
        lock.lock();
        int left = n;
        try {
            while (left > 0) {
                while (size > capacity) {
                    System.out.println(Thread.currentThread() + " before await");
                    fullCondition.await();
                    System.out.println(Thread.currentThread() + " after await");
                }
                int deviation = (left + size) > capacity ? (capacity - size) : left;
                left -= deviation;
                size += deviation;
                System.out.println("produce = " + deviation + ", size = " + size);
                emptyCondition.signal();
            }
        } catch (InterruptedException e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void consume(int n) {
        lock.lock();
        int left = n;
        try {
            while (left > 0) {
                while (size <= 0) {
                    System.out.println(Thread.currentThread() + " before await");
                    emptyCondition.await();
                    System.out.println(Thread.currentThread() + " after await");
                }
                int deviation = (size - left) > 0 ? left : size;
                left -= deviation;
                size += deviation;
                System.out.println("produce = " + deviation + ", size = " + size);
                fullCondition.signal();
            }
        } catch (InterruptedException e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}