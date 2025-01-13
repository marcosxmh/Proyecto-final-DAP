package model;

import javax.swing.JTextArea;
import java.util.ArrayList;
import java.util.List;

public class SearchHistoryManagerImpl implements SearchHistoryManager {
    private final SearchCaretaker caretaker = new SearchCaretaker();
    private final List<String> searchHistory = new ArrayList<>();

    @Override
    public void addSearchHistory(String searchQuery) {
        searchHistory.add(searchQuery);
        System.out.println("Adding search query to history: " + searchQuery);
        System.out.println("Current Search History: " + String.join(", ", searchHistory));

        if (!searchHistory.isEmpty()) {
            SearchMemento memento = new SearchMemento(new ArrayList<>(searchHistory));
            caretaker.addMemento(memento);
            System.out.println("Memento added to caretaker. Total mementos: " + caretaker.getMementoCount());
        } else {
            System.out.println("Search history is empty, memento not created.");
        }
    }

    @Override
    public void viewSearchHistoryInGUI(JTextArea searchHistoryArea) {
        StringBuilder history = new StringBuilder();
        System.out.println("Caretaker Mementos Count: " + caretaker.getMementoCount());

        if (caretaker.getMementoCount() > 0) {
            for (int i = 0; i < caretaker.getMementoCount(); i++) {
                SearchMemento memento = caretaker.getMemento(i);
                if (memento != null) {
                    String searchHistoryString = String.join(", ", memento.getSearchHistory());
                    history.append(searchHistoryString).append("\n");
                    System.out.println("Displaying Search History: " + searchHistoryString);
                } else {
                    System.out.println("Memento at index " + i + " is null.");
                }
            }
        } else {
            System.out.println("No mementos found.");
        }

        searchHistoryArea.setText(history.toString());
    }

    public List<String> getSearchHistory() {
        return new ArrayList<>(searchHistory); // Retorna una copia del historial
    }



}
