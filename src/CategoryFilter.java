import java.util.List;

public class CategoryFilter extends NotificationFilter {
    private List<String> categories;

    public CategoryFilter(Observer wrappedObserver, List<String> categories) {
        super(wrappedObserver);
        this.categories = categories;
    }

    @Override
    public void update(String videoTitle, String channelTitle, String uploadDate) {
        // Placeholder: Check categories; assuming the channel or video has metadata for category
        for (String category : categories) {
            if (channelTitle.toLowerCase().contains(category.toLowerCase())) { // Example logic
                super.update(videoTitle, channelTitle, uploadDate);
                return;
            }
        }
    }
}
