package controller;

import model.RecommendationStrategy;
import model.DescriptionBasedRecommendation;
import model.TagsBasedRecommendation;
import model.YouTubeAPIService;
import view.UserGUI;
import model.YouTubeChannel;

import java.util.List;

public class YouTubeController {
    private YouTubeAPIService apiService;
    private UserGUI userGUI;
    private RecommendationStrategy recommendationStrategy;

    public YouTubeController(YouTubeAPIService apiService, UserGUI userGUI) {
        this.apiService = apiService;
        this.userGUI = userGUI;
        this.recommendationStrategy = new DescriptionBasedRecommendation(); // Estrategia predeterminada
        initController();
    }

    private void initController() {
        userGUI.getSearchButton().addActionListener(e -> searchChannels());
        userGUI.getSubscribeButton().addActionListener(e -> subscribeUser());
        userGUI.getUnsubscribeButton().addActionListener(e -> unsubscribeUser());
        userGUI.getRecommendButton().addActionListener(e -> recommendSimilarChannels());
    }

    private void searchChannels() {
        String searchQuery = userGUI.getSearchQuery();
        if (searchQuery.isEmpty()) {
            userGUI.showMessage("Please enter a search query");
            return;
        }
        userGUI.updateChannelList(apiService.getAvailableChannels(searchQuery));
    }

    private void subscribeUser() {
        String selectedChannel = userGUI.getSelectedChannel();
        if (selectedChannel != null) {
            if (!apiService.channelExists(selectedChannel)) {
                apiService.addChannel(new YouTubeChannel(selectedChannel));
            }
            YouTubeChannel channel = apiService.getChannel(selectedChannel);
            if (channel.getObservers().contains(userGUI.getSubscriber())) {
                userGUI.showMessage("You are already subscribed to " + selectedChannel);
                return;
            }
            channel.subscribe(userGUI.getSubscriber());
            userGUI.showMessage("Subscribed to " + selectedChannel);
        }
    }

    private void unsubscribeUser() {
        String selectedChannel = userGUI.getSelectedChannel();
        if (selectedChannel != null) {
            if (!apiService.channelExists(selectedChannel)) {
                userGUI.showMessage("Channel does not exist");
                return;
            }
            YouTubeChannel channel = apiService.getChannel(selectedChannel);
            if (!channel.getObservers().contains(userGUI.getSubscriber())) {
                userGUI.showMessage("You are not subscribed to " + selectedChannel);
                return;
            }
            channel.unsubscribe(userGUI.getSubscriber());
            userGUI.showMessage("Unsubscribed from " + selectedChannel);
        }
    }

    private void recommendSimilarChannels() {
        String selectedChannel = userGUI.getSelectedChannel();
        if (selectedChannel != null) {
            List<String> recommendations = recommendationStrategy.recommendChannels(selectedChannel, apiService);
            userGUI.showRecommendations(recommendations);
        } else {
            userGUI.showMessage("Please select a channel first.");
        }
    }

    public void setRecommendationStrategy(RecommendationStrategy strategy) {
        this.recommendationStrategy = strategy;
    }
}
