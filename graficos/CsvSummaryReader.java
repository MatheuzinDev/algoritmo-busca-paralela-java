package graficos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvSummaryReader {

    public List<BenchmarkSummaryRecord> read(Path csvPath) throws IOException {
        List<String> lines = Files.readAllLines(csvPath);
        List<BenchmarkSummaryRecord> records = new ArrayList<>();

        for (int lineIndex = 1; lineIndex < lines.size(); lineIndex++) {
            String line = lines.get(lineIndex).trim();

            if (line.isEmpty()) {
                continue;
            }

            List<String> columns = parseCsvLine(line);
            records.add(new BenchmarkSummaryRecord(
                    columns.get(0),
                    columns.get(1),
                    Integer.parseInt(columns.get(2)),
                    Integer.parseInt(columns.get(3)),
                    columns.get(4),
                    Integer.parseInt(columns.get(5)),
                    Double.parseDouble(columns.get(6)),
                    Double.parseDouble(columns.get(7)),
                    Double.parseDouble(columns.get(8)),
                    Double.parseDouble(columns.get(9)),
                    Boolean.parseBoolean(columns.get(10))
            ));
        }

        return records;
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean insideQuotes = false;

        for (int index = 0; index < line.length(); index++) {
            char currentChar = line.charAt(index);

            if (currentChar == '"') {
                if (insideQuotes && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    currentValue.append('"');
                    index++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (currentChar == ',' && !insideQuotes) {
                values.add(currentValue.toString());
                currentValue.setLength(0);
            } else {
                currentValue.append(currentChar);
            }
        }

        values.add(currentValue.toString());
        return values;
    }
}
