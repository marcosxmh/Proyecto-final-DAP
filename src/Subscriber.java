import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TreeMap;

public class Subscriber implements Observer {
    private String name;
    private HashMap<String,String> lastVideos = new HashMap<String, String>();

    public Subscriber(String name) {
        this.name = name;
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

}
