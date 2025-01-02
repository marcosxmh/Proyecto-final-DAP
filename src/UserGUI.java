import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class UserGUI extends JFrame {
    private Observer subscriber;
    private JTextField searchField;
    private JButton searchButton;
    private JList<String> channelList;
    private DefaultListModel<String> listModel;
    private YouTubeAPIService apiService;
    private JButton subscribeButton;
    private JButton unsubscribeButton;
    private JTextArea notificationArea;
    private JComboBox<String> filterTypeComboBox;
    private JTextField filterInputField;
    private JButton applyFilterButton;
    private JPanel subscribedChannelsPanel;

    public UserGUI(String userName, YouTubeAPIService apiService) {
        subscriber = new Subscriber(userName);
        this.apiService = apiService;

        setTitle("User: " + userName);
        setSize(600, 600);
        setLocationRelativeTo(null);

        // Create components
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        listModel = new DefaultListModel<>();
        channelList = new JList<>(listModel);
        subscribeButton = new JButton("Subscribe");
        unsubscribeButton = new JButton("Unsubscribe");
        notificationArea = new JTextArea(5, 30);
        notificationArea.setEditable(false);
        subscribedChannelsPanel = new JPanel();
        subscribedChannelsPanel.setLayout(new BoxLayout(subscribedChannelsPanel, BoxLayout.Y_AXIS));

        // Recommendation components
        JButton recommendPopularButton = new JButton("Recommend Popular");
        JButton recommendSimilarButton = new JButton("Recommend Similar");
        JTextArea recommendationArea = new JTextArea(5, 30);
        recommendationArea.setEditable(false);

        // Filter components
        filterTypeComboBox = new JComboBox<>(new String[]{"Keyword", "Category"});
        filterInputField = new JTextField(15);
        applyFilterButton = new JButton("Apply Filter");

        // Set up layout
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Search for channels:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(channelList);
        scrollPane.setPreferredSize(new Dimension(100, 75));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(subscribeButton);
        buttonPanel.add(unsubscribeButton);
        bottomPanel.add(buttonPanel);

        JPanel notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.add(new JLabel("Notifications:"), BorderLayout.NORTH);
        JScrollPane notificationScrollPane = new JScrollPane(notificationArea);
        notificationScrollPane.setPreferredSize(new Dimension(400, 150));
        notificationPanel.add(notificationScrollPane, BorderLayout.CENTER);
        bottomPanel.add(notificationPanel);

        JPanel recommendationPanel = new JPanel(new BorderLayout());
        recommendationPanel.add(new JLabel("Recommendations:"), BorderLayout.NORTH);
        JScrollPane recommendationScrollPane = new JScrollPane(recommendationArea);
        recommendationScrollPane.setPreferredSize(new Dimension(400, 150));
        recommendationPanel.add(recommendPopularButton, BorderLayout.WEST);
        recommendationPanel.add(recommendSimilarButton, BorderLayout.EAST);
        recommendationPanel.add(recommendationScrollPane, BorderLayout.CENTER);
        bottomPanel.add(recommendationPanel);

        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Filter Type:"));
        filterPanel.add(filterTypeComboBox);
        filterPanel.add(filterInputField);
        filterPanel.add(applyFilterButton);
        bottomPanel.add(filterPanel);

        JScrollPane subscribedChannelsScrollPane = new JScrollPane(subscribedChannelsPanel);
        subscribedChannelsScrollPane.setPreferredSize(new Dimension(400, 150));
        bottomPanel.add(new JLabel("Subscribed Channels:"));
        bottomPanel.add(subscribedChannelsScrollPane);

        add(bottomPanel, BorderLayout.SOUTH);

        // Add action listener to search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });

        // Add list selection listener to channel list
        channelList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && channelList.getSelectedValue() != null) {
                subscribeButton.setEnabled(true);
                unsubscribeButton.setEnabled(true);
            } else {
                subscribeButton.setEnabled(false);
                unsubscribeButton.setEnabled(false);
            }
        });

        // Add action listeners to subscribe and unsubscribe buttons
        subscribeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedChannel = channelList.getSelectedValue();
                if (selectedChannel != null) {
                    if (!apiService.channelExists(selectedChannel)) {
                        YouTubeChannel channel = new YouTubeChannel(selectedChannel);
                        apiService.addChannel(channel);
                    }
                    if (apiService.getChannel(selectedChannel).getObservers().contains(subscriber)) {
                        JOptionPane.showMessageDialog(null, "You are already subscribed to " + selectedChannel);
                        return;
                    }
                    apiService.getChannel(selectedChannel).subscribe(subscriber);
                    updateSubscribedChannels();
                    JOptionPane.showMessageDialog(null, "Subscribed to " + selectedChannel);
                }
            }
        });

        unsubscribeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedChannel = channelList.getSelectedValue();
                if (selectedChannel != null) {
                    if (!apiService.channelExists(selectedChannel)) {
                        YouTubeChannel channel = new YouTubeChannel(selectedChannel);
                        apiService.addChannel(channel);
                    }
                    if (!apiService.getChannel(selectedChannel).getObservers().contains(subscriber)) {
                        JOptionPane.showMessageDialog(null, "You are not subscribed to " + selectedChannel);
                        return;
                    }
                    apiService.getChannel(selectedChannel).unsubscribe(subscriber);
                    updateSubscribedChannels();
                    JOptionPane.showMessageDialog(null, "Unsubscribed from " + selectedChannel);
                }
            }
        });

        recommendPopularButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RecommendationContext context = new RecommendationContext();
                context.setStrategy(new PopularChannelsStrategy());
                List<String> recommendations = context.getRecommendations(apiService.getChannels(), apiService);
                displayRecommendations(recommendations, recommendationArea);
            }
        });

        recommendSimilarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RecommendationContext context = new RecommendationContext();
                context.setStrategy(new SimilarChannelsStrategy());
                List<String> recommendations = context.getRecommendations(apiService.getChannels(), apiService);
                displayRecommendations(recommendations, recommendationArea);
            }
        });

        applyFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filterType = (String) filterTypeComboBox.getSelectedItem();
                String filterValue = filterInputField.getText();

                if (filterValue.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a filter value.");
                    return;
                }

                if ("Keyword".equals(filterType)) {
                    subscriber = new Subscriber(((Subscriber) subscriber).getName()); // Reset subscriber
                    subscriber = new KeywordFilter(subscriber, List.of(filterValue.split(",")));
                } else if ("Category".equals(filterType)) {
                    subscriber = new Subscriber(((Subscriber) subscriber).getName()); // Reset subscriber
                    subscriber = new CategoryFilter(subscriber, List.of(filterValue.split(",")));
                }

                JOptionPane.showMessageDialog(null, "Filter applied: " + filterType + " -> " + filterValue);
            }
        });

        // Initially disable buttons
        subscribeButton.setEnabled(false);
        unsubscribeButton.setEnabled(false);

        updateSubscribedChannels();
    }

    public String getUserName() {
        return ((Subscriber) subscriber).getName();
    }

    public Observer getSubscriber() {
        return subscriber;
    }

    public void updateNotificationArea() {
        notificationArea.setText("");
        for (Map.Entry<String, String> entry : ((Subscriber) subscriber).getLastVideos().entrySet()) {
            notificationArea.append("Video: " + entry.getKey() + " | Channel: " + entry.getValue() + "\n");
        }
    }

    private void displayRecommendations(List<String> recommendations, JTextArea area) {
        area.setText("");
        if (recommendations.isEmpty()) {
            area.append("No recommendations available.\n");
        } else {
            for (String channel : recommendations) {
                area.append(channel + "\n");
            }
        }
    }

    private void updateSubscribedChannels() {
        subscribedChannelsPanel.removeAll();
        for (YouTubeChannel channel : apiService.getChannels()) {
            if (channel.getObservers().contains(subscriber)) {
                try {
                    String channelName = channel.getSearchQuery();
                    URL imageUrl = new URL("https://yt3.ggpht.com/a-/" + channelName + "/photo.jpg"); // Placeholder for actual image URL
                    ImageIcon channelIcon = new ImageIcon(imageUrl);
                    JLabel channelLabel = new JLabel(channelName, channelIcon, JLabel.LEFT);
                    subscribedChannelsPanel.add(channelLabel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        subscribedChannelsPanel.revalidate();
        subscribedChannelsPanel.repaint();
    }
}
