package com.javaBasic.classInitial.PassiveReference;

/**
 * PassiveReferenceSon_1
 * 
 * Referencing the static field of the parent class by subclass does not result
 * in subclass initialization.
 */
public class PassiveReferenceSon_1 extends PassiveReferenceParent {

    static {
        System.out.println("This is static block in the PassiveReferenceSon_1 Class .");
    }

    public static String value = "PassiveReferenceSon_1_static_feild value";

    public PassiveReferenceSon_1() {
        System.out.println("PassiveReferenceSon_1 class init!");
    }
}