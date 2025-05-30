package FarmEquipmentRentalSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AdminNotificationSystem {
    private AdminHomePage adminHomePage;
    private JLabel notificationCountLabel;
    private int notificationCount = 0;
    private Timer notificationTimer;
    private List<Notification> notifications = new ArrayList<>();

    // Constructor
    public AdminNotificationSystem(AdminHomePage adminHomePage) {
        this.adminHomePage = adminHomePage;

        // Initialize notification count label
        notificationCountLabel = new JLabel("0");
        notificationCountLabel.setOpaque(true);
        notificationCountLabel.setBackground(Color.RED);
        notificationCountLabel.setForeground(Color.WHITE);
        notificationCountLabel.setFont(new Font("Arial", Font.BOLD, 10));
        notificationCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        notificationCountLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        notificationCountLabel.setVisible(false); // Hide initially until we have notifications

        // Start periodic notification check (every 30 seconds)
        startNotificationChecker(30000);
    }

    // Get notification count label for display in UI
    public JLabel getNotificationCountLabel() {
        return notificationCountLabel;
    }

    // Start the notification checker timer
    private void startNotificationChecker(long interval) {
        notificationTimer = new Timer(true); // Use daemon timer
        notificationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> checkForNewNotifications());
            }
        }, 0, interval);
    }

    // Stop the notification checker timer
    public void stopNotificationChecker() {
        if (notificationTimer != null) {
            notificationTimer.cancel();
            notificationTimer = null;
        }
    }

    // Check for new notifications from database
    public void checkForNewNotifications() {
        // Check for new users
        checkForNewUsers();

        // Check for new rental requests
        checkForNewRentalRequests();

        // Update the notification count label
        updateNotificationCountLabel();
    }

    // Check for new users registered in the last day
    private void checkForNewUsers() {
        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                String query = "SELECT id, username, full_name, registration_date FROM farmer_users " +
                        "WHERE role = 'farmer' AND registration_date > (NOW() - INTERVAL 1 DAY) " +
                        "ORDER BY registration_date DESC";

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    int userId = rs.getInt("id");
                    String username = rs.getString("username");
                    String fullName = rs.getString("full_name");
                    Timestamp regDate = rs.getTimestamp("registration_date");

                    // Check if this notification already exists
                    boolean exists = false;
                    for (Notification notification : notifications) {
                        if (notification.getType() == NotificationType.NEW_USER &&
                                notification.getId() == userId) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        notifications.add(new Notification(
                                NotificationType.NEW_USER,
                                userId,
                                "New User Registration: " + fullName,
                                "User " + username + " has registered on " + regDate,
                                regDate
                        ));
                    }
                }

                rs.close();
                stmt.close();
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

    // Check for new rental requests
    private void checkForNewRentalRequests() {
        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                String query = "SELECT r.id, u.username, u.full_name, e.name, r.request_date " +
                        "FROM farmer_rentals r " +
                        "JOIN farmer_users u ON r.farmer_id = u.id " +
                        "JOIN farmer_equipment e ON r.equipment_id = e.id " +
                        "WHERE r.status = 'Pending' AND r.request_date > (NOW() - INTERVAL 1 DAY) " +
                        "ORDER BY r.request_date DESC";

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    int rentalId = rs.getInt("id");
                    String username = rs.getString("username");
                    String fullName = rs.getString("full_name");
                    String equipmentName = rs.getString("name");
                    Timestamp requestDate = rs.getTimestamp("request_date");

                    // Check if this notification already exists
                    boolean exists = false;
                    for (Notification notification : notifications) {
                        if (notification.getType() == NotificationType.RENTAL_REQUEST &&
                                notification.getId() == rentalId) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        notifications.add(new Notification(
                                NotificationType.RENTAL_REQUEST,
                                rentalId,
                                "New Rental Request: " + equipmentName,
                                "User " + fullName + " requested to rent " + equipmentName + " on " + requestDate,
                                requestDate
                        ));
                    }
                }

                rs.close();
                stmt.close();
            } catch (SQLException ex) {
                System.err.println("Error checking for rental requests: " + ex.getMessage());
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

    // Update notification count label
    private void updateNotificationCountLabel() {
        notificationCount = notifications.size();
        notificationCountLabel.setText(String.valueOf(notificationCount));
        notificationCountLabel.setVisible(notificationCount > 0);
    }

    // Show notification dialog
    public void showNotificationDialog() {
        if (notifications.isEmpty()) {
            JOptionPane.showMessageDialog(adminHomePage,
                    "No new notifications.",
                    "Notifications",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create notification dialog
        JDialog notificationDialog = new JDialog(adminHomePage, "Notifications", true);
        notificationDialog.setLayout(new BorderLayout());
        notificationDialog.setSize(500, 400);

        // Create table model for notifications
        String[] columns = {"Type", "Title", "Time"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Populate table with notifications
        for (Notification notification : notifications) {
            model.addRow(new Object[]{
                    notification.getType().toString(),
                    notification.getTitle(),
                    notification.getTimestamp()
            });
        }

        // Create notification table
        JTable notificationTable = new JTable(model);
        notificationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notificationTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        notificationTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        notificationTable.getColumnModel().getColumn(2).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(notificationTable);
        notificationDialog.add(scrollPane, BorderLayout.CENTER);

        // Message panel for displaying selected notification details
        JTextArea messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setRows(5);
        messageArea.setBorder(BorderFactory.createTitledBorder("Notification Details"));

        notificationDialog.add(messageArea, BorderLayout.SOUTH);

        // Selection listener for showing details
        notificationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = notificationTable.getSelectedRow();
                if (selectedRow >= 0) {
                    Notification notification = notifications.get(selectedRow);
                    messageArea.setText(notification.getMessage());
                }
            }
        });

        // Add buttons for actions
        JPanel buttonPanel = new JPanel();
        JButton viewButton = new JButton("View/Action");
        JButton dismissButton = new JButton("Dismiss");
        JButton dismissAllButton = new JButton("Dismiss All");

        buttonPanel.add(viewButton);
        buttonPanel.add(dismissButton);
        buttonPanel.add(dismissAllButton);

        notificationDialog.add(buttonPanel, BorderLayout.NORTH);

        // Action for view button
        viewButton.addActionListener(e -> {
            int selectedRow = notificationTable.getSelectedRow();
            if (selectedRow >= 0) {
                Notification notification = notifications.get(selectedRow);
                handleNotificationAction(notification);
                notificationDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(notificationDialog,
                        "Please select a notification first",
                        "No Selection",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Action for dismiss button
        dismissButton.addActionListener(e -> {
            int selectedRow = notificationTable.getSelectedRow();
            if (selectedRow >= 0) {
                notifications.remove(selectedRow);
                model.removeRow(selectedRow);
                messageArea.setText("");
                updateNotificationCountLabel();

                if (notifications.isEmpty()) {
                    notificationDialog.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(notificationDialog,
                        "Please select a notification first",
                        "No Selection",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Action for dismiss all button
        dismissAllButton.addActionListener(e -> {
            notifications.clear();
            updateNotificationCountLabel();
            notificationDialog.dispose();
        });

        // Double-click listener for table rows
        notificationTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = notificationTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        Notification notification = notifications.get(selectedRow);
                        handleNotificationAction(notification);
                        notificationDialog.dispose();
                    }
                }
            }
        });

        notificationDialog.setLocationRelativeTo(adminHomePage);
        notificationDialog.setVisible(true);
    }

    // Handle notification action based on type
    private void handleNotificationAction(Notification notification) {
        switch (notification.getType()) {
            case NEW_USER:
                // For new users, we might show user details or do nothing
                JOptionPane.showMessageDialog(adminHomePage,
                        "New user registered: " + notification.getMessage(),
                        notification.getTitle(),
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case RENTAL_REQUEST:
                // For rental requests, switch to the rental tab and select the request
                adminHomePage.switchToRentalTab();
                adminHomePage.selectRentalRequest(notification.getId());
                break;

            // Add more cases as needed for other notification types
        }
    }

    // Enumeration for notification types
    public enum NotificationType {
        NEW_USER("New User"),
        RENTAL_REQUEST("Rental Request"),
        MAINTENANCE("Maintenance"),
        SYSTEM("System");

        private final String displayName;

        NotificationType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    // Notification class to store notification details
    public class Notification {
        private NotificationType type;
        private int id;
        private String title;
        private String message;
        private Timestamp timestamp;

        public Notification(NotificationType type, int id, String title, String message, Timestamp timestamp) {
            this.type = type;
            this.id = id;
            this.title = title;
            this.message = message;
            this.timestamp = timestamp;
        }

        public NotificationType getType() {
            return type;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }
    }
}