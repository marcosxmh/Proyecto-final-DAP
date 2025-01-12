package controller;

import model.YouTubeAPIService;
import model.YouTubeChannel;
import view.UserGUI;

public class YouTubeController {
    private YouTubeAPIService apiService;
    private UserGUI userGUI;

    public YouTubeController(YouTubeAPIService apiService, UserGUI userGUI) {
        this.apiService = apiService;
        this.userGUI = userGUI;
        initController();
    }

    private void initController() {
        userGUI.getSearchButton().addActionListener(e -> searchChannels());
        userGUI.getSubscribeButton().addActionListener(e -> subscribeUser());
        userGUI.getUnsubscribeButton().addActionListener(e -> unsubscribeUser());
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
                YouTubeChannel newChannel = new YouTubeChannel(selectedChannel);
                apiService.addChannel(newChannel);
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
}