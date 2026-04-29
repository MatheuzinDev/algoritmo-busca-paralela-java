package benchmark;

import algoritmos.AlgoritmoSort;
import algoritmos.InsertionSort;
import algoritmos.MergeSort;
import algoritmos.QuickSort;
import algoritmos.SelectionSort;

public enum SortAlgorithmType {
    QUICK_SORT("Quick Sort", new int[]{100_000, 250_000, 500_000}) {
        @Override
        public AlgoritmoSort create(boolean parallel, int threads) {
            return new QuickSort(parallel, threads);
        }
    },
    MERGE_SORT("Merge Sort", new int[]{100_000, 250_000, 500_000}) {
        @Override
        public AlgoritmoSort create(boolean parallel, int threads) {
            return new MergeSort(parallel, threads);
        }
    },
    INSERTION_SORT("Insertion Sort", new int[]{2_000, 5_000, 10_000}) {
        @Override
        public AlgoritmoSort create(boolean parallel, int threads) {
            return new InsertionSort(parallel, threads);
        }
    },
    SELECTION_SORT("Selection Sort", new int[]{2_000, 5_000, 10_000}) {
        @Override
        public AlgoritmoSort create(boolean parallel, int threads) {
            return new SelectionSort(parallel, threads);
        }
    };

    private final String displayName;
    private final int[] arraySizes;

    SortAlgorithmType(String displayName, int[] arraySizes) {
        this.displayName = displayName;
        this.arraySizes = arraySizes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int[] getArraySizes() {
        return arraySizes.clone();
    }

    public abstract AlgoritmoSort create(boolean parallel, int threads);
}
