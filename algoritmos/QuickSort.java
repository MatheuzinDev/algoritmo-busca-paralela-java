package algoritmos;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class QuickSort implements AlgoritmoSort {

    private final boolean parallel;
    private final int numberOfThreads;

    public QuickSort(boolean parallel, int numberOfThreads) {
        this.parallel = parallel;
        this.numberOfThreads = numberOfThreads;
    }

    @Override
    public void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }

        if (parallel) {
            parallelQuickSort(array);
        } else {
            quickSort(array, 0, array.length - 1);
        }
    }

    @Override
    public String getName() {
        if (parallel) {
            return "Quick Sort Parallel - " + numberOfThreads + " threads";
        }

        return "Quick Sort Serial";
    }

    private void quickSort(int[] array, int left, int right) {
        if (left < right) {
            int pivotIndex = partition(array, left, right);

            quickSort(array, left, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, right);
        }
    }

    private int partition(int[] array, int left, int right) {
        int pivot = array[right];
        int smallerIndex = left - 1;

        for (int currentIndex = left; currentIndex < right; currentIndex++) {
            if (array[currentIndex] <= pivot) {
                smallerIndex++;
                swap(array, smallerIndex, currentIndex);
            }
        }

        swap(array, smallerIndex + 1, right);

        return smallerIndex + 1;
    }

    private void swap(int[] array, int firstIndex, int secondIndex) {
        int temp = array[firstIndex];
        array[firstIndex] = array[secondIndex];
        array[secondIndex] = temp;
    }

    private void parallelQuickSort(int[] array) {
        ForkJoinPool pool = new ForkJoinPool(numberOfThreads);
        pool.invoke(new QuickSortTask(array, 0, array.length - 1));
        pool.shutdown();
    }

    private static class QuickSortTask extends RecursiveAction {

        private static final int THRESHOLD = 10_000;

        private final int[] array;
        private final int left;
        private final int right;

        public QuickSortTask(int[] array, int left, int right) {
            this.array = array;
            this.left = left;
            this.right = right;
        }

        @Override
        protected void compute() {
            if (left < right) {
                if (right - left < THRESHOLD) {
                    sequentialQuickSort(array, left, right);
                } else {
                    int pivotIndex = partition(array, left, right);

                    QuickSortTask leftTask = new QuickSortTask(array, left, pivotIndex - 1);
                    QuickSortTask rightTask = new QuickSortTask(array, pivotIndex + 1, right);

                    invokeAll(leftTask, rightTask);
                }
            }
        }

        private static void sequentialQuickSort(int[] array, int left, int right) {
            if (left < right) {
                int pivotIndex = partition(array, left, right);

                sequentialQuickSort(array, left, pivotIndex - 1);
                sequentialQuickSort(array, pivotIndex + 1, right);
            }
        }

        private static int partition(int[] array, int left, int right) {
            int pivot = array[right];
            int smallerIndex = left - 1;

            for (int currentIndex = left; currentIndex < right; currentIndex++) {
                if (array[currentIndex] <= pivot) {
                    smallerIndex++;
                    swap(array, smallerIndex, currentIndex);
                }
            }

            swap(array, smallerIndex + 1, right);

            return smallerIndex + 1;
        }

        private static void swap(int[] array, int firstIndex, int secondIndex) {
            int temp = array[firstIndex];
            array[firstIndex] = array[secondIndex];
            array[secondIndex] = temp;
        }
    }
}