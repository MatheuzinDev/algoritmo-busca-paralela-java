package graficos;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SummaryTableModel extends AbstractTableModel {

    private final String[] columns = new String[]{
            "Algoritmo", "Modo", "Threads", "Tamanho", "Entrada", "Media (ms)", "Desvio"
    };

    private List<BenchmarkSummaryRecord> records = new ArrayList<>();

    public void setRecords(List<BenchmarkSummaryRecord> records) {
        this.records = new ArrayList<>(records);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return records.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        BenchmarkSummaryRecord record = records.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> record.getAlgorithm();
            case 1 -> record.getMode();
            case 2 -> record.getThreads();
            case 3 -> record.getArraySize();
            case 4 -> record.getInputType();
            case 5 -> String.format("%.3f", record.getAverageTimeMs());
            case 6 -> String.format("%.3f", record.getStandardDeviationMs());
            default -> "";
        };
    }
}
