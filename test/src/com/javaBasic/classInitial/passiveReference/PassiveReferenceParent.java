package com.javaBasic.classInitial.PassiveReference;

/**
 * PassiveReferenceParent
 * 
 * Referencing the static field of the parent class by subclass does not result
 * in subclass initialization.
 */
public class PassiveReferenceParent {

    public static final String CONSTANT = "hello world";

    static {
        System.out.println("This is static block in the PassiveReferenceParent Class .");
    }
}