package graficos;

public class GraphFilterState {

    public static final String ALL = "Todos";

    private ChartType chartType = ChartType.BAR;
    private String algorithm = ALL;
    private String inputType = ALL;
    private String mode = ALL;
    private Integer arraySize;
    private Integer threads;

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getArraySize() {
        return arraySize;
    }

    public void setArraySize(Integer arraySize) {
        this.arraySize = arraySize;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public boolean matches(BenchmarkSummaryRecord record) {
        return matchesAlgorithm(record)
                && matchesInputType(record)
                && matchesMode(record)
                && matchesArraySize(record)
                && matchesThreads(record);
    }

    private boolean matchesAlgorithm(BenchmarkSummaryRecord record) {
        return ALL.equals(algorithm) || algorithm.equals(record.getAlgorithm());
    }

    private boolean matchesInputType(BenchmarkSummaryRecord record) {
        return ALL.equals(inputType) || inputType.equals(record.getInputType());
    }

    private boolean matchesMode(BenchmarkSummaryRecord record) {
        return ALL.equals(mode) || mode.equals(record.getMode());
    }

    private boolean matchesArraySize(BenchmarkSummaryRecord record) {
        return arraySize == null || arraySize == record.getArraySize();
    }

    private boolean matchesThreads(BenchmarkSummaryRecord record) {
        return threads == null || threads == record.getThreads();
    }
}
