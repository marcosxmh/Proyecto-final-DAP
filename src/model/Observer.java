package model;

import javax.swing.JTextArea;

public interface Observer {
    void update(String videoTitle, String channelTitle, String uploadDate);
    void clearLastNotifications();
    void addSearchHistory(String searchQuery);
    void viewSearchHistoryInGUI(JTextArea searchHistoryArea);
}
