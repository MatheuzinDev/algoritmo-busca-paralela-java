package graficos;

public enum ChartType {
    SCALABILITY("Escalabilidade"),
    SERIAL_VS_PARALLEL("Serial vs Paralelo"),
    INPUT_IMPACT("Impacto da Entrada"),
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
