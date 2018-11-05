package com.javaBasic.classInitial.loaderOrder;

/**
 * Parent
 */
public class Parent {

    static {
        System.out.println(" Parent staic block");
    }

    {
        System.out.println(" Parent Object block");
    }

    public Parent() {
        System.out.println("Hello Parent");
    }
}