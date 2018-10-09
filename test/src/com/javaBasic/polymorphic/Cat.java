package com.javaBasic.polymorphic;

import com.javaBasic.polymorphic.Animal;

/**
 * Cat
 */
public class Cat extends Animal {

    public Cat() {
        super("cat");
    }

    public void eat() {
        System.out.println("name is " + name);
        super.eat();
    }

    public void run() {
        System.out.println("name is " + name);
        super.run();
    }
}