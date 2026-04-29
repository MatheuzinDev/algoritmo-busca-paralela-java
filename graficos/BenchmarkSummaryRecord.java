package graficos;

public class BenchmarkSummaryRecord {

    private final String algorithm;
    private final String mode;
    private final int threads;
    private final int arraySize;
    private final String inputType;
    private final int samples;
    private final double averageTimeMs;
    private final double minTimeMs;
    private final double maxTimeMs;
    private final double standardDeviationMs;
    private final boolean allSortedCorrectly;

    public BenchmarkSummaryRecord(
            String algorithm,
            String mode,
            int threads,
            int arraySize,
            String inputType,
            int samples,
            double averageTimeMs,
            double minTimeMs,
            double maxTimeMs,
            double standardDeviationMs,
            boolean allSortedCorrectly
    ) {
        this.algorithm = algorithm;
        this.mode = mode;
        this.threads = threads;
        this.arraySize = arraySize;
        this.inputType = inputType;
        this.samples = samples;
        this.averageTimeMs = averageTimeMs;
        this.minTimeMs = minTimeMs;
        this.maxTimeMs = maxTimeMs;
        this.standardDeviationMs = standardDeviationMs;
        this.allSortedCorrectly = allSortedCorrectly;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getMode() {
        return mode;
    }

    public int getThreads() {
        return threads;
    }

    public int getArraySize() {
        return arraySize;
    }

    public String getInputType() {
        return inputType;
    }

    public int getSamples() {
        return samples;
    }

    public double getAverageTimeMs() {
        return averageTimeMs;
    }

    public double getMinTimeMs() {
        return minTimeMs;
    }

    public double getMaxTimeMs() {
        return maxTimeMs;
    }

    public double getStandardDeviationMs() {
        return standardDeviationMs;
    }

    public boolean isAllSortedCorrectly() {
        return allSortedCorrectly;
    }
}
