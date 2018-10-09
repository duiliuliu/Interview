package com.reflect;

/**
 * defferentClassnameClassloader
 * 未通过
 */
public class defferentClassnameClassloader {

    public static void main(String[] args) {
        String AnimalClass = "com.javaBasic.polymorphic.Animal";
        String CatClass = "com.javaBasic.polymorphic.Cat";
        System.out.println("=========test==========");

        System.out.println("-----------------------");
    }

    private static void testClassLoader(String className) {
        Class<?> newclass;
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        try {
            newclass = loader.loadClass(className);
            Object obj = newclass.getInterfaces();
            Methods methods = newclass.getMethods();
            for (Method m : methods) {
                m.invoke(obj);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static void testForName(String className) {
        Class<?> newclass;
        try {
            newclass = Class.forName(className);
            Object obj = newclass.getInterfaces();
            Methods methods = newclass.getMethods();
            for (Method m : methods) {
                m.invoke(obj);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}