package com.javaBasic.classInitial.PassiveReference;

/**
 * NotInitialization
 */
public class NotInitialization {
    public static void main(String[] args) {
        // 1. Referencing the static field of the parent class by subclass does not
        // result
        // in subclass initialization.
        // System.out.println(PassiveReferenceSon_2.value);

        // 2. References to classes by array definition do not trigger such
        // initialization.
        // PassiveReferenceSon_1[] passiveReferenceSons_1 = new PassiveReferenceSon_1[10];

        // 3.Constants are stored in the constant pool of calling classes at compile
        // time and are not essentially directly referenced to classes that define
        // constants, so initialization of classes that define constants is not
        // triggered
        System.out.println(PassiveReferenceParent.CONSTANT);

    }
}