package benchmark;

public class BenchmarkResumo {

    private final String algorithmName;
    private final String mode;
    private final int threads;
    private final int arraySize;
    private final String inputType;
    private final int sampleCount;
    private final double averageTimeInMillis;
    private final double minTimeInMillis;
    private final double maxTimeInMillis;
    private final double standardDeviationInMillis;
    private final boolean allSortedCorrectly;

    public BenchmarkResumo(
            String algorithmName,
            String mode,
            int threads,
            int arraySize,
            String inputType,
            int sampleCount,
            double averageTimeInMillis,
            double minTimeInMillis,
            double maxTimeInMillis,
            double standardDeviationInMillis,
            boolean allSortedCorrectly
    ) {
        this.algorithmName = algorithmName;
        this.mode = mode;
        this.threads = threads;
        this.arraySize = arraySize;
        this.inputType = inputType;
        this.sampleCount = sampleCount;
        this.averageTimeInMillis = averageTimeInMillis;
        this.minTimeInMillis = minTimeInMillis;
        this.maxTimeInMillis = maxTimeInMillis;
        this.standardDeviationInMillis = standardDeviationInMillis;
        this.allSortedCorrectly = allSortedCorrectly;
    }

    public String getAlgorithmName() {
        return algorithmName;
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

    public int getSampleCount() {
        return sampleCount;
    }

    public double getAverageTimeInMillis() {
        return averageTimeInMillis;
    }

    public double getMinTimeInMillis() {
        return minTimeInMillis;
    }

    public double getMaxTimeInMillis() {
        return maxTimeInMillis;
    }

    public double getStandardDeviationInMillis() {
        return standardDeviationInMillis;
    }

    public boolean isAllSortedCorrectly() {
        return allSortedCorrectly;
    }
}
