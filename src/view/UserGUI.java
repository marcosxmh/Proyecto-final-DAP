package view;

import model.Subscriber;
import model.YouTubeAPIService;
import model.YouTubeChannel;
import model.ChannelDetails;


import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
    private JTextArea notificationArea;
    private JButton viewDetailsButton;
    private JTextArea searchHistoryArea;
    private JButton playVideoButton;


    public UserGUI(String userName, YouTubeAPIService apiService) {
        subscriber = new Subscriber(userName);
        this.apiService = apiService;

        setTitle("User: " + userName);
        setSize(450, 400);
        setLocationRelativeTo(null);

        // Create components
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        subscribeButton = new JButton("Subscribe");
        unsubscribeButton = new JButton("Unsubscribe");
        listModel = new DefaultListModel<>();
        channelList = new JList<>(listModel);
        refreshButton = new JButton("Send Notifications");
        searchHistoryArea = new JTextArea(5, 30);
        notificationArea = new JTextArea(5, 30);
        notificationArea.setEditable(false);
        viewDetailsButton = new JButton("View Channel Details");
        playVideoButton = new JButton("Play Video");

        // Inicialización de searchHistoryArea
        searchHistoryArea = new JTextArea(4, 25);
        searchHistoryArea.setEditable(false);
        JScrollPane searchHistoryScrollPane = new JScrollPane(searchHistoryArea);
        searchHistoryScrollPane.setPreferredSize(new Dimension(300, 100));
        JPanel searchHistoryPanel = new JPanel(new BorderLayout());
        searchHistoryPanel.add(new JLabel("Search History:"), BorderLayout.NORTH);
        searchHistoryPanel.add(searchHistoryScrollPane, BorderLayout.CENTER);

        // Declaración e inicialización de bottomPanel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        // Añadir searchHistoryPanel a bottomPanel
        bottomPanel.add(searchHistoryPanel);

        // Añadir bottomPanel al layout principal
        add(bottomPanel, BorderLayout.SOUTH);

        // Set up layout
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Search for channels:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(subscribeButton);
        topPanel.add(unsubscribeButton);
        add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(channelList);
        scrollPane.setPreferredSize(new Dimension(100, 75));
        add(scrollPane, BorderLayout.CENTER);


        //Panel inferior de Botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewDetailsButton); // Añadir el botón de detalles al panel de botones
        buttonPanel.add(playVideoButton);
        bottomPanel.add(buttonPanel);

        JPanel notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.add(new JLabel("Notifications and Channel Details:"), BorderLayout.NORTH);
        JScrollPane notificationScrollPane = new JScrollPane(notificationArea);
        notificationScrollPane.setPreferredSize(new Dimension(400, 150));
        notificationPanel.add(notificationScrollPane, BorderLayout.CENTER);
        bottomPanel.add(notificationPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        // Add action listener BOTON DE BUSQUEDA
        searchButton.addActionListener(e -> {
            String searchQuery = searchField.getText();
            if (searchQuery.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a search query");
                return;
            }
            List<String> channels = apiService.getAvailableChannels(searchQuery);
            listModel.clear();
            for (String channel : channels) {
                listModel.addElement(channel);
            }
        });

        // Add list selection listener to LISTA DE CANALES
        channelList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && channelList.getSelectedValue() != null) {
                subscribeButton.setEnabled(true);
                unsubscribeButton.setEnabled(true);
            } else {
                subscribeButton.setEnabled(false);
                unsubscribeButton.setEnabled(false);
            }
        });

        // Add action listener BOTON DE HISTORIAL
        searchButton.addActionListener(e -> {
            String searchQuery = searchField.getText();
            if (!searchQuery.isEmpty()) {
                subscriber.addSearchHistory(searchQuery);  // Añadir al historial
                subscriber.viewSearchHistoryInGUI(searchHistoryArea);  // Mostrar el historial
            } else {
                System.out.println("Search query is empty.");
            }
        });

        // Add action listener to BOTON PARA MANDAR NOTIFICACIONES
        refreshButton.addActionListener(e -> {
            for (YouTubeChannel channel : apiService.getChannels()) {
                apiService.fetchVideos(channel);
            }
            updateNotificationArea();
            subscriber.clearLastNotifications();
        });

        // Add action listener to BOTON PARA MOSTRAR DETALLES DEL CANAL
        viewDetailsButton.addActionListener(e -> viewChannelDetails());

        // Add action listener to BOTON PARA REPRODUCIR VIDEO
        playVideoButton.addActionListener(e -> {
            String selectedChannel = channelList.getSelectedValue();
            if (selectedChannel != null) {
                String videoName = JOptionPane.showInputDialog("Enter the Video Name for channel: " + selectedChannel);
                if (videoName != null && !videoName.trim().isEmpty()) {
                    String videoId = apiService.getVideoIdByName(videoName);
                    if (videoId != null) {
                        String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                        openWebpage(videoUrl);
                    } else {
                        JOptionPane.showMessageDialog(null, "Video not found.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a valid Video Name.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a channel first.");
            }
        });

        // Initially disable buttons
        subscribeButton.setEnabled(false);
        unsubscribeButton.setEnabled(false);
    }

    private void viewChannelDetails() {
        String selectedChannel = channelList.getSelectedValue();
        if (selectedChannel != null) {
            String channelId = apiService.getChannelIdByName(selectedChannel);
            if (channelId != null) {
                JSONObject channelInfo = apiService.getChannelInfo(channelId);
                if (channelInfo != null) {
                    ChannelDetails details = new ChannelDetails(
                            channelInfo.getString("name"),
                            channelInfo.getString("description"),
                            channelInfo.getInt("subscribers")
                    );

                    JSONArray socialLinks = channelInfo.getJSONArray("socialLinks");
                    for (int i = 0; i < socialLinks.length(); i++) {
                        JSONObject socialLink = socialLinks.getJSONObject(i);
                        details.addSocialLink(socialLink.getString("platform"), socialLink.getString("link"));
                    }

                    displayChannelDetails(details);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al obtener los detalles del canal.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo encontrar el ID del canal.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, selecciona un canal primero.");
        }
    }

    private void displayChannelDetails(ChannelDetails channel) {
        // Limpiar el área de detalles
        notificationArea.setText("");

        // Mostrar detalles del canal en el área de texto
        notificationArea.append("Channel Name: " + channel.getChannelName() + "\n");
        notificationArea.append("Description: " + channel.getDescription() + "\n");
        notificationArea.append("Subscribers: " + channel.getSubscribers() + "\n");

        // Mostrar redes sociales si están disponibles
        if (channel.getSocialLinks() != null && !channel.getSocialLinks().isEmpty()) {
            notificationArea.append("Social Media: \n");

            // Iterar sobre las redes sociales y mostrar cada entrada
            for (Map.Entry<String, String> entry : channel.getSocialLinks().entrySet()) {
                notificationArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
            }
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

    public static void openWebpage(String urlString) {
        try {
            URI uri = new URI(urlString);
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(uri);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}