package com.thread.lock;

/**
 * ReentrantLockDemo
 */
public class ReentrantLockDemo {
    public static void main(String[] args) {
        Product product = new Product(500);
        new Producer(product).produce(500);
        new Producer(product).produce(200);
        new Consumer(product).consume(500);
        new Consumer(product).consume(200);
    }
}

class Consumer {
    private Product product;

    public Consumer(Product product){
        this.product = product;
    }

    public void consume(int n) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                product.consume(n);
            }
        }, n + " consume thread").start();
    }
}

class Producer {
    private Product product;

    public Producer(Product product) {
        this.product = product;
    }

    public void produce(int n) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                product.produce(n);
            }
        }, n + " produce thread").start();
    }
}