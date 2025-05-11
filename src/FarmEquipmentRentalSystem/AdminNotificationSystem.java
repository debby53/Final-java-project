package FarmEquipmentRentalSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AdminNotificationSystem - A class to handle system notifications for the Admin Dashboard
 */
public class AdminNotificationSystem {
    private JPanel notificationPanel;
    private JList<String> notificationList;
    private DefaultListModel<String> notificationModel;
    private JFrame parentFrame;
    private JDialog notificationDialog;
    private JLabel notificationCountLabel;
    private int notificationCount = 0;
    private Timer notificationChecker;

    private static final int CHECK_INTERVAL = 30000; // Check for new notifications every 30 seconds

    // Store the last checked IDs to avoid duplicate notifications
    private int lastCheckedUserId = 0;
    private int lastCheckedRentalId = 0;

    /**
     * Constructor for the notification system
     * @param parent The parent frame (AdminHomePage)
     */
    public AdminNotificationSystem(JFrame parent) {
        this.parentFrame = parent;
        initComponents();
        startNotificationChecker();
    }

    /**
     * Initialize the notification components
     */
    private void initComponents() {
        // Create the notification panel with a list
        notificationPanel = new JPanel(new BorderLayout());
        notificationModel = new DefaultListModel<>();
        notificationList = new JList<>(notificationModel);
        notificationList.setCellRenderer(new NotificationCellRenderer());

        JScrollPane scrollPane = new JScrollPane(notificationList);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        notificationPanel.add(scrollPane, BorderLayout.CENTER);

        // Create a button panel for the notification dialog
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton markAllReadButton = new JButton("Mark All as Read");
        JButton dismissButton = new JButton("Dismiss");

        buttonPanel.add(markAllReadButton);
        buttonPanel.add(dismissButton);
        notificationPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Create the notification dialog
        notificationDialog = new JDialog(parentFrame, "System Notifications", false);
        notificationDialog.setSize(400, 350);
        notificationDialog.setLayout(new BorderLayout());
        notificationDialog.add(notificationPanel);
        notificationDialog.setLocationRelativeTo(parentFrame);

        // Create notification counter for the admin header
        notificationCountLabel = new JLabel("0");
        notificationCountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        notificationCountLabel.setForeground(Color.WHITE);
        notificationCountLabel.setOpaque(true);
        notificationCountLabel.setBackground(Color.RED);
        notificationCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        notificationCountLabel.setPreferredSize(new Dimension(20, 20));
        notificationCountLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        notificationCountLabel.setVisible(false);

        // Event listeners
        markAllReadButton.addActionListener(e -> {
            notificationModel.clear();
            updateNotificationCount(0);
            notificationDialog.setVisible(false);
        });

        dismissButton.addActionListener(e -> notificationDialog.setVisible(false));

        // Add double-click listener to open relevant section based on notification
        notificationList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleNotificationClick();
                }
            }
        });
    }

    /**
     * Start the timer to check for new notifications periodically
     */
    private void startNotificationChecker() {
        notificationChecker = new Timer(CHECK_INTERVAL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkForNewNotifications();
            }
        });
        notificationChecker.start();
    }

    /**
     * Stop the notification checker timer
     */
    public void stopNotificationChecker() {
        if (notificationChecker != null && notificationChecker.isRunning()) {
            notificationChecker.stop();
        }
    }

    /**
     * Check for new notifications from the database
     */
    public void checkForNewNotifications() {
        checkForNewUsers();
        checkForNewRentalRequests();
        checkForEquipmentStatusChanges();
    }

    /**
     * Check if there are new user sign-ups
     */
    private void checkForNewUsers() {
        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                String query = "SELECT id, username, full_name, registration_date FROM farmer_users " +
                        "WHERE id > ? AND role = 'farmer' " +
                        "ORDER BY id ASC";

                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, lastCheckedUserId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int userId = rs.getInt("id");
                    String username = rs.getString("username");
                    String fullName = rs.getString("full_name");
                    Timestamp regDate = rs.getTimestamp("registration_date");

                    // Add notification
                    String notification = "NEW_USER:" + userId + ":" +
                            DateTimeFormatter.ofPattern("MM/dd HH:mm").format(regDate.toLocalDateTime()) +
                            " - New farmer registration: " + fullName + " (" + username + ")";

                    addNotification(notification);

                    // Update last checked ID
                    if (userId > lastCheckedUserId) {
                        lastCheckedUserId = userId;
                    }
                }

                rs.close();
                ps.close();
            } catch (SQLException ex) {
                System.err.println("Error checking for new users: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Check if there are new rental requests
     */
    private void checkForNewRentalRequests() {
        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                String query = "SELECT r.id, r.request_date, u.username, e.name " +
                        "FROM farmer_rentals r " +
                        "JOIN farmer_users u ON r.farmer_id = u.id " +
                        "JOIN farmer_equipment e ON r.equipment_id = e.id " +
                        "WHERE r.id > ? AND r.status = 'Pending' " +
                        "ORDER BY r.id ASC";

                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, lastCheckedRentalId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int rentalId = rs.getInt("id");
                    String username = rs.getString("username");
                    String equipmentName = rs.getString("name");
                    Timestamp requestDate = rs.getTimestamp("request_date");

                    // Add notification
                    String notification = "NEW_RENTAL:" + rentalId + ":" +
                            DateTimeFormatter.ofPattern("MM/dd HH:mm").format(requestDate.toLocalDateTime()) +
                            " - New rental request: " + equipmentName + " by " + username;

                    addNotification(notification);

                    // Update last checked ID
                    if (rentalId > lastCheckedRentalId) {
                        lastCheckedRentalId = rentalId;
                    }
                }

                rs.close();
                ps.close();
            } catch (SQLException ex) {
                System.err.println("Error checking for new rental requests: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Check if there are equipment status changes that require attention
     */
    private void checkForEquipmentStatusChanges() {
        // This would check for equipment that's scheduled to be returned today
        // Implementation depends on additional database fields for tracking returns
        // For a real implementation, this would query the database for equipment due
        // to be returned based on rental end dates
    }

    /**
     * Add a new notification to the list
     * @param notification The notification text
     */
    public void addNotification(String notification) {
        notificationModel.add(0, notification); // Add at the top
        updateNotificationCount(notificationCount + 1);
    }

    /**
     * Update the notification count badge
     * @param count The new count
     */
    private void updateNotificationCount(int count) {
        notificationCount = count;
        notificationCountLabel.setText(String.valueOf(count));
        notificationCountLabel.setVisible(count > 0);

        // Play sound for new notifications
        if (count > 0) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    /**
     * @return The notification count badge label
     */
    public JLabel getNotificationCountLabel() {
        return notificationCountLabel;
    }

    /**
     * Show the notification dialog
     */
    public void showNotificationDialog() {
        notificationDialog.setVisible(true);
    }

    /**
     * Handle clicking on a notification
     */
    private void handleNotificationClick() {
        int selectedIndex = notificationList.getSelectedIndex();
        if (selectedIndex != -1) {
            String notification = notificationModel.getElementAt(selectedIndex);

            // Parse the notification type and ID
            String[] parts = notification.split(":", 3);
            if (parts.length >= 2) {
                String type = parts[0];
                int id = Integer.parseInt(parts[1]);

                // Remove the clicked notification
                notificationModel.remove(selectedIndex);
                updateNotificationCount(notificationCount - 1);

                // Navigate to the appropriate tab based on notification type
                AdminHomePage adminPage = (AdminHomePage) parentFrame;
                if (type.equals("NEW_USER")) {
                    // Navigate to user management tab (if implemented)
                    JOptionPane.showMessageDialog(parentFrame,
                            "User details would be displayed here.\nUser ID: " + id,
                            "User Details",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (type.equals("NEW_RENTAL")) {
                    // Navigate to rental management tab
                    adminPage.switchToRentalTab();
                    adminPage.selectRentalRequest(id);
                }

                // Close the notification dialog
                notificationDialog.setVisible(false);
            }
        }
    }

    /**
     * Custom cell renderer for notifications with colors based on type
     */
    private class NotificationCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value != null && value instanceof String) {
                String notification = (String) value;

                if (notification.startsWith("NEW_USER:")) {
                    if (!isSelected) {
                        c.setBackground(new Color(230, 255, 230)); // Light green
                    }
                } else if (notification.startsWith("NEW_RENTAL:")) {
                    if (!isSelected) {
                        c.setBackground(new Color(255, 230, 230)); // Light red
                    }
                }

                // Format the visible text to not show the metadata
                String[] parts = notification.split(":", 3);
                if (parts.length >= 3) {
                    setText(parts[2]);
                }
            }

            return c;
        }
    }
}