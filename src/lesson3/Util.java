package lesson3;

import java.util.ArrayList;

class Util {
    private static <T extends Comparable<T>> int binarySearch(int left, int right, T toSearch, ArrayList<T> list) {
        if (right <= left) {
            return right;
        }
        int mid = (left + right) / 2;
        if (toSearch.compareTo(list.get(mid)) <= 0) {
            return binarySearch(left, mid, toSearch, list);
        } else {
            return binarySearch(mid + 1, right, toSearch, list);
        }
    }

    static <T extends Comparable<T>> int binarySearch(T toSearch, ArrayList<T> list) {
        return binarySearch(0, list.size() - 1, toSearch, list);
    }
}
