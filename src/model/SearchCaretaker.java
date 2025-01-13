package model;

import java.util.ArrayList;
import java.util.List;

public class SearchCaretaker {
    private final List<SearchMemento> mementoList = new ArrayList<>();

    public void addMemento(SearchMemento memento) {
        if (memento != null) {
            mementoList.add(memento);
            System.out.println("Memento added. Total mementos: " + mementoList.size());
        } else {
            System.out.println("Memento is null, cannot add.");
        }
    }

    public SearchMemento getMemento(int index) {
        if (index >= 0 && index < mementoList.size()) {
            return mementoList.get(index);
        }
        return null;
    }

    public int getMementoCount() {
        return mementoList.size();
    }
}