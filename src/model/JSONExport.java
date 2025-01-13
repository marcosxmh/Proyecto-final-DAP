package model;

import org.json.JSONArray;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JSONExport extends ExportTemplate {
    @Override
    protected String formatData(List<String> searchHistory) {
        JSONArray jsonArray = new JSONArray(searchHistory);
        return jsonArray.toString(2); // Formatear como JSON legible
    }

    @Override
    protected void writeFile(String data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(data);
            System.out.println("Search history exported to JSON at: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing JSON file: " + e.getMessage());
        }
    }
}
