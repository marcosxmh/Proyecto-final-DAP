import java.util.List;

public class PopularChannelsStrategy implements RecommendationStrategy {
    @Override
    public List<String> recommend(List<YouTubeChannel> subscribedChannels, YouTubeAPIService apiService) {
        return apiService.getAvailableChannels(""); // Devuelve los más populares en general
    }
}
