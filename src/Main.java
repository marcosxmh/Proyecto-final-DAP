import model.YouTubeAPIService;
import view.MenuGUI;
import model.UsageStats;

public class Main {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            UsageStats.getInstance().endSession();
            System.out.println("Session ended. Usage stats:\n" + UsageStats.getInstance());
        }));

        YouTubeAPIService apiService = new YouTubeAPIService();
        MenuGUI menu = new MenuGUI(apiService);
        menu.setVisible(true);
    }

}