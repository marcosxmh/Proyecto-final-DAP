package model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExport extends ExportTemplate {
    @Override
    protected String formatData(List<String> searchHistory) {
        return String.join(",", searchHistory); // Unir los elementos con comas
    }

    @Override
    protected void writeFile(String data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(data);
            System.out.println("Search history exported to CSV at: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + e.getMessage());
        }
    }
}
