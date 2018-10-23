package com.javaBasic.classInitial.PassiveReference;

/**
 * PassiveReferenceSon_2
 * 
 * Referencing the static field of the parent class by subclass does not result
 * in subclass initialization.
 */
public class PassiveReferenceSon_2 extends PassiveReferenceSon_1 {

    static {
        System.out.println("This is static block in the PassiveReferenceSon_2 Class .");
    }

    public PassiveReferenceSon_2() {
        System.out.println("PassiveReferenceSon_2 class init!");
    }
}