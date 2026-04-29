import benchmark.BenchmarkAggregator;
import benchmark.BenchmarkConfig;
import benchmark.BenchmarkCsvExporter;
import benchmark.BenchmarkResultado;
import benchmark.BenchmarkRunner;
import benchmark.BenchmarkResumo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BenchmarkMain {

    public static void main(String[] args) throws IOException {
        Path summaryPath = runBenchmark();

        System.out.println("Arquivo de resumo gerado em: " + summaryPath);
    }

    public static Path runBenchmark() throws IOException {
        BenchmarkConfig config = BenchmarkConfig.defaultConfig();
        BenchmarkRunner runner = new BenchmarkRunner();

        List<BenchmarkResultado> resultados = runner.run(config);
        List<BenchmarkResumo> resumos = BenchmarkAggregator.aggregate(resultados);

        BenchmarkCsvExporter exporter = new BenchmarkCsvExporter();
        exporter.exportRawResults(config.getOutputDirectory(), resultados);
        exporter.exportSummaryResults(config.getOutputDirectory(), resumos);

        System.out.println("--------------------------------------");
        System.out.println("Benchmark concluído");
        System.out.println("Resultados brutos: " + config.getOutputDirectory() + "/benchmark_resultados.csv");
        System.out.println("Resultados agregados: " + config.getOutputDirectory() + "/benchmark_resumo.csv");
        System.out.println("Total de execuções: " + resultados.size());
        System.out.println("Total de cenários agregados: " + resumos.size());

        return Paths.get(config.getOutputDirectory(), "benchmark_resumo.csv");
    }
}
