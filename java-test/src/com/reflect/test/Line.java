package com.reflect.test;

/**
 * Line
 */
public class Line implements Graphical {
    static {
        System.out.println("static code executing: loading Line Class");
    }

    {
        System.out.println("common code executing load Line Object");
    }

    private int length;

    public Line() {
        length = 0;
    }

    public Line(int length) {
        this.length = length;
    }

    @Override
    public void paint() {
        System.out.println("Line Object has painted line , and length is " + length);
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public static void staticMethod() {
        System.out.println("executing static Method");
    }

    public String toString() {
        return " This is a line with a length of " + length;
    }

}