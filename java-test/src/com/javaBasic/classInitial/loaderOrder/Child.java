package com.javaBasic.classInitial.loaderOrder;

/**
 * Child
 */
public class Child extends Parent {

    static {
        System.out.println(" Child staic block");
    }

    {
        System.out.println(" Child Object block");
    }

    public Child() {
        System.out.println("Hello Child");
    }

    public static void main(String[] args) {
        // new Child();
        /**
         * output:
         *  Parent staic block
            Child staic block
            Parent Object block
            Hello Parent
            Child Object block
            Hello Child
         */

        // new Parent();
        /**
         * output:
         * Parent staic block
            Child staic block
            Parent Object block
            Hello Parent
         */

        Parent child = new Child();
        /**
         * output:
         *  Parent staic block
            Child staic block
            Parent Object block
            Hello Parent
            Child Object block
            Hello Child
         */
    }
}