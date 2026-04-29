package benchmark;

public enum ExecutionMode {
    SERIAL("serial"),
    PARALLEL("parallel");

    private final String label;

    ExecutionMode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
