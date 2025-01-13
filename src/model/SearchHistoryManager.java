package model;

import javax.swing.JTextArea;

public interface SearchHistoryManager {
    void addSearchHistory(String searchQuery);
    void viewSearchHistoryInGUI(JTextArea searchHistoryArea);
}
