package model;

import java.util.List;

public abstract class ExportTemplate {
    // Método template
    public final void export(List<String> searchHistory, String filePath) {
        if (searchHistory == null || searchHistory.isEmpty()) {
            System.out.println("No search history to export.");
            return;
        }
        String formattedData = formatData(searchHistory);
        writeFile(formattedData, filePath);
    }

    // Métodos abstractos para implementar en subclases
    protected abstract String formatData(List<String> searchHistory);
    protected abstract void writeFile(String data, String filePath);
}
