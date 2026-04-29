import benchmark.BenchmarkAggregator;
import benchmark.BenchmarkConfig;
import benchmark.BenchmarkCsvExporter;
import benchmark.BenchmarkResultado;
import benchmark.BenchmarkRunner;
import benchmark.BenchmarkResumo;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        BenchmarkConfig config = BenchmarkConfig.defaultConfig();
        BenchmarkRunner runner = new BenchmarkRunner();

        List<BenchmarkResultado> resultados = runner.run(config);
        List<BenchmarkResumo> resumos = BenchmarkAggregator.aggregate(resultados);

        BenchmarkCsvExporter exporter = new BenchmarkCsvExporter();
        exporter.exportRawResults(config.getOutputDirectory(), resultados);
        exporter.exportSummaryResults(config.getOutputDirectory(), resumos);

        System.out.println("--------------------------------------");
        System.out.println("Benchmark concluido");
        System.out.println("Resultados brutos: " + config.getOutputDirectory() + "/benchmark_resultados.csv");
        System.out.println("Resultados agregados: " + config.getOutputDirectory() + "/benchmark_resumo.csv");
        System.out.println("Total de execucoes: " + resultados.size());
        System.out.println("Total de cenarios agregados: " + resumos.size());
    }
}
