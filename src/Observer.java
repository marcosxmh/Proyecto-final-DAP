public interface Observer {
    void update(String videoTitle, String channelTitle, String uploadDate);
    void clearLastNotifications();
}
