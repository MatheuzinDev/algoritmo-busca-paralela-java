package algoritmos;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class MergeSort implements AlgoritmoSort {

    private final boolean parallel;
    private final int numberOfThreads;

    public MergeSort(boolean parallel, int numberOfThreads) {
        this.parallel = parallel;
        this.numberOfThreads = numberOfThreads;
    }

    @Override
    public void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }

        int[] auxiliaryArray = new int[array.length];

        if (parallel) {
            parallelMergeSort(array, auxiliaryArray);
        } else {
            mergeSort(array, auxiliaryArray, 0, array.length - 1);
        }
    }

    @Override
    public String getName() {
        if (parallel) {
            return "Merge Sort Parallel - " + numberOfThreads + " threads";
        }

        return "Merge Sort Serial";
    }

    private void mergeSort(int[] array, int[] auxiliaryArray, int left, int right) {
        if (left >= right) {
            return;
        }

        int middle = left + (right - left) / 2;

        mergeSort(array, auxiliaryArray, left, middle);
        mergeSort(array, auxiliaryArray, middle + 1, right);
        merge(array, auxiliaryArray, left, middle, right);
    }

    private void merge(int[] array, int[] auxiliaryArray, int left, int middle, int right) {
        System.arraycopy(array, left, auxiliaryArray, left, right - left + 1);

        int leftIndex = left;
        int rightIndex = middle + 1;
        int mergedIndex = left;

        while (leftIndex <= middle && rightIndex <= right) {
            if (auxiliaryArray[leftIndex] <= auxiliaryArray[rightIndex]) {
                array[mergedIndex++] = auxiliaryArray[leftIndex++];
            } else {
                array[mergedIndex++] = auxiliaryArray[rightIndex++];
            }
        }

        while (leftIndex <= middle) {
            array[mergedIndex++] = auxiliaryArray[leftIndex++];
        }

        while (rightIndex <= right) {
            array[mergedIndex++] = auxiliaryArray[rightIndex++];
        }
    }

    private void parallelMergeSort(int[] array, int[] auxiliaryArray) {
        ForkJoinPool pool = new ForkJoinPool(numberOfThreads);
        pool.invoke(new MergeSortTask(array, auxiliaryArray, 0, array.length - 1));
        pool.shutdown();
    }

    private static class MergeSortTask extends RecursiveAction {

        private static final int THRESHOLD = 10_000;

        private final int[] array;
        private final int[] auxiliaryArray;
        private final int left;
        private final int right;

        public MergeSortTask(int[] array, int[] auxiliaryArray, int left, int right) {
            this.array = array;
            this.auxiliaryArray = auxiliaryArray;
            this.left = left;
            this.right = right;
        }

        @Override
        protected void compute() {
            if (left >= right) {
                return;
            }

            if (right - left < THRESHOLD) {
                sequentialMergeSort(array, auxiliaryArray, left, right);
                return;
            }

            int middle = left + (right - left) / 2;

            MergeSortTask leftTask = new MergeSortTask(array, auxiliaryArray, left, middle);
            MergeSortTask rightTask = new MergeSortTask(array, auxiliaryArray, middle + 1, right);

            invokeAll(leftTask, rightTask);
            merge(array, auxiliaryArray, left, middle, right);
        }

        private static void sequentialMergeSort(int[] array, int[] auxiliaryArray, int left, int right) {
            if (left >= right) {
                return;
            }

            int middle = left + (right - left) / 2;

            sequentialMergeSort(array, auxiliaryArray, left, middle);
            sequentialMergeSort(array, auxiliaryArray, middle + 1, right);
            merge(array, auxiliaryArray, left, middle, right);
        }

        private static void merge(int[] array, int[] auxiliaryArray, int left, int middle, int right) {
            System.arraycopy(array, left, auxiliaryArray, left, right - left + 1);

            int leftIndex = left;
            int rightIndex = middle + 1;
            int mergedIndex = left;

            while (leftIndex <= middle && rightIndex <= right) {
                if (auxiliaryArray[leftIndex] <= auxiliaryArray[rightIndex]) {
                    array[mergedIndex++] = auxiliaryArray[leftIndex++];
                } else {
                    array[mergedIndex++] = auxiliaryArray[rightIndex++];
                }
            }

            while (leftIndex <= middle) {
                array[mergedIndex++] = auxiliaryArray[leftIndex++];
            }

            while (rightIndex <= right) {
                array[mergedIndex++] = auxiliaryArray[rightIndex++];
            }
        }
    }
}
