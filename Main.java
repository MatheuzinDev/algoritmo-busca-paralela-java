import algoritmos.QuickSort;
import algoritmos.AlgoritmoSort;
import algoritmos.InsertionSort;
import algoritmos.MergeSort;
import algoritmos.SelectionSort;
import utils.GerarArrays;
import utils.ArrayUtils;

public class Main {

    public static void main(String[] args) {
        int arraySize = 1_000_000;
        int insertionSortArraySize = 20_000;
        int selectionSortArraySize = 20_000;
        int maxValue = 1_000_000;

        int[] originalArray = GerarArrays.generateRandomArray(arraySize, maxValue);
        int[] insertionSortArray = GerarArrays.generateRandomArray(insertionSortArraySize, maxValue);
        int[] selectionSortArray = GerarArrays.generateRandomArray(selectionSortArraySize, maxValue);

        runTest(new QuickSort(false, 1), originalArray, 1);
        runTest(new MergeSort(false, 1), originalArray, 1);
        runTest(new SelectionSort(false, 1), selectionSortArray, 1);
        runTest(new InsertionSort(false, 1), insertionSortArray, 1);

        runTest(new QuickSort(true, 2), originalArray, 2);
        runTest(new QuickSort(true, 4), originalArray, 4);
        runTest(new QuickSort(true, 8), originalArray, 8);

        runTest(new MergeSort(true, 2), originalArray, 2);
        runTest(new MergeSort(true, 4), originalArray, 4);
        runTest(new MergeSort(true, 8), originalArray, 8);

        runTest(new SelectionSort(true, 2), selectionSortArray, 2);
        runTest(new SelectionSort(true, 4), selectionSortArray, 4);
        runTest(new SelectionSort(true, 8), selectionSortArray, 8);

        runTest(new InsertionSort(true, 2), insertionSortArray, 2);
        runTest(new InsertionSort(true, 4), insertionSortArray, 4);
        runTest(new InsertionSort(true, 8), insertionSortArray, 8);
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
