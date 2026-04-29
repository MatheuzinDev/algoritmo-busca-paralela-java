package algoritmos;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class InsertionSort implements AlgoritmoSort {

    private final boolean parallel;
    private final int numberOfThreads;

    public InsertionSort(boolean parallel, int numberOfThreads) {
        this.parallel = parallel;
        this.numberOfThreads = numberOfThreads;
    }

    @Override
    public void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }

        if (parallel) {
            parallelInsertionSort(array);
        } else {
            insertionSort(array);
        }
    }

    @Override
    public String getName() {
        if (parallel) {
            return "Insertion Sort Parallel - " + numberOfThreads + " threads";
        }

        return "Insertion Sort Serial";
    }

    private void insertionSort(int[] array) {
        for (int currentIndex = 1; currentIndex < array.length; currentIndex++) {
            insertValueAtCorrectPosition(array, currentIndex, findInsertionIndexSequential(array, currentIndex));
        }
    }

    private void parallelInsertionSort(int[] array) {
        ForkJoinPool pool = new ForkJoinPool(numberOfThreads);

        for (int currentIndex = 1; currentIndex < array.length; currentIndex++) {
            int insertionIndex = pool.invoke(new FindInsertionIndexTask(array, 0, currentIndex, array[currentIndex]));
            insertValueAtCorrectPosition(array, currentIndex, insertionIndex);
        }

        pool.shutdown();
    }

    private int findInsertionIndexSequential(int[] array, int currentIndex) {
        int value = array[currentIndex];
        int insertionIndex = currentIndex;

        while (insertionIndex > 0 && array[insertionIndex - 1] > value) {
            insertionIndex--;
        }

        return insertionIndex;
    }

    private void insertValueAtCorrectPosition(int[] array, int currentIndex, int insertionIndex) {
        int value = array[currentIndex];

        for (int index = currentIndex; index > insertionIndex; index--) {
            array[index] = array[index - 1];
        }

        array[insertionIndex] = value;
    }

    private static class FindInsertionIndexTask extends RecursiveTask<Integer> {

        private static final int THRESHOLD = 4_096;

        private final int[] array;
        private final int left;
        private final int rightExclusive;
        private final int value;

        public FindInsertionIndexTask(int[] array, int left, int rightExclusive, int value) {
            this.array = array;
            this.left = left;
            this.rightExclusive = rightExclusive;
            this.value = value;
        }

        @Override
        protected Integer compute() {
            if (rightExclusive - left <= THRESHOLD) {
                return findInsertionIndexSequential(array, left, rightExclusive, value);
            }

            int middle = left + (rightExclusive - left) / 2;

            FindInsertionIndexTask leftTask = new FindInsertionIndexTask(array, left, middle, value);
            FindInsertionIndexTask rightTask = new FindInsertionIndexTask(array, middle, rightExclusive, value);

            leftTask.fork();
            int rightResult = rightTask.compute();
            int leftResult = leftTask.join();

            if (leftResult < middle) {
                return leftResult;
            }

            return rightResult;
        }

        private static int findInsertionIndexSequential(int[] array, int left, int rightExclusive, int value) {
            for (int index = left; index < rightExclusive; index++) {
                if (array[index] > value) {
                    return index;
                }
            }

            return rightExclusive;
        }
    }
}
