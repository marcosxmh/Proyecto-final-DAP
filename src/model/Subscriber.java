package model;

import java.util.HashMap;
import javax.swing.JTextArea;
import java.util.ArrayList;
import java.util.List;
import model.SearchHistoryManagerImpl;

public class Subscriber implements Observer {
    private String name;
    private HashMap<String, String> lastVideos = new HashMap<>();
    private SearchHistoryManager searchHistoryManager;
    private List<String> lastNotifications;

    public Subscriber(String name) {
        this.name = name;
        this.searchHistoryManager = new SearchHistoryManagerImpl(); // Cambiado aquí
        this.lastNotifications = new ArrayList<>();
    }

    public String getName() {
        return name;
    }


    @Override
    public void update(String videoTitle, String channelTitle, String uploadDate) {
        //sort the videos by date
        lastVideos.put(videoTitle, channelTitle + " (Uploaded on: " + uploadDate + ")");

    }

    @Override
    public void clearLastNotifications() {
        lastVideos.clear();
    }

    public HashMap<String,String> getLastVideos() {
        return lastVideos;
    }

    @Override
    public void addSearchHistory(String searchQuery) {
        searchHistoryManager.addSearchHistory(searchQuery);
    }


    @Override
    public void viewSearchHistoryInGUI(JTextArea searchHistoryArea) {
        searchHistoryManager.viewSearchHistoryInGUI(searchHistoryArea);
    }

    public SearchHistoryManager getSearchHistoryManager() {
        return searchHistoryManager;
    }


}
