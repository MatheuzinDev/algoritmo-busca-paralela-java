import algoritmos.QuickSort;
import algoritmos.AlgoritmoSort;
import utils.GerarArrays;
import utils.ArrayUtils;

public class Main {

    public static void main(String[] args) {
        int arraySize = 1_000_000;
        int maxValue = 1_000_000;

        int[] originalArray = GerarArrays.generateRandomArray(arraySize, maxValue);

        runTest(new QuickSort(false, 1), originalArray, 1);

        runTest(new QuickSort(true, 2), originalArray, 2);
        runTest(new QuickSort(true, 4), originalArray, 4);
        runTest(new QuickSort(true, 8), originalArray, 8);
    }

    private static void runTest(AlgoritmoSort algorithm, int[] originalArray, int threads) {
        int[] arrayToSort = ArrayUtils.copyArray(originalArray);

        long startTime = System.currentTimeMillis();

        algorithm.sort(arrayToSort);

        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;

        boolean sortedCorrectly = ArrayUtils.isSorted(arrayToSort);

        System.out.println("--------------------------------------");
        System.out.println("Algoritmo: " + algorithm.getName());
        System.out.println("Threads: " + threads);
        System.out.println("Tamanho do array: " + arrayToSort.length);
        System.out.println("Tempo de execução: " + executionTime + " ms");
        System.out.println("Ordenado corretamente: " + sortedCorrectly);
    }
}