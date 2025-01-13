package model;

import java.util.List;

public class SearchMemento {
    private final List<String> searchHistory;

    public SearchMemento(List<String> searchHistory) {
        System.out.println("Creating Memento with search history: " + String.join(", ", searchHistory));
        this.searchHistory = List.copyOf(searchHistory);
    }

    public List<String> getSearchHistory() {
        return searchHistory;
    }
}