import model.YouTubeAPIService;
import view.MenuGUI;

public class Main {
    public static void main(String[] args) {
        // Create and display the menu
        YouTubeAPIService apiService = new YouTubeAPIService();
        MenuGUI menu = new MenuGUI(apiService);
        menu.setVisible(true);
    }
}