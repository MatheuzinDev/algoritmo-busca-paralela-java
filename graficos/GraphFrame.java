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
    private JComboBox<String> groupComboBox;
    private JComboBox<String> algorithmComboBox;
    private JComboBox<String> inputTypeComboBox;
    private JComboBox<String> modeComboBox;
    private JComboBox<String> sizeComboBox;
    private JComboBox<String> threadsComboBox;

    private JPanel groupFilterPanel;
    private JPanel algorithmFilterPanel;
    private JPanel inputFilterPanel;
    private JPanel modeFilterPanel;
    private JPanel sizeFilterPanel;
    private JPanel threadsFilterPanel;

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
        applyChartDefaults(filterState.getChartType());
        pack();
        refreshUiState();
        refreshData();
    }

    private JPanel buildHeader(Path csvPath) {
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Painel de Analise dos Resultados", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(20f));
        JLabel source = new JLabel("Fonte: " + csvPath, SwingConstants.RIGHT);
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
        verticalSplit.setResizeWeight(0.74);

        JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, filterPanel, verticalSplit);
        horizontalSplit.setResizeWeight(0.22);
        return horizontalSplit;
    }

    private JPanel buildFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Visualizacao"));
        filterPanel.setPreferredSize(new Dimension(290, 600));

        chartTypeComboBox = new JComboBox<>(ChartType.values());
        groupComboBox = new JComboBox<>(new String[]{GraphFilterState.FAST_GROUP, GraphFilterState.QUADRATIC_GROUP});
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
        modeComboBox = new JComboBox<>(new String[]{"serial", "parallel"});
        sizeComboBox = new JComboBox<>(buildIntegerOptions(records.stream()
                .map(BenchmarkSummaryRecord::getArraySize)
                .distinct()
                .sorted()
                .collect(Collectors.toList())));
        threadsComboBox = new JComboBox<>(buildIntegerOptions(records.stream()
                .map(BenchmarkSummaryRecord::getThreads)
                .filter(threadCount -> threadCount > 1)
                .distinct()
                .sorted()
                .collect(Collectors.toList())));

        addStaticInfo(filterPanel);
        addFilterControl(filterPanel, "Grafico", chartTypeComboBox, true);
        groupFilterPanel = addFilterControl(filterPanel, "Grupo de algoritmos", groupComboBox, false);
        algorithmFilterPanel = addFilterControl(filterPanel, "Algoritmo", algorithmComboBox, false);
        inputFilterPanel = addFilterControl(filterPanel, "Tipo de entrada", inputTypeComboBox, false);
        modeFilterPanel = addFilterControl(filterPanel, "Modo", modeComboBox, false);
        sizeFilterPanel = addFilterControl(filterPanel, "Tamanho do array", sizeComboBox, false);
        threadsFilterPanel = addFilterControl(filterPanel, "Threads", threadsComboBox, false);

        chartTypeComboBox.addActionListener(event -> {
            ChartType selectedType = (ChartType) chartTypeComboBox.getSelectedItem();
            filterState.setChartType(selectedType);
            applyChartDefaults(selectedType);
            refreshUiState();
            refreshData();
        });
        groupComboBox.addActionListener(event -> {
            filterState.setAlgorithmGroup((String) groupComboBox.getSelectedItem());
            adjustSizeAfterGroupChange();
            refreshData();
        });
        algorithmComboBox.addActionListener(event -> {
            filterState.setAlgorithm((String) algorithmComboBox.getSelectedItem());
            adjustSizeForAlgorithm();
            refreshData();
        });
        inputTypeComboBox.addActionListener(event -> {
            filterState.setInputType((String) inputTypeComboBox.getSelectedItem());
            refreshData();
        });
        modeComboBox.addActionListener(event -> {
            filterState.setMode((String) modeComboBox.getSelectedItem());
            refreshUiState();
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

    private void addStaticInfo(JPanel filterPanel) {
        JLabel info = new JLabel("Escolha um dos 4 graficos principais do trabalho.");
        info.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        filterPanel.add(info);
    }

    private JPanel addFilterControl(JPanel parent, String labelText, JComboBox<?> comboBox, boolean alwaysVisible) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        panel.add(new JLabel(labelText), BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.CENTER);
        panel.setVisible(alwaysVisible);
        parent.add(panel);
        return panel;
    }

    private void applyChartDefaults(ChartType chartType) {
        if (chartType == ChartType.SCALABILITY) {
            groupComboBox.setSelectedItem(GraphFilterState.FAST_GROUP);
            inputTypeComboBox.setSelectedItem("random");
            modeComboBox.setSelectedItem("serial");
            threadsComboBox.setSelectedItem("4");
            filterState.setAlgorithmGroup(GraphFilterState.FAST_GROUP);
            filterState.setInputType("random");
            filterState.setMode("serial");
            filterState.setThreads(4);
            filterState.setArraySize(null);
        } else if (chartType == ChartType.SERIAL_VS_PARALLEL) {
            algorithmComboBox.setSelectedItem("Quick Sort");
            inputTypeComboBox.setSelectedItem("random");
            filterState.setAlgorithm("Quick Sort");
            filterState.setInputType("random");
            selectSizeForAlgorithm("Quick Sort", false, true);
        } else if (chartType == ChartType.INPUT_IMPACT) {
            algorithmComboBox.setSelectedItem("Quick Sort");
            modeComboBox.setSelectedItem("serial");
            threadsComboBox.setSelectedItem("4");
            filterState.setAlgorithm("Quick Sort");
            filterState.setMode("serial");
            filterState.setThreads(4);
            selectSizeForAlgorithm("Quick Sort", false, false);
        } else if (chartType == ChartType.SPEEDUP) {
            groupComboBox.setSelectedItem(GraphFilterState.FAST_GROUP);
            inputTypeComboBox.setSelectedItem("random");
            filterState.setAlgorithmGroup(GraphFilterState.FAST_GROUP);
            filterState.setInputType("random");
            selectSizeForGroup(GraphFilterState.FAST_GROUP, true);
        }
    }

    private void refreshUiState() {
        ChartType chartType = filterState.getChartType();

        groupFilterPanel.setVisible(chartType == ChartType.SCALABILITY || chartType == ChartType.SPEEDUP);
        algorithmFilterPanel.setVisible(chartType == ChartType.SERIAL_VS_PARALLEL || chartType == ChartType.INPUT_IMPACT);
        inputFilterPanel.setVisible(true);
        modeFilterPanel.setVisible(chartType == ChartType.SCALABILITY || chartType == ChartType.INPUT_IMPACT);
        sizeFilterPanel.setVisible(chartType != ChartType.SCALABILITY);
        threadsFilterPanel.setVisible((chartType == ChartType.SCALABILITY && "parallel".equals(filterState.getMode()))
                || (chartType == ChartType.INPUT_IMPACT && "parallel".equals(filterState.getMode())));

        filterPanelRevalidate();
    }

    private void filterPanelRevalidate() {
        groupFilterPanel.getParent().revalidate();
        groupFilterPanel.getParent().repaint();
    }

    private void adjustSizeForAlgorithm() {
        ChartType chartType = filterState.getChartType();

        if (chartType == ChartType.SERIAL_VS_PARALLEL) {
            selectSizeForAlgorithm(filterState.getAlgorithm(), false, true);
        } else if (chartType == ChartType.INPUT_IMPACT) {
            selectSizeForAlgorithm(filterState.getAlgorithm(), false, false);
        }
    }

    private void adjustSizeAfterGroupChange() {
        if (filterState.getChartType() == ChartType.SPEEDUP) {
            selectSizeForGroup(filterState.getAlgorithmGroup(), true);
        }
    }

    private void selectSizeForAlgorithm(String algorithm, boolean updateComboBox, boolean largest) {
        List<Integer> sizes = records.stream()
                .filter(record -> algorithm.equals(record.getAlgorithm()))
                .map(BenchmarkSummaryRecord::getArraySize)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (sizes.isEmpty()) {
            return;
        }

        int selectedSize = largest ? sizes.get(sizes.size() - 1) : sizes.get(sizes.size() / 2);
        filterState.setArraySize(selectedSize);

        if (!updateComboBox || !String.valueOf(selectedSize).equals(sizeComboBox.getSelectedItem())) {
            sizeComboBox.setSelectedItem(String.valueOf(selectedSize));
        }
    }

    private void selectSizeForGroup(String group, boolean largest) {
        List<Integer> sizes = records.stream()
                .filter(record -> group.equals(GraphFilterState.FAST_GROUP)
                        ? ("Quick Sort".equals(record.getAlgorithm()) || "Merge Sort".equals(record.getAlgorithm()))
                        : ("Insertion Sort".equals(record.getAlgorithm()) || "Selection Sort".equals(record.getAlgorithm())))
                .map(BenchmarkSummaryRecord::getArraySize)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (sizes.isEmpty()) {
            return;
        }

        int selectedSize = largest ? sizes.get(sizes.size() - 1) : sizes.get(sizes.size() / 2);
        filterState.setArraySize(selectedSize);
        sizeComboBox.setSelectedItem(String.valueOf(selectedSize));
    }

    private void refreshData() {
        chartPanel.setFilterState(filterState);

        List<BenchmarkSummaryRecord> filtered = chartPanel.getPreviewTableRecords().stream()
                .sorted(Comparator
                        .comparing(BenchmarkSummaryRecord::getAlgorithm)
                        .thenComparing(BenchmarkSummaryRecord::getInputType)
                        .thenComparingInt(BenchmarkSummaryRecord::getArraySize)
                        .thenComparing(BenchmarkSummaryRecord::getMode)
                        .thenComparingInt(BenchmarkSummaryRecord::getThreads))
                .collect(Collectors.toList());

        tableModel.setRecords(filtered);
    }

    private String[] buildStringOptions(List<String> values) {
        return values.toArray(new String[0]);
    }

    private String[] buildIntegerOptions(List<Integer> values) {
        return values.stream().map(String::valueOf).toArray(String[]::new);
    }

    private Integer parseIntegerSelection(String selection) {
        if (selection == null || selection.isBlank()) {
            return null;
        }

        return Integer.parseInt(selection);
    }
}
