package benchmark;

public class BenchmarkResultado {

    private final String algorithmName;
    private final String mode;
    private final int arraySize;
    private final String inputType;
    private final int threads;
    private final int sampleNumber;
    private final long executionTimeInNanos;
    private final boolean sortedCorrectly;

    public BenchmarkResultado(
            String algorithmName,
            String mode,
            int arraySize,
            String inputType,
            int threads,
            int sampleNumber,
            long executionTimeInNanos,
            boolean sortedCorrectly
    ) {
        this.algorithmName = algorithmName;
        this.mode = mode;
        this.arraySize = arraySize;
        this.inputType = inputType;
        this.threads = threads;
        this.sampleNumber = sampleNumber;
        this.executionTimeInNanos = executionTimeInNanos;
        this.sortedCorrectly = sortedCorrectly;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public String getMode() {
        return mode;
    }

    public int getArraySize() {
        return arraySize;
    }

    public String getInputType() {
        return inputType;
    }

    public int getThreads() {
        return threads;
    }

    public int getSampleNumber() {
        return sampleNumber;
    }

    public long getExecutionTimeInNanos() {
        return executionTimeInNanos;
    }

    public double getExecutionTimeInMillis() {
        return executionTimeInNanos / 1_000_000.0;
    }

    public boolean isSortedCorrectly() {
        return sortedCorrectly;
    }

    @Override
    public String toString() {
        return algorithmName +
                " | Modo: " + mode +
                " | Tamanho: " + arraySize +
                " | Entrada: " + inputType +
                " | Threads: " + threads +
                " | Amostra: " + sampleNumber +
                " | Tempo: " + String.format("%.3f", getExecutionTimeInMillis()) + " ms" +
                " | Ordenado: " + sortedCorrectly;
    }
}
