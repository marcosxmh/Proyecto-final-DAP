package model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TagsBasedRecommendation implements RecommendationStrategy {
    @Override
    public List<String> recommendChannels(String channelName, YouTubeAPIService apiService) {
        String channelId = apiService.getChannelIdByName(channelName);
        if (channelId != null) {
            List<String> tags = apiService.getChannelTags(channelId);
            if (tags != null && !tags.isEmpty()) {
                return apiService.getSimilarChannelsByTags(tags);
            }
        }
        return new ArrayList<>();
    }
}

