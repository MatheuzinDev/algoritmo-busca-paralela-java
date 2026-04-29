package graficos;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GraphFrame extends JFrame {

    private final List<BenchmarkSummaryRecord> records;
    private final GraphFilterState filterState;
    private final ChartPanel chartPanel;
    private final SummaryTableModel tableModel;

    private JComboBox<ChartType> chartTypeComboBox;
    private JComboBox<String> algorithmComboBox;
    private JComboBox<String> inputTypeComboBox;
    private JComboBox<String> modeComboBox;
    private JComboBox<String> sizeComboBox;
    private JComboBox<String> threadsComboBox;

    public GraphFrame(Path csvPath, List<BenchmarkSummaryRecord> records) {
        super("Analise Grafica dos Benchmarks");
        this.records = records;
        this.filterState = new GraphFilterState();
        this.chartPanel = new ChartPanel(records, filterState);
        this.tableModel = new SummaryTableModel();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 800));
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new BorderLayout(12, 12));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        rootPanel.add(buildHeader(csvPath), BorderLayout.NORTH);
        rootPanel.add(buildCenter(), BorderLayout.CENTER);

        setContentPane(rootPanel);
        pack();

        updateUiState();
        refreshData();
    }

    private JPanel buildHeader(Path csvPath) {
        JPanel header = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Visualizador Dinamico dos Resultados do Benchmark", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(20f));

        JLabel source = new JLabel("Fonte: " + csvPath.toString(), SwingConstants.RIGHT);

        header.add(title, BorderLayout.WEST);
        header.add(source, BorderLayout.EAST);

        return header;
    }

    private JSplitPane buildCenter() {
        JPanel filterPanel = buildFilterPanel();

        JTable table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(700, 220));

        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chartPanel, tableScrollPane);
        verticalSplit.setResizeWeight(0.72);

        JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filterPanel, verticalSplit);
        horizontalSplit.setResizeWeight(0.2);

        return horizontalSplit;
    }

    private JPanel buildFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtros"));
        filterPanel.setPreferredSize(new Dimension(260, 600));

        chartTypeComboBox = new JComboBox<>(ChartType.values());
        algorithmComboBox = new JComboBox<>(buildStringOptions(records.stream()
                .map(BenchmarkSummaryRecord::getAlgorithm)
                .distinct()
                .sorted()
                .collect(Collectors.toList())));
        inputTypeComboBox = new JComboBox<>(buildStringOptions(records.stream()
                .map(BenchmarkSummaryRecord::getInputType)
                .distinct()
                .sorted()
                .collect(Collectors.toList())));
        modeComboBox = new JComboBox<>(buildStringOptions(records.stream()
                .map(BenchmarkSummaryRecord::getMode)
                .distinct()
                .sorted()
                .collect(Collectors.toList())));
        sizeComboBox = new JComboBox<>(buildIntegerOptions(records.stream()
                .map(BenchmarkSummaryRecord::getArraySize)
                .distinct()
                .sorted()
                .collect(Collectors.toList())));
        threadsComboBox = new JComboBox<>(buildIntegerOptions(records.stream()
                .map(BenchmarkSummaryRecord::getThreads)
                .distinct()
                .sorted()
                .collect(Collectors.toList())));

        algorithmComboBox.setSelectedItem(GraphFilterState.ALL);
        inputTypeComboBox.setSelectedItem("random");
        modeComboBox.setSelectedItem(GraphFilterState.ALL);
        sizeComboBox.setSelectedItem(GraphFilterState.ALL);
        threadsComboBox.setSelectedItem(GraphFilterState.ALL);

        addFilterControl(filterPanel, "Tipo de grafico", chartTypeComboBox);
        addFilterControl(filterPanel, "Algoritmo", algorithmComboBox);
        addFilterControl(filterPanel, "Tipo de entrada", inputTypeComboBox);
        addFilterControl(filterPanel, "Modo", modeComboBox);
        addFilterControl(filterPanel, "Tamanho do array", sizeComboBox);
        addFilterControl(filterPanel, "Threads", threadsComboBox);

        chartTypeComboBox.addActionListener(event -> {
            filterState.setChartType((ChartType) chartTypeComboBox.getSelectedItem());
            updateUiState();
            refreshData();
        });
        algorithmComboBox.addActionListener(event -> {
            filterState.setAlgorithm((String) algorithmComboBox.getSelectedItem());
            refreshData();
        });
        inputTypeComboBox.addActionListener(event -> {
            filterState.setInputType((String) inputTypeComboBox.getSelectedItem());
            refreshData();
        });
        modeComboBox.addActionListener(event -> {
            filterState.setMode((String) modeComboBox.getSelectedItem());
            refreshData();
        });
        sizeComboBox.addActionListener(event -> {
            filterState.setArraySize(parseIntegerSelection((String) sizeComboBox.getSelectedItem()));
            refreshData();
        });
        threadsComboBox.addActionListener(event -> {
            filterState.setThreads(parseIntegerSelection((String) threadsComboBox.getSelectedItem()));
            refreshData();
        });

        return filterPanel;
    }

    private void addFilterControl(JPanel parent, String labelText, JComboBox<?> comboBox) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        panel.add(new JLabel(labelText), BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.CENTER);
        parent.add(panel);
    }

    private void updateUiState() {
        ChartType chartType = filterState.getChartType();
        boolean speedup = chartType == ChartType.SPEEDUP;

        modeComboBox.setEnabled(!speedup);
        threadsComboBox.setEnabled(!speedup);

        if (speedup) {
            modeComboBox.setSelectedItem(GraphFilterState.ALL);
            threadsComboBox.setSelectedItem(GraphFilterState.ALL);
            filterState.setMode(GraphFilterState.ALL);
            filterState.setThreads(null);
        }
    }

    private void refreshData() {
        List<BenchmarkSummaryRecord> filtered = records.stream()
                .filter(filterState::matches)
                .sorted(Comparator
                        .comparing(BenchmarkSummaryRecord::getAlgorithm)
                        .thenComparing(BenchmarkSummaryRecord::getInputType)
                        .thenComparingInt(BenchmarkSummaryRecord::getArraySize)
                        .thenComparing(BenchmarkSummaryRecord::getMode)
                        .thenComparingInt(BenchmarkSummaryRecord::getThreads))
                .collect(Collectors.toList());

        tableModel.setRecords(filtered);
        chartPanel.setFilterState(filterState);
    }

    private String[] buildStringOptions(List<String> values) {
        String[] options = new String[values.size() + 1];
        options[0] = GraphFilterState.ALL;

        for (int index = 0; index < values.size(); index++) {
            options[index + 1] = values.get(index);
        }

        return options;
    }

    private String[] buildIntegerOptions(List<Integer> values) {
        String[] options = new String[values.size() + 1];
        options[0] = GraphFilterState.ALL;

        for (int index = 0; index < values.size(); index++) {
            options[index + 1] = String.valueOf(values.get(index));
        }

        return options;
    }

    private Integer parseIntegerSelection(String selection) {
        if (selection == null || GraphFilterState.ALL.equals(selection)) {
            return null;
        }

        return Integer.parseInt(selection);
    }
}
