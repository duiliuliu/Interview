package com.reflect;

/**
 * defferentClassnameClassloader
 * 
 * Class.forName not only load class file into JVM, but also interprets the
 * class and executes the static block in the class.
 * 
 * Classloader does just one thing, loading the.Class file into JVM.
 */
public class defferentClassnameClassloader {

    public static void main(String[] args) {
        String lineClass = "com.reflect.test.Point";
        String pointClass = "com.reflect.test.Line";

        System.out.println("testclassloader");
        testClassLoader(lineClass);
        testClassLoader(pointClass);

        System.out.println("testclassforname");
        testForName(lineClass);
        testForName(pointClass);

    }

    private static void testClassLoader(String className) {
        Class<?> line;
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        try {
            line = loader.loadClass(className);
            System.out.println("testClassLoader\tline: " + line.toString());
        } catch (ClassNotFoundException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private static void testForName(String className) {
        try {
            Class line = Class.forName(className);
            System.out.println("testForName\tline: " + line.toString());
        } catch (ClassNotFoundException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}