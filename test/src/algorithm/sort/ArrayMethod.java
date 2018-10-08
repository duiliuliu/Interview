package com.algorithm.sort;

/**
 * ArrayMethod
 */
public class ArrayMethod {

    public static void swap(int[] data, int i, int j) {
        int temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    public static void reverse(int[] data) {
        int low = 0;
        int high = data.length - 1;
        while (low < high) {
            swap(data, low, high);
            low++;
            high--;
        }
    }

    public static String StringfyArray(int[] data) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i : data) {
            sb.append(" " + i + ",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }
}