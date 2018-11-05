package com.algorithm.sort;

import com.algorithm.sort.ArrayMethod;

/**
 * HeatTest
 */
public class HeatTest {

    public static void main(String[] args) {
        int[] nums = { 1, 3, 4, 6, 7, 5, 2, 9 };
        heapSort(nums);
        System.out.println("heapSorted: " + ArrayMethod.StringfyArray(nums));
    }

    public static void heapSort(int[] nums) {
        initHeap(nums);
        sort(nums);
    }

    private static void sort(int[] nums) {
        int len = nums.length;
        for (int i = len - 1; i >= 0; i--) {
            ArrayMethod.swap(nums, i, 0);
            judgeHeap(nums, i, 0);
        }
    }

    private static void initHeap(int[] nums) {
        int i = (nums.length - 1) >> 1;
        for (; i >= 0; i--) {
            judgeHeap(nums, nums.length, i);
        }
    }

    private static void judgeHeap(int[] nums, int size, int index) {
        int left = (index << 1) + 1;
        int right = (index << 1) + 2;
        int largest = index;

        if (size > left && nums[largest] < nums[left]) {
            largest = left;
        }
        if (size > right && nums[largest] < nums[right]) {
            largest = right;
        }
        if (index != largest) {
            ArrayMethod.swap(nums, index, largest);
            judgeHeap(nums, size, largest);
        }

    }
}