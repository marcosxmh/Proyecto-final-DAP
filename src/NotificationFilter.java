public abstract class NotificationFilter implements Observer {
    protected Observer wrappedObserver;

    public NotificationFilter(Observer wrappedObserver) {
        this.wrappedObserver = wrappedObserver;
    }

    @Override
    public void update(String videoTitle, String channelTitle, String uploadDate) {
        wrappedObserver.update(videoTitle, channelTitle, uploadDate);
    }

    @Override
    public void clearLastNotifications() {
        wrappedObserver.clearLastNotifications();
    }
}
