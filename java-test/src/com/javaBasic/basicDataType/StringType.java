package com.javaBasic.basicDataType;

/**
 * StringType
 */
public class StringType {

    public static void main(String[] args) {
        String str1 = "hello";
        String str2 = "he" + new String("llo");
        System.out.println(str1 == str2);
        System.out.println(str1.equals(str2));

        /**
         * output:
         * false
         * true
         */
    }
}