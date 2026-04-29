package graficos;

public class GraphFilterState {

    public static final String ALL = "Todos";
    public static final String FAST_GROUP = "Quick + Merge";
    public static final String QUADRATIC_GROUP = "Insertion + Selection";

    private ChartType chartType = ChartType.SCALABILITY;
    private String algorithmGroup = FAST_GROUP;
    private String algorithm = "Quick Sort";
    private String inputType = "random";
    private String mode = "serial";
    private Integer arraySize;
    private Integer threads = 4;

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public String getAlgorithmGroup() {
        return algorithmGroup;
    }

    public void setAlgorithmGroup(String algorithmGroup) {
        this.algorithmGroup = algorithmGroup;
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

    public boolean isAlgorithmInSelectedGroup(String algorithmName) {
        if (FAST_GROUP.equals(algorithmGroup)) {
            return "Quick Sort".equals(algorithmName) || "Merge Sort".equals(algorithmName);
        }

        if (QUADRATIC_GROUP.equals(algorithmGroup)) {
            return "Insertion Sort".equals(algorithmName) || "Selection Sort".equals(algorithmName);
        }

        return true;
    }
}
