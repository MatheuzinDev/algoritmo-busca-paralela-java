package algoritmos;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class SelectionSort implements AlgoritmoSort {

    private final boolean parallel;
    private final int numberOfThreads;

    public SelectionSort(boolean parallel, int numberOfThreads) {
        this.parallel = parallel;
        this.numberOfThreads = numberOfThreads;
    }

    @Override
    public void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }

        if (parallel) {
            parallelSelectionSort(array);
        } else {
            selectionSort(array);
        }
    }

    @Override
    public String getName() {
        if (parallel) {
            return "Selection Sort Parallel - " + numberOfThreads + " threads";
        }

        return "Selection Sort Serial";
    }

    private void selectionSort(int[] array) {
        for (int currentIndex = 0; currentIndex < array.length - 1; currentIndex++) {
            int minIndex = findMinSequential(array, currentIndex, array.length - 1);
            swap(array, currentIndex, minIndex);
        }
    }

    private void parallelSelectionSort(int[] array) {
        ForkJoinPool pool = new ForkJoinPool(numberOfThreads);

        for (int currentIndex = 0; currentIndex < array.length - 1; currentIndex++) {
            int minIndex = pool.invoke(new FindMinTask(array, currentIndex, array.length - 1));
            swap(array, currentIndex, minIndex);
        }

        pool.shutdown();
    }

    private int findMinSequential(int[] array, int left, int right) {
        int minIndex = left;

        for (int index = left + 1; index <= right; index++) {
            if (array[index] < array[minIndex]) {
                minIndex = index;
            }
        }

        return minIndex;
    }

    private void swap(int[] array, int firstIndex, int secondIndex) {
        int temp = array[firstIndex];
        array[firstIndex] = array[secondIndex];
        array[secondIndex] = temp;
    }

    private static class FindMinTask extends RecursiveTask<Integer> {

        private static final int THRESHOLD = 10_000;

        private final int[] array;
        private final int left;
        private final int right;

        public FindMinTask(int[] array, int left, int right) {
            this.array = array;
            this.left = left;
            this.right = right;
        }

        @Override
        protected Integer compute() {
            if (right - left < THRESHOLD) {
                return findMinSequential(array, left, right);
            }

            int middle = left + (right - left) / 2;

            FindMinTask leftTask = new FindMinTask(array, left, middle);
            FindMinTask rightTask = new FindMinTask(array, middle + 1, right);

            leftTask.fork();
            int rightMinIndex = rightTask.compute();
            int leftMinIndex = leftTask.join();

            return array[leftMinIndex] <= array[rightMinIndex] ? leftMinIndex : rightMinIndex;
        }

        private static int findMinSequential(int[] array, int left, int right) {
            int minIndex = left;

            for (int index = left + 1; index <= right; index++) {
                if (array[index] < array[minIndex]) {
                    minIndex = index;
                }
            }

            return minIndex;
        }
    }
}
