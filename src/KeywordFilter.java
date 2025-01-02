import java.util.List;

public class KeywordFilter extends NotificationFilter {
    private List<String> keywords;

    public KeywordFilter(Observer wrappedObserver, List<String> keywords) {
        super(wrappedObserver);
        this.keywords = keywords;
    }

    @Override
    public void update(String videoTitle, String channelTitle, String uploadDate) {
        for (String keyword : keywords) {
            if (videoTitle.toLowerCase().contains(keyword.toLowerCase())) {
                super.update(videoTitle, channelTitle, uploadDate);
                return;
            }
        }
    }
}
