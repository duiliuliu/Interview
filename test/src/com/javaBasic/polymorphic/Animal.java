package com.javaBasic.polymorphic;

/**
 * Animal
 */
public class Animal {

    public String name;

    public Animal(String name) {
        this.name = name;
    }

    public void eat() {
        System.out.println(name + "eatting");
    }

    public void run() {
        System.out.println(name + "Animal running");
    }
}