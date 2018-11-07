package com.algorithm.sort;

import com.algorithm.sort.ArrayMethod;

/**
 * HeatTest
 */
public class HeatTest {

    public static void main(String[] args) {
        int[] nums = { 1, 3, 4, 6, 7, 5, 2, 9 };
        heapsort(nums);
        System.out.println("heapSorted: " + ArrayMethod.StringfyArray(nums));
    }

    public static void heapsort(int[] nums) {
        initHeap(nums);
        for (int i = nums.length - 1; i >= 0; i--) {
            ArrayMethod.swap(nums, i, 0);
            judgeHeap(nums, i, 0);
        }
    }

    private static void initHeap(int[] nums) {
        int parentIndex = (nums.length - 1) >> 1;
        for (; parentIndex >= 0; parentIndex--) {
            judgeHeap(nums, nums.length, parentIndex);
        }
    }

    private static void judgeHeap(int[] nums, int size, int index) {
        int left = (index << 1) + 1;
        int right = (index << 1) + 2;
        int largest = index;

        if (left < size && nums[left] > nums[largest])
            largest = left;
        if (right < size && nums[right] > nums[largest])
            largest = right;
        if (largest != index) {
            ArrayMethod.swap(nums, largest, index);
            judgeHeap(nums, size, largest);
        }

    }

}