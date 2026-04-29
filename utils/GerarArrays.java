package utils;

import java.util.Random;

public class GerarArrays {

    public static int[] generateRandomArray(int size, int maxValue) {
        return generateRandomArray(size, maxValue, new Random());
    }

    public static int[] generateRandomArray(int size, int maxValue, long seed) {
        return generateRandomArray(size, maxValue, new Random(seed));
    }

    private static int[] generateRandomArray(int size, int maxValue, Random random) {
        int[] array = new int[size];

        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(maxValue);
        }

        return array;
    }

    public static int[] generateSortedArray(int size) {
        int[] array = new int[size];

        for (int i = 0; i < size; i++) {
            array[i] = i;
        }

        return array;
    }

    public static int[] generateReversedArray(int size) {
        int[] array = new int[size];

        for (int i = 0; i < size; i++) {
            array[i] = size - i;
        }

        return array;
    }

    public static int[] generatePartiallySortedArray(int size, int maxValue) {
        return generatePartiallySortedArray(size, maxValue, new Random());
    }

    public static int[] generatePartiallySortedArray(int size, int maxValue, long seed) {
        return generatePartiallySortedArray(size, maxValue, new Random(seed));
    }

    private static int[] generatePartiallySortedArray(int size, int maxValue, Random random) {
        int[] array = generateSortedArray(size);

        int changes = size / 10;

        for (int i = 0; i < changes; i++) {
            int firstIndex = random.nextInt(size);
            int secondIndex = random.nextInt(size);

            int temp = array[firstIndex];
            array[firstIndex] = array[secondIndex];
            array[secondIndex] = temp;
        }

        return array;
    }
}
