package com.algorithm.sort;

/**
 * HeapSort
 */
public class HeapSort {

    public static void main(String[] args) {

        int[] data = new int[] { 1, 0, 10, 20, 3, 5, 6, 4, 9, 8, 12, 17, 34, 11 };
        int[] data2 = { 4, 6, 8, 5, 9 };
        System.out.println("data: " + ArrayMethod.StringfyArray(data));

        heapsort(data);
        System.out.println("heapSorted: " + ArrayMethod.StringfyArray(data));

    }

    public static void heapsort(int[] data) {
        buildMaxHeapify(data);
        heapSort(data);
    }

    private static void buildMaxHeapify(int[] data) {
        int startIndex = getParentIndex(data.length - 1);
        for (int i = startIndex; i >= 0; i--) {
            maxHeapify(data, data.length, i);
        }
    }

    private static void maxHeapify(int[] data, int heapSize, int index) {
        int left = getChildLeftIndex(index);
        int right = getChildRightIndex(index);

        int largest = index;
        if (left < heapSize && data[index] < data[left]) {
            largest = left;
        }
        if (right < heapSize && data[largest] < data[right]) {
            largest = right;
        }
        if (largest != index) {
            ArrayMethod.swap(data, index, largest);
            maxHeapify(data, heapSize, largest);
        }
    }

    private static void heapSort(int[] data) {
        for (int i = data.length - 1; i > 0; i--) {
            ArrayMethod.swap(data, 0, i);
            maxHeapify(data, i, 0);
        }
    }

    private static int getParentIndex(int current) {
        return (current - 1) >> 1;
    }

    private static int getChildLeftIndex(int current) {
        return (current << 1) + 1;
    }

    private static int getChildRightIndex(int current) {
        return (current << 1) + 2;
    }

}