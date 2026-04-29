import graficos.CsvSummaryReader;
import graficos.GraphFrame;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GraphMain {

    public static void main(String[] args) {
        Path csvPath = Paths.get("resultados", "benchmark_resumo.csv");

        SwingUtilities.invokeLater(() -> {
            try {
                GraphFrame frame = new GraphFrame(csvPath, new CsvSummaryReader().read(csvPath));
                frame.setVisible(true);
            } catch (IOException exception) {
                throw new RuntimeException("Nao foi possivel carregar o arquivo de resumo: " + csvPath, exception);
            }
        });
    }
}
