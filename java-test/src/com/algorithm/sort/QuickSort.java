package com.algorithm.sort;

import com.algorithm.sort.ArrayMethod;

/**
 * QuickSort
 */
public class QuickSort {

    public static void main(String[] args) {

        int[] data = new int[] { 1, 0, 10, 20, 3, 5, 6, 4, 9, 8, 12, 17, 34, 11 };
        System.out.println("data: " + ArrayMethod.StringfyArray(data));

        quicksort(data);
        System.out.println("quickSorted: " + ArrayMethod.StringfyArray(data));
    }

    public static void quicksort(int[] data) {
        sort(data, 0, data.length - 1);
    }

    private static void sort(int[] data, int low, int high) {
        if (low >= high) {
            return;
        }
        int mid = partition(data, low, high);
        sort(data, low, mid - 1);
        sort(data, mid + 1, high);
    }

    private static int partition(int[] nums, int low, int high) {
        int i = low, j = high + 1;
        int T = nums[low];
        while (true) {
            while (nums[++i] < T && i < j)
                ;
            while (T < nums[--j] && j > low)
                ;
            if (i >= j)
                break;
            ArrayMethod.swap(nums, i, j);
        }
        ArrayMethod.swap(nums, low, j);
        return j;
    }
}