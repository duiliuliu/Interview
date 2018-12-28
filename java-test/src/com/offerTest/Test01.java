package com.offerTest;

import java.util.HashMap;

import com.algorithm.sort.ArrayMethod;
import com.algorithm.sort.QuickSort;

/**
 * Test01
 * 
 * ��ָofferTest03���������ظ�������
 */
public class Test01 {

    public static void main(String[] args) {
        int[] nums = { 2, 3, 1, 0, 2, 5, 3 };
        // solve01(nums);
        // solve02(nums);
        solve03(nums);
    }

    // ����������Ȼ����������Ƚ�
    public static void solve01(int[] nums) {
        QuickSort.quicksort(nums);
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] == nums[i + 1]) {
                System.out.println(nums[i]);
            }
        }
    }

    // ����hash
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

    // ����Ԫ����0~n-1���ڵ����֣������е�����ӳ�䵽��Ӧ��λ�á�
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