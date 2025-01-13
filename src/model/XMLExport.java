package model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class XMLExport extends ExportTemplate {
    @Override
    protected String formatData(List<String> searchHistory) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<SearchHistory>\n");
        for (String query : searchHistory) {
            xmlBuilder.append("    <Query>").append(query).append("</Query>\n");
        }
        xmlBuilder.append("</SearchHistory>");
        return xmlBuilder.toString();
    }

    @Override
    protected void writeFile(String data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(data);
            System.out.println("Search history exported to XML at: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing XML file: " + e.getMessage());
        }
    }
}
