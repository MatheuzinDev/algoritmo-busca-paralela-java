package benchmark;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BenchmarkAggregator {

    public static List<BenchmarkResumo> aggregate(List<BenchmarkResultado> resultados) {
        Map<String, List<BenchmarkResultado>> groupedResults = new LinkedHashMap<>();

        for (BenchmarkResultado resultado : resultados) {
            groupedResults
                    .computeIfAbsent(buildGroupKey(resultado), key -> new ArrayList<>())
                    .add(resultado);
        }

        List<BenchmarkResumo> resumos = new ArrayList<>();

        for (List<BenchmarkResultado> group : groupedResults.values()) {
            resumos.add(buildSummary(group));
        }

        return resumos;
    }

    private static String buildGroupKey(BenchmarkResultado resultado) {
        return resultado.getAlgorithmName() + "|" +
                resultado.getMode() + "|" +
                resultado.getThreads() + "|" +
                resultado.getArraySize() + "|" +
                resultado.getInputType();
    }

    private static BenchmarkResumo buildSummary(List<BenchmarkResultado> resultados) {
        BenchmarkResultado firstResult = resultados.get(0);
        double sum = 0.0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        boolean allSortedCorrectly = true;

        for (BenchmarkResultado resultado : resultados) {
            double executionTimeInMillis = resultado.getExecutionTimeInMillis();
            sum += executionTimeInMillis;
            min = Math.min(min, executionTimeInMillis);
            max = Math.max(max, executionTimeInMillis);
            allSortedCorrectly = allSortedCorrectly && resultado.isSortedCorrectly();
        }

        double average = sum / resultados.size();
        double varianceSum = 0.0;

        for (BenchmarkResultado resultado : resultados) {
            double difference = resultado.getExecutionTimeInMillis() - average;
            varianceSum += difference * difference;
        }

        double standardDeviation = Math.sqrt(varianceSum / resultados.size());

        return new BenchmarkResumo(
                firstResult.getAlgorithmName(),
                firstResult.getMode(),
                firstResult.getThreads(),
                firstResult.getArraySize(),
                firstResult.getInputType(),
                resultados.size(),
                average,
                min,
                max,
                standardDeviation,
                allSortedCorrectly
        );
    }
}
