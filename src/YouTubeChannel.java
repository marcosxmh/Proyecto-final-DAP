import java.util.ArrayList;
import java.util.List;

public class YouTubeChannel {
    private List<Observer> observers = new ArrayList<>();
    private String searchQuery;

    public YouTubeChannel(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void subscribe(Observer observer) {
        observers.add(observer);
    }

    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String videoTitle, String channelTitle, String uploadDate) {
        for (Observer observer : observers) {
            observer.update(videoTitle, channelTitle, uploadDate);
        }
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public List<Observer> getObservers() {
        return observers;
    }
}