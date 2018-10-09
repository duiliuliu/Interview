package com.collection.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * IterHashmap
 */
public class IterHashmap {

    public static void main(String[] args) {

        HashMap<Integer, String> map = new HashMap<Integer, String>();

        int length = 5;

        for (int i = 0; i < length; i++) {
            map.put(i, "value is " + i);
        }

        // 1. for i in Map.KeySet
        System.out.println("\n// 1. for i in Map.KeySet");
        for (int key : map.keySet()) {
            System.out.println("key: " + key + "\t" + "value: " + map.get(key));
        }

        // 2. for i in Map.values , could not iter key
        System.out.println("\n// 2. for i in Map.values");
        for (String v : map.values()) {
            System.out.println("The value is " + v);
        }

        // 3. foreach lambda . suggest this way
        System.out.println("\n// 3. foreach lambda");
        map.forEach((key, value) -> {
            System.out.println("key: " + key + "\t" + "value: " + value);
        });

        // 4. for i in map.entrySet() . suggest this way
        System.out.println("\n// 4. for i in map.entrySet()");
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            System.out.println("key: " + entry.getKey() + "\t" + "value: " + entry.getValue());
        }

        // 5. iterator map.entrySet()
        System.out.println("\n// 5. iterator map.entrySet()");
        Iterator<Entry<Integer, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, String> entry = it.next();
            System.out.println("key: " + entry.getKey() + "\t" + "value: " + entry.getValue());
        }

    }
}