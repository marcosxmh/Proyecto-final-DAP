import java.util.ArrayList;
import java.util.List;

public class SimilarChannelsStrategy implements RecommendationStrategy {
    @Override
    public List<String> recommend(List<YouTubeChannel> subscribedChannels, YouTubeAPIService apiService) {
        List<String> recommendations = new ArrayList<>();
        for (YouTubeChannel channel : subscribedChannels) {
            List<String> relatedChannels = apiService.getAvailableChannels(channel.getSearchQuery());
            for (String related : relatedChannels) {
                if (!recommendations.contains(related)) {
                    recommendations.add(related);
                }
            }
        }
        return recommendations;
    }
}
