import java.util.List;

public interface RecommendationStrategy {
    List<String> recommend(List<YouTubeChannel> subscribedChannels, YouTubeAPIService apiService);
}
