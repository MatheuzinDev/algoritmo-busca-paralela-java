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
import java.util.Map;
import java.util.stream.Collectors;

public class ChartPanel extends JPanel {

    private static final int LEFT_MARGIN = 80;
    private static final int RIGHT_MARGIN = 40;
    private static final int TOP_MARGIN = 50;
    private static final int BOTTOM_MARGIN = 90;

    private final List<BenchmarkSummaryRecord> allRecords;
    private GraphFilterState filterState;

    public ChartPanel(List<BenchmarkSummaryRecord> allRecords, GraphFilterState filterState) {
        this.allRecords = allRecords;
        this.filterState = filterState;
        setBackground(Color.WHITE);
    }

    public void setFilterState(GraphFilterState filterState) {
        this.filterState = filterState;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<BenchmarkSummaryRecord> filteredRecords = allRecords.stream()
                .filter(filterState::matches)
                .collect(Collectors.toList());

        drawTitle(graphics2D);

        if (filterState.getChartType() == ChartType.SPEEDUP) {
            drawSpeedupChart(graphics2D, filteredRecords);
        } else if (filterState.getChartType() == ChartType.LINE) {
            drawLineChart(graphics2D, filteredRecords);
        } else {
            drawBarChart(graphics2D, filteredRecords);
        }

        graphics2D.dispose();
    }

    private void drawTitle(Graphics2D graphics2D) {
        graphics2D.setColor(new Color(30, 30, 30));
        graphics2D.setFont(getFont().deriveFont(Font.BOLD, 16f));
        graphics2D.drawString(buildTitle(), LEFT_MARGIN, 28);
    }

    private String buildTitle() {
        if (filterState.getChartType() == ChartType.BAR) {
            return "Comparacao por barras";
        }

        if (filterState.getChartType() == ChartType.LINE) {
            return "Escalabilidade por tamanho de entrada";
        }

        return "Speedup em relacao a execucao serial";
    }

    private void drawBarChart(Graphics2D graphics2D, List<BenchmarkSummaryRecord> filteredRecords) {
        if (filteredRecords.isEmpty()) {
            drawEmptyState(graphics2D, "Nenhum dado encontrado para os filtros atuais.");
            return;
        }

        if (filteredRecords.size() > 24) {
            drawEmptyState(graphics2D, "Refine os filtros para reduzir a quantidade de barras.");
            return;
        }

        filteredRecords.sort(Comparator
                .comparing(BenchmarkSummaryRecord::getAlgorithm)
                .thenComparing(BenchmarkSummaryRecord::getArraySize)
                .thenComparing(BenchmarkSummaryRecord::getMode)
                .thenComparingInt(BenchmarkSummaryRecord::getThreads));

        double maxValue = filteredRecords.stream()
                .mapToDouble(BenchmarkSummaryRecord::getAverageTimeMs)
                .max()
                .orElse(1.0);

        ChartBounds bounds = buildChartBounds();
        drawAxes(graphics2D, bounds, maxValue, "Tempo medio (ms)");

        int barCount = filteredRecords.size();
        double slotWidth = (double) bounds.width / barCount;
        double barWidth = Math.max(14, slotWidth * 0.65);

        for (int index = 0; index < filteredRecords.size(); index++) {
            BenchmarkSummaryRecord record = filteredRecords.get(index);
            double value = record.getAverageTimeMs();
            double normalizedHeight = value / maxValue;
            double height = normalizedHeight * bounds.height;
            double x = bounds.x + slotWidth * index + (slotWidth - barWidth) / 2.0;
            double y = bounds.y + bounds.height - height;

            graphics2D.setColor(colorFor(record.getAlgorithm() + record.getMode() + record.getThreads()));
            graphics2D.fill(new Rectangle2D.Double(x, y, barWidth, height));
            graphics2D.setColor(Color.DARK_GRAY);
            graphics2D.draw(new Rectangle2D.Double(x, y, barWidth, height));

            drawVerticalLabel(graphics2D, buildBarLabel(record), x + barWidth / 2.0, bounds.y + bounds.height + 10);
        }
    }

    private void drawLineChart(Graphics2D graphics2D, List<BenchmarkSummaryRecord> filteredRecords) {
        if (filteredRecords.isEmpty()) {
            drawEmptyState(graphics2D, "Nenhum dado encontrado para os filtros atuais.");
            return;
        }

        Map<String, List<BenchmarkSummaryRecord>> seriesMap = buildLineSeries(filteredRecords);

        if (seriesMap.isEmpty()) {
            drawEmptyState(graphics2D, "Nao foi possivel montar series para o grafico de linhas.");
            return;
        }

        double maxValue = filteredRecords.stream()
                .mapToDouble(BenchmarkSummaryRecord::getAverageTimeMs)
                .max()
                .orElse(1.0);

        List<Integer> xValues = filteredRecords.stream()
                .map(BenchmarkSummaryRecord::getArraySize)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (xValues.size() < 2) {
            drawEmptyState(graphics2D, "Selecione mais de um tamanho de entrada para o grafico de linhas.");
            return;
        }

        ChartBounds bounds = buildChartBounds();
        drawAxes(graphics2D, bounds, maxValue, "Tempo medio (ms)");
        drawLineXAxisLabels(graphics2D, bounds, xValues);

        Stroke previousStroke = graphics2D.getStroke();
        graphics2D.setStroke(new BasicStroke(2.2f));

        for (Map.Entry<String, List<BenchmarkSummaryRecord>> entry : seriesMap.entrySet()) {
            List<BenchmarkSummaryRecord> series = entry.getValue();
            Color color = colorFor(entry.getKey());
            graphics2D.setColor(color);

            for (int index = 0; index < series.size() - 1; index++) {
                BenchmarkSummaryRecord current = series.get(index);
                BenchmarkSummaryRecord next = series.get(index + 1);
                double x1 = mapX(current.getArraySize(), xValues, bounds);
                double y1 = mapY(current.getAverageTimeMs(), maxValue, bounds);
                double x2 = mapX(next.getArraySize(), xValues, bounds);
                double y2 = mapY(next.getAverageTimeMs(), maxValue, bounds);
                graphics2D.draw(new Line2D.Double(x1, y1, x2, y2));
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

    private void drawSpeedupChart(Graphics2D graphics2D, List<BenchmarkSummaryRecord> filteredRecords) {
        if (filterState.getArraySize() == null) {
            drawEmptyState(graphics2D, "Selecione um tamanho de entrada para calcular o speedup.");
            return;
        }

        Map<String, List<SpeedupPoint>> speedupSeries = buildSpeedupSeries(filteredRecords);

        if (speedupSeries.isEmpty()) {
            drawEmptyState(graphics2D, "Nao ha dados suficientes para calcular speedup com os filtros atuais.");
            return;
        }

        double maxValue = speedupSeries.values().stream()
                .flatMap(List::stream)
                .mapToDouble(SpeedupPoint::speedup)
                .max()
                .orElse(1.0);

        List<Integer> threadValues = speedupSeries.values().stream()
                .flatMap(List::stream)
                .map(SpeedupPoint::threads)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        ChartBounds bounds = buildChartBounds();
        drawAxes(graphics2D, bounds, Math.max(1.0, maxValue), "Speedup");
        drawLineXAxisLabels(graphics2D, bounds, threadValues);

        Stroke previousStroke = graphics2D.getStroke();
        graphics2D.setStroke(new BasicStroke(2.2f));

        for (Map.Entry<String, List<SpeedupPoint>> entry : speedupSeries.entrySet()) {
            List<SpeedupPoint> series = entry.getValue();
            Color color = colorFor(entry.getKey());
            graphics2D.setColor(color);

            for (int index = 0; index < series.size() - 1; index++) {
                SpeedupPoint current = series.get(index);
                SpeedupPoint next = series.get(index + 1);
                double x1 = mapX(current.threads(), threadValues, bounds);
                double y1 = mapY(current.speedup(), Math.max(1.0, maxValue), bounds);
                double x2 = mapX(next.threads(), threadValues, bounds);
                double y2 = mapY(next.speedup(), Math.max(1.0, maxValue), bounds);
                graphics2D.draw(new Line2D.Double(x1, y1, x2, y2));
            }

            for (SpeedupPoint point : series) {
                double x = mapX(point.threads(), threadValues, bounds);
                double y = mapY(point.speedup(), Math.max(1.0, maxValue), bounds);
                graphics2D.fillOval((int) x - 4, (int) y - 4, 8, 8);
            }
        }

        graphics2D.setStroke(previousStroke);
        drawLegend(graphics2D, new ArrayList<>(speedupSeries.keySet()));
    }

    private Map<String, List<BenchmarkSummaryRecord>> buildLineSeries(List<BenchmarkSummaryRecord> filteredRecords) {
        Map<String, List<BenchmarkSummaryRecord>> seriesMap = new LinkedHashMap<>();

        for (BenchmarkSummaryRecord record : filteredRecords) {
            String seriesKey = buildLineSeriesKey(record);
            seriesMap.computeIfAbsent(seriesKey, key -> new ArrayList<>()).add(record);
        }

        for (List<BenchmarkSummaryRecord> series : seriesMap.values()) {
            series.sort(Comparator.comparingInt(BenchmarkSummaryRecord::getArraySize));
        }

        return seriesMap;
    }

    private Map<String, List<SpeedupPoint>> buildSpeedupSeries(List<BenchmarkSummaryRecord> filteredRecords) {
        List<BenchmarkSummaryRecord> scopedRecords = filteredRecords.stream()
                .filter(record -> filterState.getArraySize() == record.getArraySize())
                .filter(record -> GraphFilterState.ALL.equals(filterState.getAlgorithm()) || filterState.getAlgorithm().equals(record.getAlgorithm()))
                .filter(record -> GraphFilterState.ALL.equals(filterState.getInputType()) || filterState.getInputType().equals(record.getInputType()))
                .collect(Collectors.toList());

        Map<String, BenchmarkSummaryRecord> serialByKey = new LinkedHashMap<>();
        Map<String, List<BenchmarkSummaryRecord>> parallelByKey = new LinkedHashMap<>();

        for (BenchmarkSummaryRecord record : scopedRecords) {
            String key = record.getAlgorithm() + "|" + record.getInputType() + "|" + record.getArraySize();

            if ("serial".equals(record.getMode())) {
                serialByKey.put(key, record);
            } else if ("parallel".equals(record.getMode())) {
                parallelByKey.computeIfAbsent(key, ignored -> new ArrayList<>()).add(record);
            }
        }

        Map<String, List<SpeedupPoint>> seriesMap = new LinkedHashMap<>();

        for (Map.Entry<String, List<BenchmarkSummaryRecord>> entry : parallelByKey.entrySet()) {
            BenchmarkSummaryRecord serialRecord = serialByKey.get(entry.getKey());

            if (serialRecord == null) {
                continue;
            }

            String seriesLabel = buildSpeedupSeriesLabel(serialRecord);
            List<SpeedupPoint> points = new ArrayList<>();

            for (BenchmarkSummaryRecord parallelRecord : entry.getValue()) {
                points.add(new SpeedupPoint(
                        parallelRecord.getThreads(),
                        serialRecord.getAverageTimeMs() / parallelRecord.getAverageTimeMs()
                ));
            }

            points.sort(Comparator.comparingInt(SpeedupPoint::threads));
            seriesMap.put(seriesLabel, points);
        }

        return seriesMap;
    }

    private void drawAxes(Graphics2D graphics2D, ChartBounds bounds, double maxValue, String yLabel) {
        graphics2D.setColor(new Color(90, 90, 90));
        graphics2D.drawLine(bounds.x, bounds.y, bounds.x, bounds.y + bounds.height);
        graphics2D.drawLine(bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y + bounds.height);

        graphics2D.setFont(getFont().deriveFont(11f));
        int divisions = 5;

        for (int index = 0; index <= divisions; index++) {
            double value = maxValue * index / divisions;
            double y = bounds.y + bounds.height - (bounds.height * index / (double) divisions);

            graphics2D.setColor(new Color(235, 235, 235));
            graphics2D.draw(new Line2D.Double(bounds.x, y, bounds.x + bounds.width, y));
            graphics2D.setColor(new Color(80, 80, 80));
            graphics2D.drawString(String.format("%.2f", value), 20, (int) y + 4);
        }

        graphics2D.setColor(new Color(60, 60, 60));
        graphics2D.drawString(yLabel, 22, TOP_MARGIN - 12);
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
        int x = getWidth() - 220;
        int y = 22;

        graphics2D.setFont(getFont().deriveFont(11f));

        for (String label : labels) {
            graphics2D.setColor(colorFor(label));
            graphics2D.fillRect(x, y - 10, 12, 12);
            graphics2D.setColor(new Color(40, 40, 40));
            graphics2D.drawString(label, x + 18, y);
            y += 18;
        }
    }

    private void drawVerticalLabel(Graphics2D graphics2D, String label, double centerX, int baselineY) {
        Graphics2D rotated = (Graphics2D) graphics2D.create();
        rotated.setFont(getFont().deriveFont(10f));
        rotated.setColor(new Color(60, 60, 60));
        rotated.rotate(-Math.PI / 4, centerX, baselineY);
        rotated.drawString(label, (int) centerX - 10, baselineY + 4);
        rotated.dispose();
    }

    private void drawEmptyState(Graphics2D graphics2D, String message) {
        graphics2D.setColor(new Color(110, 110, 110));
        graphics2D.setFont(getFont().deriveFont(Font.PLAIN, 14f));
        int textWidth = graphics2D.getFontMetrics().stringWidth(message);
        graphics2D.drawString(message, (getWidth() - textWidth) / 2, getHeight() / 2);
    }

    private String buildBarLabel(BenchmarkSummaryRecord record) {
        StringBuilder label = new StringBuilder(record.getAlgorithm());

        if (!GraphFilterState.ALL.equals(filterState.getMode())) {
            label.append(" ").append(record.getThreads()).append("t");
        } else {
            label.append(" ").append(record.getMode());
            if ("parallel".equals(record.getMode())) {
                label.append("-").append(record.getThreads()).append("t");
            }
        }

        if (filterState.getArraySize() == null) {
            label.append(" ").append(record.getArraySize());
        }

        return label.toString();
    }

    private String buildLineSeriesKey(BenchmarkSummaryRecord record) {
        if (!GraphFilterState.ALL.equals(filterState.getAlgorithm())) {
            if ("parallel".equals(record.getMode()) && filterState.getThreads() == null) {
                return record.getMode() + "-" + record.getThreads() + " threads";
            }

            if (GraphFilterState.ALL.equals(filterState.getMode())) {
                return record.getMode() + ("parallel".equals(record.getMode()) ? "-" + record.getThreads() + " threads" : "");
            }

            return record.getAlgorithm();
        }

        return record.getAlgorithm() + ("parallel".equals(record.getMode()) ? "-" + record.getThreads() + " threads" : "");
    }

    private String buildSpeedupSeriesLabel(BenchmarkSummaryRecord record) {
        if (!GraphFilterState.ALL.equals(filterState.getAlgorithm())) {
            return record.getInputType();
        }

        if (!GraphFilterState.ALL.equals(filterState.getInputType())) {
            return record.getAlgorithm();
        }

        return record.getAlgorithm() + " - " + record.getInputType();
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
}
