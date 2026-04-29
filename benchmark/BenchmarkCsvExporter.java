package benchmark;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public class BenchmarkCsvExporter {

    public void exportRawResults(String outputDirectory, List<BenchmarkResultado> resultados) throws IOException {
        Path directoryPath = prepareOutputDirectory(outputDirectory);
        Path filePath = directoryPath.resolve("benchmark_resultados.csv");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("algorithm,mode,threads,arraySize,inputType,sample,executionTimeNs,executionTimeMs,sortedCorrectly");
            writer.newLine();

            for (BenchmarkResultado resultado : resultados) {
                writer.write(csvValue(resultado.getAlgorithmName()) + "," +
                        csvValue(resultado.getMode()) + "," +
                        resultado.getThreads() + "," +
                        resultado.getArraySize() + "," +
                        csvValue(resultado.getInputType()) + "," +
                        resultado.getSampleNumber() + "," +
                        resultado.getExecutionTimeInNanos() + "," +
                        formatDecimal(resultado.getExecutionTimeInMillis()) + "," +
                        resultado.isSortedCorrectly());
                writer.newLine();
            }
        }
    }

    public void exportSummaryResults(String outputDirectory, List<BenchmarkResumo> resumos) throws IOException {
        Path directoryPath = prepareOutputDirectory(outputDirectory);
        Path filePath = directoryPath.resolve("benchmark_resumo.csv");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("algorithm,mode,threads,arraySize,inputType,samples,avgTimeMs,minTimeMs,maxTimeMs,stdDevMs,allSortedCorrectly");
            writer.newLine();

            for (BenchmarkResumo resumo : resumos) {
                writer.write(csvValue(resumo.getAlgorithmName()) + "," +
                        csvValue(resumo.getMode()) + "," +
                        resumo.getThreads() + "," +
                        resumo.getArraySize() + "," +
                        csvValue(resumo.getInputType()) + "," +
                        resumo.getSampleCount() + "," +
                        formatDecimal(resumo.getAverageTimeInMillis()) + "," +
                        formatDecimal(resumo.getMinTimeInMillis()) + "," +
                        formatDecimal(resumo.getMaxTimeInMillis()) + "," +
                        formatDecimal(resumo.getStandardDeviationInMillis()) + "," +
                        resumo.isAllSortedCorrectly());
                writer.newLine();
            }
        }
    }

    private Path prepareOutputDirectory(String outputDirectory) throws IOException {
        Path directoryPath = Paths.get(outputDirectory);
        Files.createDirectories(directoryPath);
        return directoryPath;
    }

    private String formatDecimal(double value) {
        return String.format(Locale.US, "%.6f", value);
    }

    private String csvValue(String value) {
        return '"' + value.replace("\"", "\"\"") + '"';
    }
}
