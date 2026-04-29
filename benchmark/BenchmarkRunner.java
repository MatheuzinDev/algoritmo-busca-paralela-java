package benchmark;

import algoritmos.AlgoritmoSort;
import utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkRunner {

    public List<BenchmarkResultado> run(BenchmarkConfig config) {
        List<BenchmarkResultado> resultados = new ArrayList<>();
        List<BenchmarkScenario> scenarios = buildScenarios(config);

        for (int index = 0; index < scenarios.size(); index++) {
            BenchmarkScenario scenario = scenarios.get(index);
            BenchmarkResultado resultado = executeScenario(scenario, config);
            resultados.add(resultado);

            System.out.println("[" + (index + 1) + "/" + scenarios.size() + "] " + resultado);
        }

        return resultados;
    }

    private List<BenchmarkScenario> buildScenarios(BenchmarkConfig config) {
        List<BenchmarkScenario> scenarios = new ArrayList<>();

        for (SortAlgorithmType algorithm : config.getAlgorithms()) {
            for (int arraySize : algorithm.getArraySizes()) {
                for (InputType inputType : config.getInputTypes()) {
                    for (int sampleNumber = 1; sampleNumber <= config.getSampleCount(); sampleNumber++) {
                        scenarios.add(new BenchmarkScenario(
                                algorithm,
                                ExecutionMode.SERIAL,
                                1,
                                arraySize,
                                inputType,
                                sampleNumber
                        ));

                        for (int threadCount : config.getParallelThreadCounts()) {
                            scenarios.add(new BenchmarkScenario(
                                    algorithm,
                                    ExecutionMode.PARALLEL,
                                    threadCount,
                                    arraySize,
                                    inputType,
                                    sampleNumber
                            ));
                        }
                    }
                }
            }
        }

        return scenarios;
    }

    private BenchmarkResultado executeScenario(BenchmarkScenario scenario, BenchmarkConfig config) {
        int[] baseArray = scenario.getInputType().generateArray(
                scenario.getArraySize(),
                config.getMaxValue(),
                scenario.buildSeed()
        );

        int[] arrayToSort = ArrayUtils.copyArray(baseArray);
        boolean parallel = scenario.getMode() == ExecutionMode.PARALLEL;
        AlgoritmoSort algorithm = scenario.getAlgorithm().create(parallel, scenario.getThreads());

        long startTime = System.nanoTime();
        algorithm.sort(arrayToSort);
        long endTime = System.nanoTime();

        return new BenchmarkResultado(
                scenario.getAlgorithm().getDisplayName(),
                scenario.getMode().getLabel(),
                scenario.getArraySize(),
                scenario.getInputType().getLabel(),
                scenario.getThreads(),
                scenario.getSampleNumber(),
                endTime - startTime,
                ArrayUtils.isSorted(arrayToSort)
        );
    }
}
