package model;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class YouTubeAPIService {
    private static final String API_KEY = "AIzaSyC4TFkCYgOZeXeAUYpfmM2A6XFMqtRXSqI";
    private static final String BASE_URL = "https://www.googleapis.com/youtube/v3/search";
    private List<YouTubeChannel> channels = new ArrayList<YouTubeChannel>();

    public void addChannel(YouTubeChannel channel) {
        channels.add(channel);
    }

    public List<YouTubeChannel> getChannels() {
        return channels;
    }

    //check if the channel is already in the list
    public boolean channelExists(String searchQuery) {
        for (YouTubeChannel channel : channels) {
            if (channel.getSearchQuery().equals(searchQuery)) {
                return true;
            }
        }
        return false;
    }

    //get channel by search query
    public YouTubeChannel getChannel(String searchQuery) {
        for (YouTubeChannel channel : channels) {
            if (channel.getSearchQuery().equals(searchQuery)) {
                return channel;
            }
        }
        return null;
    }

    public void fetchVideos(YouTubeChannel channel) {
        String searchQuery = channel.getSearchQuery();
        int maxResults = 3;

        try {
            // Fetch the channel ID first
            String channelUrlString = String.format("%s?part=snippet&type=channel&q=%s&key=%s",
                    BASE_URL, searchQuery.replace(" ", "%20"), API_KEY);

            URL channelUrl = new URL(channelUrlString);
            HttpURLConnection channelConnection = (HttpURLConnection) channelUrl.openConnection();
            channelConnection.setRequestMethod("GET");

            int channelResponseCode = channelConnection.getResponseCode();
            if (channelResponseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader channelIn = new BufferedReader(new InputStreamReader(channelConnection.getInputStream()));
                StringBuilder channelResponse = new StringBuilder();
                String channelLine;
                while ((channelLine = channelIn.readLine()) != null) {
                    channelResponse.append(channelLine);
                }
                channelIn.close();

                JSONObject channelJsonResponse = new JSONObject(channelResponse.toString());
                JSONArray channelItems = channelJsonResponse.getJSONArray("items");
                if (channelItems.length() > 0) {
                    String channelId = channelItems.getJSONObject(0).getJSONObject("id").getString("channelId");

                    // Fetch the uploads playlist ID
                    String playlistUrlString = String.format("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&id=%s&key=%s",
                            channelId, API_KEY);

                    URL playlistUrl = new URL(playlistUrlString);
                    HttpURLConnection playlistConnection = (HttpURLConnection) playlistUrl.openConnection();
                    playlistConnection.setRequestMethod("GET");

                    int playlistResponseCode = playlistConnection.getResponseCode();
                    if (playlistResponseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader playlistIn = new BufferedReader(new InputStreamReader(playlistConnection.getInputStream()));
                        StringBuilder playlistResponse = new StringBuilder();
                        String playlistLine;
                        while ((playlistLine = playlistIn.readLine()) != null) {
                            playlistResponse.append(playlistLine);
                        }
                        playlistIn.close();

                        JSONObject playlistJsonResponse = new JSONObject(playlistResponse.toString());
                        JSONArray playlistItems = playlistJsonResponse.getJSONArray("items");
                        if (playlistItems.length() > 0) {
                            String uploadsPlaylistId = playlistItems.getJSONObject(0).getJSONObject("contentDetails")
                                    .getJSONObject("relatedPlaylists").getString("uploads");

                            // Fetch the last videos from the uploads playlist
                            String videosUrlString = String.format("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=%d&playlistId=%s&key=%s",
                                    maxResults, uploadsPlaylistId, API_KEY);

                            URL videosUrl = new URL(videosUrlString);
                            HttpURLConnection videosConnection = (HttpURLConnection) videosUrl.openConnection();
                            videosConnection.setRequestMethod("GET");

                            int videosResponseCode = videosConnection.getResponseCode();
                            if (videosResponseCode == HttpURLConnection.HTTP_OK) {
                                BufferedReader videosIn = new BufferedReader(new InputStreamReader(videosConnection.getInputStream()));
                                StringBuilder videosResponse = new StringBuilder();
                                String videosLine;
                                while ((videosLine = videosIn.readLine()) != null) {
                                    videosResponse.append(videosLine);
                                }
                                videosIn.close();

                                JSONObject videosJsonResponse = new JSONObject(videosResponse.toString());
                                JSONArray videosItems = videosJsonResponse.getJSONArray("items");

                                List<JSONObject> videoList = new ArrayList<>();
                                for (int i = 0; i < videosItems.length(); i++) {
                                    videoList.add(videosItems.getJSONObject(i).getJSONObject("snippet"));
                                }

                                // Sort videos by upload date
                                Collections.sort(videoList, new Comparator<JSONObject>() {
                                    @Override
                                    public int compare(JSONObject video1, JSONObject video2) {
                                        String date1 = video1.getString("publishedAt");
                                        String date2 = video2.getString("publishedAt");
                                        return date2.compareTo(date1);
                                    }
                                });

                                for (JSONObject video : videoList) {
                                    String videoTitle = video.getString("title");
                                    String channelTitle = video.getString("channelTitle");
                                    String uploadDate = video.getString("publishedAt");

                                    channel.notifyObservers(videoTitle, channelTitle, uploadDate);
                                }
                            } else {
                                System.out.println("Error fetching videos: " + videosResponseCode);
                            }
                        }
                    } else {
                        System.out.println("Error fetching playlist: " + playlistResponseCode);
                    }
                }
            } else {
                System.out.println("Error fetching channel: " + channelResponseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getAvailableChannels(String searchQuery) {
        Set<String> channelTitles = new HashSet<>();
        int maxResults = 5;

        try {
            String urlString = String.format("%s?part=snippet&maxResults=%d&q=%s&type=channel&key=%s",
                    BASE_URL, maxResults, searchQuery.replace(" ", "%20"), API_KEY);

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray items = jsonResponse.getJSONArray("items");

                for (int i = 0; i < items.length(); i++) {
                    JSONObject channel = items.getJSONObject(i).getJSONObject("snippet");
                    String channelTitle = channel.getString("channelTitle");
                    channelTitles.add(channelTitle);
                }
            } else {
                System.out.println("Error en la conexión: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(channelTitles);
    }

    // Metodo para obtener el ID de un canal
    public String getChannelIdByName(String channelName) {
        try {
            String urlString = String.format("%s?part=snippet&type=channel&q=%s&key=%s",
                    BASE_URL, channelName.replace(" ", "%20"), API_KEY);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                System.out.println(jsonResponse.toString(2)); // Imprimir respuesta JSON para depuración

                JSONArray items = jsonResponse.getJSONArray("items");
                if (items.length() > 0) {
                    JSONObject channel = items.getJSONObject(0);
                    return channel.getJSONObject("id").getString("channelId");
                }
            } else {
                System.out.println("Error en la conexión: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Obtener detalles del canal, incluyendo redes sociales
    public JSONObject getChannelInfo(String channelId) {
        try {
            String url = String.format("https://www.googleapis.com/youtube/v3/channels?part=snippet,statistics&id=%s&key=%s", channelId, API_KEY);
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode); // Depuración

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            System.out.println(jsonResponse.toString(2)); // Imprimir respuesta JSON para depuración

            JSONArray items = jsonResponse.getJSONArray("items");
            if (items.length() > 0) {
                JSONObject channel = items.getJSONObject(0);
                JSONObject snippet = channel.getJSONObject("snippet");
                JSONObject statistics = channel.getJSONObject("statistics");

                JSONObject channelDetails = new JSONObject();
                channelDetails.put("name", snippet.getString("title"));
                channelDetails.put("subscribers", statistics.getInt("subscriberCount"));
                channelDetails.put("description", snippet.getString("description"));

                // Obtener redes sociales, si están disponibles
                JSONArray socialLinks = new JSONArray();
                if (snippet.has("customUrl")) {
                    JSONObject socialLink = new JSONObject();
                    socialLink.put("platform", "YouTube");
                    socialLink.put("link", "https://youtube.com/" + snippet.getString("customUrl"));
                    socialLinks.put(socialLink);
                }
                channelDetails.put("socialLinks", socialLinks);

                return channelDetails;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getVideoIdByName(String videoName) {
        try {
            String urlString = String.format("https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&q=%s&key=%s",
                    videoName.replace(" ", "%20"), API_KEY);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray items = jsonResponse.getJSONArray("items");

                if (items.length() > 0) {
                    // Obtener el video ID del primer resultado
                    JSONObject firstItem = items.getJSONObject(0);
                    return firstItem.getJSONObject("id").getString("videoId");
                } else {
                    return null;  // No se encontró el video
                }
            } else {
                System.out.println("Error en la conexión: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
