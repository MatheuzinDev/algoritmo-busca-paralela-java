package benchmark;

import java.util.Arrays;

public class BenchmarkConfig {

    private final SortAlgorithmType[] algorithms;
    private final InputType[] inputTypes;
    private final int[] parallelThreadCounts;
    private final int sampleCount;
    private final int maxValue;
    private final String outputDirectory;

    public BenchmarkConfig(
            SortAlgorithmType[] algorithms,
            InputType[] inputTypes,
            int[] parallelThreadCounts,
            int sampleCount,
            int maxValue,
            String outputDirectory
    ) {
        this.algorithms = algorithms;
        this.inputTypes = inputTypes;
        this.parallelThreadCounts = parallelThreadCounts;
        this.sampleCount = sampleCount;
        this.maxValue = maxValue;
        this.outputDirectory = outputDirectory;
    }

    public static BenchmarkConfig defaultConfig() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int[] desiredThreads = new int[]{2, 4, 8};
        int[] usableThreads = Arrays.stream(desiredThreads)
                .filter(threadCount -> threadCount <= availableProcessors)
                .distinct()
                .toArray();

        if (usableThreads.length == 0 && availableProcessors > 1) {
            usableThreads = new int[]{availableProcessors};
        }

        return new BenchmarkConfig(
                SortAlgorithmType.values(),
                InputType.values(),
                usableThreads,
                5,
                1_000_000,
                "resultados"
        );
    }

    public SortAlgorithmType[] getAlgorithms() {
        return algorithms;
    }

    public InputType[] getInputTypes() {
        return inputTypes;
    }

    public int[] getParallelThreadCounts() {
        return parallelThreadCounts;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }
}
