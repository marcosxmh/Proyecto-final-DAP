import java.util.List;

public class RecommendationContext {
    private RecommendationStrategy strategy;

    public void setStrategy(RecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public List<String> getRecommendations(List<YouTubeChannel> subscribedChannels, YouTubeAPIService apiService) {
        if (strategy == null) {
            throw new IllegalStateException("Strategy not set!");
        }
        return strategy.recommend(subscribedChannels, apiService);
    }
}
