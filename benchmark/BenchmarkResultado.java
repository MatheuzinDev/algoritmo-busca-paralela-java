package benchmark;

public class BenchmarkResultado {

    private final String algorithmName;
    private final int arraySize;
    private final String inputType;
    private final int threads;
    private final long executionTimeInMillis;
    private final boolean sortedCorrectly;

    public BenchmarkResultado(
            String algorithmName,
            int arraySize,
            String inputType,
            int threads,
            long executionTimeInMillis,
            boolean sortedCorrectly
    ) {
        this.algorithmName = algorithmName;
        this.arraySize = arraySize;
        this.inputType = inputType;
        this.threads = threads;
        this.executionTimeInMillis = executionTimeInMillis;
        this.sortedCorrectly = sortedCorrectly;
    }

    public String getAlgorithmName() {
        return algorithmName;
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

    public long getExecutionTimeInMillis() {
        return executionTimeInMillis;
    }

    public boolean isSortedCorrectly() {
        return sortedCorrectly;
    }

    @Override
    public String toString() {
        return algorithmName +
                " | Tamanho: " + arraySize +
                " | Entrada: " + inputType +
                " | Threads: " + threads +
                " | Tempo: " + executionTimeInMillis + " ms" +
                " | Ordenado: " + sortedCorrectly;
    }
}