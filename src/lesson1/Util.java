package lesson1;

import java.util.Arrays;

class Util {
    private static void mergeInt(int[] initialArray, int left, int middle, int right) {
        int[] leftPart = Arrays.copyOfRange(initialArray, left, middle);
        int[] rightPart = Arrays.copyOfRange(initialArray, middle, right);

        int leftIndex = 0;
        int rightIndex = 0;

        for (int i = left; i < right; i++) {
            if (leftIndex == leftPart.length ||
                    (rightIndex < rightPart.length && leftPart[leftIndex] > rightPart[rightIndex])) {
                initialArray[i] = rightPart[rightIndex++];
            } else {
                initialArray[i] = leftPart[leftIndex++];
            }
        }
    }

    static void mergeSortInt(int[] initialArray, int left, int right) {
        if (right - left == 1) {
            return;
        }

        int middle = (left + right) / 2;

        mergeSortInt(initialArray, left, middle);
        mergeSortInt(initialArray, middle, right);
        mergeInt(initialArray, left, middle, right);
    }

    private static void mergeDouble(double[] initialArray, int left, int middle, int right) {
        double[] leftPart = Arrays.copyOfRange(initialArray, left, middle);
        double[] rightPart = Arrays.copyOfRange(initialArray, middle, right);

        int leftIndex = 0;
        int rightIndex = 0;

        for (int i = left; i < right; i++) {
            if (leftIndex == leftPart.length ||
                    (rightIndex < rightPart.length && leftPart[leftIndex] > rightPart[rightIndex])) {
                initialArray[i] = rightPart[rightIndex++];
            } else {
                initialArray[i] = leftPart[leftIndex++];
            }
        }
    }

    static void mergeSortDouble(double[] initialArray, int left, int right) {
        if (right - left == 1) {
            return;
        }

        int middle = (left + right) / 2;

        mergeSortDouble(initialArray, left, middle);
        mergeSortDouble(initialArray, middle, right);
        mergeDouble(initialArray, left, middle, right);
    }

    static String alphabeticallyFirstString(String first, String second, int index) {

        if (first.equals(second)) {
            return null;
        }

        if (index >= second.length()) {
            return second;
        }

        if (index >= first.length()) {
            return first;
        }

        char letterFromFirst = first.charAt(index);
        char letterFromSecond = second.charAt(index);

        if (letterFromFirst == '-') {
            letterFromFirst += 2000;
        }

        if (letterFromSecond == '-') {
            letterFromSecond += 2000;
        }

        if (letterFromFirst == ' ' && letterFromSecond != ' ') {
            return first;
        }

        if (letterFromSecond == ' ' && letterFromFirst != ' ') {
            return second;
        }

        if (letterFromFirst == letterFromSecond) {
            return alphabeticallyFirstString(first, second, index + 1);
        }

        if (letterFromFirst == 'Ё'/* && letterFromSecond <= 'Е' || letterFromFirst == 'ё' && letterFromSecond <= 'е'*/) {
            return second;
        }

        /*if (letterFromFirst == 'Ё' || letterFromFirst == 'ё') {
            return first;
        }*/

        if (letterFromSecond == 'Ё'/* && letterFromFirst <= 'Е' || letterFromSecond == 'ё' && letterFromFirst <= 'е'*/) {
            return first;
        }

        /*if (letterFromSecond == 'Ё' || letterFromSecond == 'ё') {
            return second;
        }*/

        // По неизвестным науке причинам тесты полагают верным алфавитным порядком такой, при котором буква Ё -
        // последняя в русском алфавите. Закомменченные фрагменты кода при возвращении в программу позволяют
        // работать с традиционным алфавитным порядком, в котором буква Ё стоит между Е и Ж.

        if (letterFromFirst > letterFromSecond) {
            return second;
        } else {
            return first;
        }
    }

    private static boolean firstEarlierThanSecond(String first, String second) {
        String earlier = alphabeticallyFirstString(first, second, 0);
        return earlier == null || earlier.equals(first);
    }

    private static void mergeByAddress(HouseWithInhabitants[] initialArray, int left, int middle, int right) {
        HouseWithInhabitants[] leftPart = Arrays.copyOfRange(initialArray, left, middle);
        HouseWithInhabitants[] rightPart = Arrays.copyOfRange(initialArray, middle, right);

        int leftIndex = 0;
        int rightIndex = 0;

        for (int i = left; i < right; i++) {
            if (leftIndex == leftPart.length ||
                    (rightIndex < rightPart.length &&
                            rightPart[rightIndex].address.isAlphabeticallyEarlier(leftPart[leftIndex].address))) {
                initialArray[i] = rightPart[rightIndex++];
            } else {
                initialArray[i] = leftPart[leftIndex++];
            }
        }
    }

    static void mergeSortByAddress(HouseWithInhabitants[] initialArray, int left, int right) {
        if (right - left == 1) {
            return;
        }
        int middle = (left + right) / 2;
        mergeSortByAddress(initialArray, left, middle);
        mergeSortByAddress(initialArray, middle, right);
        mergeByAddress(initialArray, left, middle, right);
    }

    private static void mergeString(String[] initialArray, int left, int middle, int right) {
        String[] leftPart = Arrays.copyOfRange(initialArray, left, middle);
        String[] rightPart = Arrays.copyOfRange(initialArray, middle, right);

        int leftIndex = 0;
        int rightIndex = 0;

        for (int i = left; i < right; i++) {
            if (leftIndex == leftPart.length ||
                    (rightIndex < rightPart.length
                            && firstEarlierThanSecond(rightPart[rightIndex], leftPart[leftIndex]))) {
                initialArray[i] = rightPart[rightIndex++];
            } else {
                initialArray[i] = leftPart[leftIndex++];
            }
        }
    }

    static void mergeSortString(String[] initialArray, int left, int right) {
        if (right - left == 1) {
            return;
        }

        int middle = (left + right) / 2;

        mergeSortString(initialArray, left, middle);
        mergeSortString(initialArray, middle, right);
        mergeString(initialArray, left, middle, right);
    }
}
