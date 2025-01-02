import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class UserGUI extends JFrame {
    private Subscriber subscriber;
    private JTextField searchField;
    private JButton searchButton;
    private JList<String> channelList;
    private DefaultListModel<String> listModel;
    private YouTubeAPIService apiService;
    private JButton subscribeButton;
    private JButton unsubscribeButton;
    private JTextArea notificationArea;

    public UserGUI(String userName, YouTubeAPIService apiService) {
        subscriber = new Subscriber(userName);
        this.apiService = apiService;

        setTitle("User: " + userName);
        setSize(450, 350);
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
                    JOptionPane.showMessageDialog(null, "Unsubscribed from " + selectedChannel);
                }
            }
        });

        // Initially disable buttons
        subscribeButton.setEnabled(false);
        unsubscribeButton.setEnabled(false);
    }

    public String getUserName() {
        return subscriber.getName();
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void updateNotificationArea() {
        notificationArea.setText("");
        for (Map.Entry<String, String> entry : subscriber.getLastVideos().entrySet()) {
            notificationArea.append("Video: " + entry.getKey() + " | Channel: " + entry.getValue() + "\n");
        }
    }
}