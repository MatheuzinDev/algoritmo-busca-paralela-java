package graficos;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ChartPanel extends JPanel {

    private static final int LEFT_MARGIN = 80;
    private static final int RIGHT_MARGIN = 40;
    private static final int TOP_MARGIN = 55;
    private static final int BOTTOM_MARGIN = 90;

    private final List<BenchmarkSummaryRecord> allRecords;
    private GraphFilterState filterState;
    private List<BenchmarkSummaryRecord> tableRecords = new ArrayList<>();

    public ChartPanel(List<BenchmarkSummaryRecord> allRecords, GraphFilterState filterState) {
        this.allRecords = allRecords;
        this.filterState = filterState;
        setBackground(Color.WHITE);
    }

    public void setFilterState(GraphFilterState filterState) {
        this.filterState = filterState;
        repaint();
    }

    public List<BenchmarkSummaryRecord> getTableRecords() {
        return new ArrayList<>(tableRecords);
    }

    public List<BenchmarkSummaryRecord> getPreviewTableRecords() {
        return switch (filterState.getChartType()) {
            case SCALABILITY -> allRecords.stream()
                    .filter(record -> filterState.isAlgorithmInSelectedGroup(record.getAlgorithm()))
                    .filter(record -> record.getInputType().equals(filterState.getInputType()))
                    .filter(record -> record.getMode().equals(filterState.getMode()))
                    .filter(record -> "serial".equals(filterState.getMode()) || record.getThreads() == filterState.getThreads())
                    .collect(Collectors.toList());
            case SERIAL_VS_PARALLEL -> allRecords.stream()
                    .filter(record -> record.getAlgorithm().equals(filterState.getAlgorithm()))
                    .filter(record -> record.getInputType().equals(filterState.getInputType()))
                    .filter(record -> record.getArraySize() == filterState.getArraySize())
                    .collect(Collectors.toList());
            case INPUT_IMPACT -> allRecords.stream()
                    .filter(record -> record.getAlgorithm().equals(filterState.getAlgorithm()))
                    .filter(record -> record.getMode().equals(filterState.getMode()))
                    .filter(record -> record.getArraySize() == filterState.getArraySize())
                    .filter(record -> "serial".equals(filterState.getMode()) || record.getThreads() == filterState.getThreads())
                    .collect(Collectors.toList());
            case SPEEDUP -> allRecords.stream()
                    .filter(record -> filterState.isAlgorithmInSelectedGroup(record.getAlgorithm()))
                    .filter(record -> record.getInputType().equals(filterState.getInputType()))
                    .filter(record -> record.getArraySize() == filterState.getArraySize())
                    .collect(Collectors.toList());
        };
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        tableRecords = new ArrayList<>();

        drawTitle(graphics2D);

        if (filterState.getChartType() == ChartType.SCALABILITY) {
            drawScalabilityChart(graphics2D);
        } else if (filterState.getChartType() == ChartType.SERIAL_VS_PARALLEL) {
            drawSerialVsParallelChart(graphics2D);
        } else if (filterState.getChartType() == ChartType.INPUT_IMPACT) {
            drawInputImpactChart(graphics2D);
        } else {
            drawSpeedupChart(graphics2D);
        }

        graphics2D.dispose();
    }

    private void drawTitle(Graphics2D graphics2D) {
        graphics2D.setColor(new Color(30, 30, 30));
        graphics2D.setFont(getFont().deriveFont(Font.BOLD, 16f));
        graphics2D.drawString(buildTitle(), LEFT_MARGIN, 28);
    }

    private String buildTitle() {
        return switch (filterState.getChartType()) {
            case SCALABILITY -> "Escalabilidade dos algoritmos por tamanho da entrada";
            case SERIAL_VS_PARALLEL -> "Comparacao entre execucao serial e paralela";
            case INPUT_IMPACT -> "Impacto do tipo de entrada no desempenho";
            case SPEEDUP -> "Speedup da versao paralela em relacao a serial";
        };
    }

    private void drawScalabilityChart(Graphics2D graphics2D) {
        List<BenchmarkSummaryRecord> filteredRecords = allRecords.stream()
                .filter(record -> filterState.isAlgorithmInSelectedGroup(record.getAlgorithm()))
                .filter(record -> record.getInputType().equals(filterState.getInputType()))
                .filter(record -> record.getMode().equals(filterState.getMode()))
                .filter(record -> "serial".equals(filterState.getMode()) || record.getThreads() == filterState.getThreads())
                .sorted(Comparator.comparing(BenchmarkSummaryRecord::getAlgorithm).thenComparingInt(BenchmarkSummaryRecord::getArraySize))
                .collect(Collectors.toList());

        tableRecords = filteredRecords;

        if (filteredRecords.isEmpty()) {
            drawEmptyState(graphics2D, "Nenhum dado encontrado para o grafico de escalabilidade.");
            return;
        }

        Map<String, List<BenchmarkSummaryRecord>> seriesMap = filteredRecords.stream()
                .collect(Collectors.groupingBy(BenchmarkSummaryRecord::getAlgorithm, LinkedHashMap::new, Collectors.toList()));

        List<Integer> xValues = filteredRecords.stream()
                .map(BenchmarkSummaryRecord::getArraySize)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        drawLineChart(graphics2D, seriesMap, xValues, "Tempo medio (ms)");
    }

    private void drawSerialVsParallelChart(Graphics2D graphics2D) {
        List<BenchmarkSummaryRecord> filteredRecords = allRecords.stream()
                .filter(record -> record.getAlgorithm().equals(filterState.getAlgorithm()))
                .filter(record -> record.getInputType().equals(filterState.getInputType()))
                .filter(record -> record.getArraySize() == filterState.getArraySize())
                .sorted(Comparator.comparing(BenchmarkSummaryRecord::getMode).thenComparingInt(BenchmarkSummaryRecord::getThreads))
                .collect(Collectors.toList());

        tableRecords = filteredRecords;

        if (filteredRecords.isEmpty()) {
            drawEmptyState(graphics2D, "Nao ha dados para comparar serial e paralelo nesse cenario.");
            return;
        }

        List<BarValue> bars = new ArrayList<>();

        for (BenchmarkSummaryRecord record : filteredRecords) {
            String label = "serial".equals(record.getMode()) ? "serial" : record.getThreads() + " threads";
            bars.add(new BarValue(label, record.getAverageTimeMs(), record.getAlgorithm()));
        }

        drawBarChart(graphics2D, bars, "Tempo medio (ms)");
    }

    private void drawInputImpactChart(Graphics2D graphics2D) {
        List<BenchmarkSummaryRecord> filteredRecords = allRecords.stream()
                .filter(record -> record.getAlgorithm().equals(filterState.getAlgorithm()))
                .filter(record -> record.getMode().equals(filterState.getMode()))
                .filter(record -> record.getArraySize() == filterState.getArraySize())
                .filter(record -> "serial".equals(filterState.getMode()) || record.getThreads() == filterState.getThreads())
                .sorted(Comparator.comparing(BenchmarkSummaryRecord::getInputType))
                .collect(Collectors.toList());

        tableRecords = filteredRecords;

        if (filteredRecords.isEmpty()) {
            drawEmptyState(graphics2D, "Nao ha dados para comparar os tipos de entrada nesse cenario.");
            return;
        }

        List<BarValue> bars = filteredRecords.stream()
                .map(record -> new BarValue(record.getInputType(), record.getAverageTimeMs(), record.getInputType()))
                .collect(Collectors.toList());

        drawBarChart(graphics2D, bars, "Tempo medio (ms)");
    }

    private void drawSpeedupChart(Graphics2D graphics2D) {
        List<BenchmarkSummaryRecord> scopedRecords = allRecords.stream()
                .filter(record -> filterState.isAlgorithmInSelectedGroup(record.getAlgorithm()))
                .filter(record -> record.getInputType().equals(filterState.getInputType()))
                .filter(record -> record.getArraySize() == filterState.getArraySize())
                .collect(Collectors.toList());

        tableRecords = scopedRecords.stream()
                .filter(record -> "parallel".equals(record.getMode()) || "serial".equals(record.getMode()))
                .sorted(Comparator.comparing(BenchmarkSummaryRecord::getAlgorithm).thenComparingInt(BenchmarkSummaryRecord::getThreads))
                .collect(Collectors.toList());

        Map<String, BenchmarkSummaryRecord> serialByAlgorithm = scopedRecords.stream()
                .filter(record -> "serial".equals(record.getMode()))
                .collect(Collectors.toMap(BenchmarkSummaryRecord::getAlgorithm, record -> record, (left, right) -> left, LinkedHashMap::new));

        Map<String, List<SpeedupPoint>> seriesMap = new LinkedHashMap<>();

        for (String algorithm : serialByAlgorithm.keySet()) {
            BenchmarkSummaryRecord serialRecord = serialByAlgorithm.get(algorithm);
            List<SpeedupPoint> points = scopedRecords.stream()
                    .filter(record -> algorithm.equals(record.getAlgorithm()))
                    .filter(record -> "parallel".equals(record.getMode()))
                    .sorted(Comparator.comparingInt(BenchmarkSummaryRecord::getThreads))
                    .map(record -> new SpeedupPoint(record.getThreads(), serialRecord.getAverageTimeMs() / record.getAverageTimeMs()))
                    .collect(Collectors.toList());

            if (!points.isEmpty()) {
                seriesMap.put(algorithm, points);
            }
        }

        if (seriesMap.isEmpty()) {
            drawEmptyState(graphics2D, "Nao ha dados suficientes para calcular speedup nesse cenario.");
            return;
        }

        List<Integer> xValues = seriesMap.values().stream()
                .flatMap(List::stream)
                .map(SpeedupPoint::threads)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        double maxValue = seriesMap.values().stream()
                .flatMap(List::stream)
                .mapToDouble(SpeedupPoint::speedup)
                .max()
                .orElse(1.0);

        ChartBounds bounds = buildChartBounds();
        drawAxes(graphics2D, bounds, Math.max(1.0, maxValue), "Speedup", "Threads");
        drawLineXAxisLabels(graphics2D, bounds, xValues);

        Stroke previousStroke = graphics2D.getStroke();
        graphics2D.setStroke(new BasicStroke(2.2f));

        for (Map.Entry<String, List<SpeedupPoint>> entry : seriesMap.entrySet()) {
            graphics2D.setColor(colorFor(entry.getKey()));

            for (int index = 0; index < entry.getValue().size() - 1; index++) {
                SpeedupPoint current = entry.getValue().get(index);
                SpeedupPoint next = entry.getValue().get(index + 1);
                graphics2D.draw(new Line2D.Double(
                        mapX(current.threads(), xValues, bounds),
                        mapY(current.speedup(), Math.max(1.0, maxValue), bounds),
                        mapX(next.threads(), xValues, bounds),
                        mapY(next.speedup(), Math.max(1.0, maxValue), bounds)
                ));
            }

            for (SpeedupPoint point : entry.getValue()) {
                double x = mapX(point.threads(), xValues, bounds);
                double y = mapY(point.speedup(), Math.max(1.0, maxValue), bounds);
                graphics2D.fillOval((int) x - 4, (int) y - 4, 8, 8);
            }
        }

        graphics2D.setStroke(previousStroke);
        drawLegend(graphics2D, new ArrayList<>(seriesMap.keySet()));
    }

    private void drawLineChart(Graphics2D graphics2D, Map<String, List<BenchmarkSummaryRecord>> seriesMap, List<Integer> xValues, String yLabel) {
        if (xValues.size() < 2) {
            drawEmptyState(graphics2D, "Esse grafico precisa de mais de um tamanho de entrada.");
            return;
        }

        double maxValue = seriesMap.values().stream()
                .flatMap(List::stream)
                .mapToDouble(BenchmarkSummaryRecord::getAverageTimeMs)
                .max()
                .orElse(1.0);

        ChartBounds bounds = buildChartBounds();
        drawAxes(graphics2D, bounds, maxValue, yLabel, "Tamanho do array");
        drawLineXAxisLabels(graphics2D, bounds, xValues);

        Stroke previousStroke = graphics2D.getStroke();
        graphics2D.setStroke(new BasicStroke(2.2f));

        for (Map.Entry<String, List<BenchmarkSummaryRecord>> entry : seriesMap.entrySet()) {
            List<BenchmarkSummaryRecord> series = entry.getValue();
            graphics2D.setColor(colorFor(entry.getKey()));

            for (int index = 0; index < series.size() - 1; index++) {
                BenchmarkSummaryRecord current = series.get(index);
                BenchmarkSummaryRecord next = series.get(index + 1);
                graphics2D.draw(new Line2D.Double(
                        mapX(current.getArraySize(), xValues, bounds),
                        mapY(current.getAverageTimeMs(), maxValue, bounds),
                        mapX(next.getArraySize(), xValues, bounds),
                        mapY(next.getAverageTimeMs(), maxValue, bounds)
                ));
            }

            for (BenchmarkSummaryRecord record : series) {
                double x = mapX(record.getArraySize(), xValues, bounds);
                double y = mapY(record.getAverageTimeMs(), maxValue, bounds);
                graphics2D.fillOval((int) x - 4, (int) y - 4, 8, 8);
            }
        }

        graphics2D.setStroke(previousStroke);
        drawLegend(graphics2D, new ArrayList<>(seriesMap.keySet()));
    }

    private void drawBarChart(Graphics2D graphics2D, List<BarValue> bars, String yLabel) {
        double maxValue = bars.stream().mapToDouble(BarValue::value).max().orElse(1.0);
        ChartBounds bounds = buildChartBounds();
        drawAxes(graphics2D, bounds, maxValue, yLabel, "Cenario");

        double slotWidth = (double) bounds.width / bars.size();
        double barWidth = Math.max(26, slotWidth * 0.58);

        for (int index = 0; index < bars.size(); index++) {
            BarValue bar = bars.get(index);
            double height = (bar.value() / maxValue) * bounds.height;
            double x = bounds.x + slotWidth * index + (slotWidth - barWidth) / 2.0;
            double y = bounds.y + bounds.height - height;

            graphics2D.setColor(colorFor(bar.colorKey()));
            graphics2D.fill(new Rectangle2D.Double(x, y, barWidth, height));
            graphics2D.setColor(Color.DARK_GRAY);
            graphics2D.draw(new Rectangle2D.Double(x, y, barWidth, height));
            graphics2D.setFont(getFont().deriveFont(11f));

            String label = bar.label();
            int labelWidth = graphics2D.getFontMetrics().stringWidth(label);
            graphics2D.drawString(label, (int) (x + barWidth / 2 - labelWidth / 2.0), bounds.y + bounds.height + 24);
            graphics2D.drawString(formatValue(bar.value()), (int) (x + 2), (int) (y - 6));
        }
    }

    private void drawAxes(Graphics2D graphics2D, ChartBounds bounds, double maxValue, String yLabel, String xLabel) {
        graphics2D.setColor(new Color(90, 90, 90));
        graphics2D.drawLine(bounds.x, bounds.y, bounds.x, bounds.y + bounds.height);
        graphics2D.drawLine(bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y + bounds.height);

        graphics2D.setFont(getFont().deriveFont(11f));

        for (int index = 0; index <= 5; index++) {
            double value = maxValue * index / 5.0;
            double y = bounds.y + bounds.height - (bounds.height * index / 5.0);
            graphics2D.setColor(new Color(235, 235, 235));
            graphics2D.draw(new Line2D.Double(bounds.x, y, bounds.x + bounds.width, y));
            graphics2D.setColor(new Color(80, 80, 80));
            graphics2D.drawString(formatValue(value), 20, (int) y + 4);
        }

        graphics2D.setColor(new Color(60, 60, 60));
        graphics2D.drawString(yLabel, 22, TOP_MARGIN - 14);
        int xLabelWidth = graphics2D.getFontMetrics().stringWidth(xLabel);
        graphics2D.drawString(xLabel, bounds.x + bounds.width / 2 - xLabelWidth / 2, getHeight() - 18);
    }

    private void drawLineXAxisLabels(Graphics2D graphics2D, ChartBounds bounds, List<Integer> xValues) {
        graphics2D.setColor(new Color(60, 60, 60));
        graphics2D.setFont(getFont().deriveFont(11f));

        for (Integer xValue : xValues) {
            double x = mapX(xValue, xValues, bounds);
            String label = String.valueOf(xValue);
            int labelWidth = graphics2D.getFontMetrics().stringWidth(label);
            graphics2D.drawString(label, (int) x - labelWidth / 2, bounds.y + bounds.height + 22);
        }
    }

    private void drawLegend(Graphics2D graphics2D, List<String> labels) {
        int x = getWidth() - 240;
        int y = 24;
        graphics2D.setFont(getFont().deriveFont(11f));

        for (String label : labels) {
            graphics2D.setColor(colorFor(label));
            graphics2D.fillRect(x, y - 10, 12, 12);
            graphics2D.setColor(new Color(40, 40, 40));
            graphics2D.drawString(label, x + 18, y);
            y += 18;
        }
    }

    private void drawEmptyState(Graphics2D graphics2D, String message) {
        graphics2D.setColor(new Color(110, 110, 110));
        graphics2D.setFont(getFont().deriveFont(Font.PLAIN, 14f));
        int textWidth = graphics2D.getFontMetrics().stringWidth(message);
        graphics2D.drawString(message, (getWidth() - textWidth) / 2, getHeight() / 2);
    }

    private String formatValue(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private double mapX(int value, List<Integer> xValues, ChartBounds bounds) {
        if (xValues.size() == 1) {
            return bounds.x + bounds.width / 2.0;
        }

        int position = xValues.indexOf(value);
        double ratio = position / (double) (xValues.size() - 1);
        return bounds.x + ratio * bounds.width;
    }

    private double mapY(double value, double maxValue, ChartBounds bounds) {
        return bounds.y + bounds.height - (value / maxValue) * bounds.height;
    }

    private ChartBounds buildChartBounds() {
        return new ChartBounds(
                LEFT_MARGIN,
                TOP_MARGIN,
                Math.max(100, getWidth() - LEFT_MARGIN - RIGHT_MARGIN),
                Math.max(100, getHeight() - TOP_MARGIN - BOTTOM_MARGIN)
        );
    }

    private Color colorFor(String key) {
        Color[] palette = new Color[]{
                new Color(52, 152, 219),
                new Color(46, 204, 113),
                new Color(231, 76, 60),
                new Color(155, 89, 182),
                new Color(241, 196, 15),
                new Color(26, 188, 156),
                new Color(230, 126, 34),
                new Color(127, 140, 141)
        };

        return palette[Math.abs(key.hashCode()) % palette.length];
    }

    private record ChartBounds(int x, int y, int width, int height) {
    }

    private record SpeedupPoint(int threads, double speedup) {
    }

    private record BarValue(String label, double value, String colorKey) {
    }
}
