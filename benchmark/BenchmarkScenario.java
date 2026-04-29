package benchmark;

public class BenchmarkScenario {

    private final SortAlgorithmType algorithm;
    private final ExecutionMode mode;
    private final int threads;
    private final int arraySize;
    private final InputType inputType;
    private final int sampleNumber;

    public BenchmarkScenario(
            SortAlgorithmType algorithm,
            ExecutionMode mode,
            int threads,
            int arraySize,
            InputType inputType,
            int sampleNumber
    ) {
        this.algorithm = algorithm;
        this.mode = mode;
        this.threads = threads;
        this.arraySize = arraySize;
        this.inputType = inputType;
        this.sampleNumber = sampleNumber;
    }

    public SortAlgorithmType getAlgorithm() {
        return algorithm;
    }

    public ExecutionMode getMode() {
        return mode;
    }

    public int getThreads() {
        return threads;
    }

    public int getArraySize() {
        return arraySize;
    }

    public InputType getInputType() {
        return inputType;
    }

    public int getSampleNumber() {
        return sampleNumber;
    }

    public long buildSeed() {
        long seed = 17L;
        seed = seed * 31 + algorithm.ordinal();
        seed = seed * 31 + mode.ordinal();
        seed = seed * 31 + threads;
        seed = seed * 31 + arraySize;
        seed = seed * 31 + inputType.ordinal();
        seed = seed * 31 + sampleNumber;
        return seed;
    }
}
