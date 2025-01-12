package view;

import model.Subscriber;
import model.YouTubeAPIService;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

import model.YouTubeChannel;


public class UserGUI extends JFrame {
    private Subscriber subscriber;
    private JTextField searchField;
    private JButton searchButton;
    private JList<String> channelList;
    private DefaultListModel<String> listModel;
    private YouTubeAPIService apiService;
    private JButton subscribeButton;
    private JButton unsubscribeButton;
    private JButton refreshButton;
    private JButton recommendButton;
    private JTextArea notificationArea;
    private JTextArea searchHistoryArea;
    private JTextArea recommendationArea; // Nuevo área para recomendaciones

    public UserGUI(String userName, YouTubeAPIService apiService) {
        subscriber = new Subscriber(userName);
        this.apiService = apiService;

        setTitle("User: " + userName);
        setSize(600, 600); // Ajustar el tamaño para acomodar todas las áreas
        setLocationRelativeTo(null);

        // Crear componentes
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        subscribeButton = new JButton("Subscribe");
        unsubscribeButton = new JButton("Unsubscribe");
        recommendButton = new JButton("Recommend Similar Channels");
        refreshButton = new JButton("Send Notifications");
        listModel = new DefaultListModel<>();
        channelList = new JList<>(listModel);
        notificationArea = new JTextArea(5, 30);
        notificationArea.setEditable(false);
        searchHistoryArea = new JTextArea(5, 30);
        searchHistoryArea.setEditable(false);
        recommendationArea = new JTextArea(5, 30); // Inicializar área de recomendaciones
        recommendationArea.setEditable(false);

        // Configuración de diseño
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Search for channels:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(subscribeButton);
        topPanel.add(unsubscribeButton);
        topPanel.add(recommendButton);
        add(topPanel, BorderLayout.NORTH);

        JScrollPane channelScrollPane = new JScrollPane(channelList);
        channelScrollPane.setPreferredSize(new Dimension(100, 75));
        add(channelScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        // Área de notificaciones
        JScrollPane notificationScrollPane = new JScrollPane(notificationArea);
        notificationScrollPane.setPreferredSize(new Dimension(400, 150));
        bottomPanel.add(new JLabel("Notifications:"));
        bottomPanel.add(notificationScrollPane);

        // Área de historial de búsquedas
        JScrollPane searchHistoryScrollPane = new JScrollPane(searchHistoryArea);
        searchHistoryScrollPane.setPreferredSize(new Dimension(400, 100));
        bottomPanel.add(new JLabel("Search History:"));
        bottomPanel.add(searchHistoryScrollPane);

        // Área de recomendaciones
        JScrollPane recommendationScrollPane = new JScrollPane(recommendationArea);
        recommendationScrollPane.setPreferredSize(new Dimension(400, 100));
        bottomPanel.add(new JLabel("Recommended Channels:"));
        bottomPanel.add(recommendationScrollPane);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        bottomPanel.add(buttonPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        initListeners();
    }

    private void initListeners() {
        searchButton.addActionListener(e -> {
            String searchQuery = searchField.getText();
            if (searchQuery.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a search query");
                return;
            }
            List<String> channels = apiService.getAvailableChannels(searchQuery);
            listModel.clear();
            for (String channel : channels) {
                listModel.addElement(channel);
            }
            subscriber.addSearchHistory(searchQuery);
            subscriber.viewSearchHistoryInGUI(searchHistoryArea);
        });

        subscribeButton.addActionListener(e -> {
            String selectedChannel = channelList.getSelectedValue();
            if (selectedChannel != null) {
                if (!apiService.channelExists(selectedChannel)) {
                    apiService.addChannel(new YouTubeChannel(selectedChannel));
                }
                YouTubeChannel channel = apiService.getChannel(selectedChannel);
                channel.subscribe(subscriber);
                JOptionPane.showMessageDialog(this, "Subscribed to " + selectedChannel);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a channel first.");
            }
        });

        unsubscribeButton.addActionListener(e -> {
            String selectedChannel = channelList.getSelectedValue();
            if (selectedChannel != null) {
                YouTubeChannel channel = apiService.getChannel(selectedChannel);
                if (channel != null) {
                    channel.unsubscribe(subscriber);
                    JOptionPane.showMessageDialog(this, "Unsubscribed from " + selectedChannel);
                } else {
                    JOptionPane.showMessageDialog(this, "Channel does not exist.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a channel first.");
            }
        });

        refreshButton.addActionListener(e -> {
            for (YouTubeChannel channel : apiService.getChannels()) {
                apiService.fetchVideos(channel);
            }
            updateNotificationArea();
            subscriber.clearLastNotifications();
        });

        recommendButton.addActionListener(e -> {
            String selectedChannel = channelList.getSelectedValue();
            if (selectedChannel != null) {
                List<String> recommendations = apiService.getSimilarChannelsByDescription(selectedChannel);
                if (recommendations.isEmpty()) {
                    recommendationArea.setText("No similar channels found.");
                } else {
                    showRecommendations(recommendations);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a channel first.");
            }
        });

        channelList.addListSelectionListener(e -> {
            boolean isSelected = !channelList.isSelectionEmpty();
            subscribeButton.setEnabled(isSelected);
            unsubscribeButton.setEnabled(isSelected);
        });
    }

    public void showRecommendations(List<String> recommendations) {
        recommendationArea.setText(""); // Limpiar el área antes de agregar contenido
        for (String channel : recommendations) {
            recommendationArea.append(channel + "\n");
        }
    }

    public String getUserName() {
        return subscriber.getName();
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JButton getSubscribeButton() {
        return subscribeButton;
    }

    public JButton getUnsubscribeButton() {
        return unsubscribeButton;
    }

    public JButton getRecommendButton() {
        return recommendButton;
    }

    public String getSearchQuery() {
        return searchField.getText();
    }

    public String getSelectedChannel() {
        return channelList.getSelectedValue();
    }

    public void updateChannelList(List<String> channels) {
        listModel.clear();
        for (String channel : channels) {
            listModel.addElement(channel);
        }
    }

    public void updateNotificationArea() {
        notificationArea.setText("");
        for (Map.Entry<String, String> entry : subscriber.getLastVideos().entrySet()) {
            notificationArea.append("Video: " + entry.getKey() + " | Channel: " + entry.getValue() + "\n");
        }
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
