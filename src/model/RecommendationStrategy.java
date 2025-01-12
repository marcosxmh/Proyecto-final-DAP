package model;

import java.util.List;

public interface RecommendationStrategy {
    List<String> recommendChannels(String channelName, YouTubeAPIService apiService);
}
