package model;



import org.json.JSONObject;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

public class ChannelDetailsAdapter implements Adapter {
    private YouTubeAPIService apiService;
    private String apiKey;

    public ChannelDetailsAdapter(YouTubeAPIService apiService, String apiKey) {
        this.apiService = apiService;
        this.apiKey = apiKey;
    }

    public ChannelDetails getChannelDetails(String channelNameInput) {
        String channelId = apiService.getChannelIdByName(channelNameInput);
        if (channelId != null) {
            JSONObject channelInfo = apiService.getChannelInfo(channelId);
            if (channelInfo != null) {
                String channelName = channelInfo.getString("name");
                String description = channelInfo.getString("description");
                int subscribers = channelInfo.getInt("subscribers");
                JSONArray socialLinks = channelInfo.getJSONArray("socialLinks");

                // Crear y devolver el objeto ChannelDetails
                ChannelDetails details = new ChannelDetails(channelName, description, subscribers);

                // Iterar sobre el JSONArray de enlaces sociales
                for (int i = 0; i < socialLinks.length(); i++) {
                    JSONObject socialLink = socialLinks.getJSONObject(i); // Obtener el objeto socialLink
                    String platform = socialLink.getString("platform"); // Obtener la plataforma
                    String link = socialLink.getString("link"); // Obtener el enlace

                    // Agregar la plataforma y el enlace al objeto ChannelDetails
                    details.addSocialLink(platform, link);
                }

                return details;
            }
        }
        return null; // Retornar null si no se pudo obtener la información
    }


    public String getChannelIdByName(String channelName) {
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=channel&q=" + channelName + "&key=" + apiKey;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (response.length() > 0) {
                JSONObject jsonResponse = new JSONObject(response.toString());
                if (jsonResponse.has("items") && jsonResponse.getJSONArray("items").length() > 0) {
                    // Obtener el primer canal de la lista de resultados
                    String channelId = jsonResponse.getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("channelId");
                    return channelId;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al hacer la solicitud de búsqueda: " + e.getMessage());
        }
        return null;
    }
}

