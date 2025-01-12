package model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DescriptionBasedRecommendation implements RecommendationStrategy {
    @Override
    public List<String> recommendChannels(String channelName, YouTubeAPIService apiService) {
        String channelId = apiService.getChannelIdByName(channelName);
        if (channelId != null) {
            JSONObject channelInfo = apiService.getChannelInfo(channelId);
            if (channelInfo != null) {
                String description = channelInfo.getString("description");
                return apiService.getSimilarChannelsByDescription(description);
            }
        }
        return new ArrayList<>();
    }
}
