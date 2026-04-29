import graficos.CsvSummaryReader;
import graficos.GraphFrame;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::startApplication);
    }

    private static void startApplication() {
        LoadingDialog loadingDialog = new LoadingDialog();

        SwingWorker<GraphFrame, String> worker = new SwingWorker<>() {
            @Override
            protected GraphFrame doInBackground() throws Exception {
                publish("Executando benchmark completo...");
                Path csvPath = BenchmarkMain.runBenchmark();

                publish("Carregando dados consolidados...");
                return new GraphFrame(csvPath, new CsvSummaryReader().read(csvPath));
            }

            @Override
            protected void process(java.util.List<String> messages) {
                loadingDialog.setMessage(messages.get(messages.size() - 1));
            }

            @Override
            protected void done() {
                loadingDialog.dispose();

                try {
                    GraphFrame frame = get();
                    frame.setVisible(true);
                } catch (Exception exception) {
                    throw new RuntimeException("Não foi possível executar o benchmark e abrir os gráficos.", exception);
                }
            }
        };

        worker.execute();
        loadingDialog.setVisible(true);
    }

    private static class LoadingDialog extends JDialog {

        private final JLabel messageLabel;

        public LoadingDialog() {
            super((JFrame) null, "Preparando análise", true);

            messageLabel = new JLabel("Inicializando benchmark...");
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);

            JPanel panel = new JPanel(new BorderLayout(0, 12));
            panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
            panel.add(messageLabel, BorderLayout.NORTH);
            panel.add(progressBar, BorderLayout.CENTER);

            setContentPane(panel);
            setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            setPreferredSize(new Dimension(360, 120));
            pack();
            setResizable(false);
            setLocationRelativeTo(null);
        }

        public void setMessage(String message) {
            messageLabel.setText(message);
        }
    }
}
