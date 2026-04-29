package utils;

public class ArrayUtils {

    public static int[] copyArray(int[] originalArray) {
        int[] copy = new int[originalArray.length];

        System.arraycopy(originalArray, 0, copy, 0, originalArray.length);

        return copy;
    }

    public static boolean isSorted(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1] > array[i]) {
                return false;
            }
        }

        return true;
    }
}