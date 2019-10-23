package lesson1;

import java.util.Arrays;

class Util {

    private static <T extends Comparable<T>> void genericMerge(T[] initialArray, int left, int middle, int right) {
        T[] leftPart = Arrays.copyOfRange(initialArray, left, middle);
        T[] rightPart = Arrays.copyOfRange(initialArray, middle, right);

        int leftIndex = 0;
        int rightIndex = 0;

        for (int i = left; i < right; i++) {
            if (leftIndex == leftPart.length ||
                    (rightIndex < rightPart.length
                            && rightPart[rightIndex].compareTo(leftPart[leftIndex]) < 0)) {
                initialArray[i] = rightPart[rightIndex++];
            } else {
                initialArray[i] = leftPart[leftIndex++];
            }
        }
    }

    private static <T extends Comparable<T>> void genericMergeSort(T[] initialArray, int left, int right) {
        if (right - left == 1) {
            return;
        }

        int middle = (left + right) / 2;

        genericMergeSort(initialArray, left, middle);
        genericMergeSort(initialArray, middle, right);
        genericMerge(initialArray, left, middle, right);
    }

    static <T extends Comparable<T>> void genericMergeSort(T[] initialArray) {
        genericMergeSort(initialArray, 0, initialArray.length);
    }
}
