import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MenuGUI extends JFrame {
    private YouTubeAPIService apiService;
    private List<UserGUI> userGUIs = new ArrayList<UserGUI>();

    public MenuGUI() {
        apiService = new YouTubeAPIService();

        // Set up the frame
        setTitle("Youtube Notification Program");
        setSize(350, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create panel with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding

        // Load and scale the image
        ImageIcon icon = new ImageIcon("resources/youtube-logo-01.png");
        Image image = icon.getImage();
        Image newImage = image.getScaledInstance(250, 150, Image.SCALE_SMOOTH);
        icon = new ImageIcon(newImage);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create button
        JButton createUserButton = new JButton("Create User");
        createUserButton.setPreferredSize(new Dimension(100, 50));

        //Create button "Refresh"
        JButton refreshButton = new JButton("Send Notifications");
        refreshButton.setPreferredSize(new Dimension(100, 50));

        // Add action listeners
        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //ask for user name
                String userName = JOptionPane.showInputDialog("Enter your name:");
                if (userName == null || userName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid name");
                    return;
                }

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        //check if user already exists
                        for (UserGUI userGUI : userGUIs) {
                            if (userGUI.getUserName().equals(userName)) {
                                JOptionPane.showMessageDialog(null, "User already exists");
                                return;
                            }
                        }
                        UserGUI userGUI = new UserGUI(userName, apiService);
                        userGUI.setVisible(true);
                        userGUIs.add(userGUI);
                    }
                });

            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (YouTubeChannel channel : apiService.getChannels()) {
                    apiService.fetchVideos(channel);
                }
                for (UserGUI userGUI : userGUIs) {
                    userGUI.updateNotificationArea();
                    userGUI.getSubscriber().clearLastNotifications();
                }
            }
        });

        // Add components to panel with GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(iconLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(createUserButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(refreshButton, gbc);

        // Add panel to frame
        add(panel);
    }
}