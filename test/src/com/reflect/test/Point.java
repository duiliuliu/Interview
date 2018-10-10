package com.reflect.test;

/**
 * Point
 */
public class Point implements Graphical {
    static {
        System.out.println("static code executing: loading Point Class");
    }

    {
        System.out.println("common code executing load Point Object");
    }

    private int count;

    public Point() {
        count = 0;
    }

    public Point(int count) {
        this.count = count;
    }

    @Override
    public void paint() {
        System.out.println("Point Object has painted Point , and count is " + count);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static void staticMethod() {
        System.out.println("executing static Method");
    }

    public String toString() {
        return "These are " + count + " points.";
    }

}