package graficos;

public enum ChartType {
    BAR("Barras"),
    LINE("Linhas"),
    SPEEDUP("Speedup");

    private final String displayName;

    ChartType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
