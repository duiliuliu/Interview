package com.offerTest;

import java.util.HashMap;

import com.algorithm.sort.ArrayMethod;
import com.algorithm.sort.QuickSort;

/**
 * Test01
 * 
 * 剑指offerTest03：数组中重复的数字
 */
public class Test01 {

    public static void main(String[] args) {
        int[] nums = { 2, 3, 1, 0, 2, 5, 3 };
        // solve01(nums);
        // solve02(nums);
        solve03(nums);
    }

    // 将数组排序，然后进行两两比较
    public static void solve01(int[] nums) {
        QuickSort.quicksort(nums);
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] == nums[i + 1]) {
                System.out.println(nums[i]);
            }
        }
    }

    // 利用hash
    public static void solve02(int[] nums) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i : nums) {
            if (map.containsKey(i)) {
                map.put(i, map.get(i) + 1);
                System.out.println(i);
            }
            map.put(i, 1);
        }
    }

    // 数组元素是0~n-1以内的数字，则将所有的数字映射到相应的位置。
    public static void solve03(int[] nums) {
        int i = 0;
        while (i < nums.length) {
            if (nums[i] != i) {
                if (nums[i] == nums[nums[i]]) {
                    System.out.println(nums[i]);
                    i++;
                } else {
                    ArrayMethod.swap(nums, nums[i], i);
                }
            } else {
                i++;
            }
        }
    }

}