package benchmark;

import utils.GerarArrays;

public enum InputType {
    RANDOM("random") {
        @Override
        public int[] generateArray(int size, int maxValue, long seed) {
            return GerarArrays.generateRandomArray(size, maxValue, seed);
        }
    },
    SORTED("sorted") {
        @Override
        public int[] generateArray(int size, int maxValue, long seed) {
            return GerarArrays.generateSortedArray(size);
        }
    },
    REVERSED("reversed") {
        @Override
        public int[] generateArray(int size, int maxValue, long seed) {
            return GerarArrays.generateReversedArray(size);
        }
    },
    PARTIALLY_SORTED("partially_sorted") {
        @Override
        public int[] generateArray(int size, int maxValue, long seed) {
            return GerarArrays.generatePartiallySortedArray(size, maxValue, seed);
        }
    };

    private final String label;

    InputType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public abstract int[] generateArray(int size, int maxValue, long seed);
}
