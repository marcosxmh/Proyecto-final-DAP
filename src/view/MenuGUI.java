package view;

import controller.YouTubeController;
import model.YouTubeAPIService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MenuGUI extends JFrame {
    private YouTubeAPIService apiService;
    private List<UserGUI> userGUIs = new ArrayList<>();

    public MenuGUI(YouTubeAPIService apiService) {
        this.apiService = apiService;

        // Set up the frame
        setTitle("Youtube Notification Program");
        setSize(350, 250);
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

        // Add action listeners
        createUserButton.addActionListener(e -> {
            String userName = JOptionPane.showInputDialog("Enter your name:");
            if (userName == null || userName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a valid name");
                return;
            }

            SwingUtilities.invokeLater(() -> {
                for (UserGUI userGUI : userGUIs) {
                    if (userGUI.getUserName().equals(userName)) {
                        JOptionPane.showMessageDialog(null, "User already exists");
                        return;
                    }
                }
                UserGUI userGUI = new UserGUI(userName, apiService);
                new YouTubeController(apiService, userGUI);
                userGUI.setVisible(true);
                userGUIs.add(userGUI);
            });
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

        // Add panel to frame
        add(panel);
    }
}