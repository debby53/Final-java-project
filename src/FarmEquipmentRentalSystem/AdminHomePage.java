package FarmEquipmentRentalSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AdminHomePage extends JFrame {
    private JTabbedPane tabbedPane;
    private String adminUsername;
    // In the constructor, after creating headerPanel


    // Equipment management panel components
    private JPanel equipmentPanel;
    private JTable equipmentTable;
    private DefaultTableModel equipmentTableModel;
    private JButton addEquipmentButton;
    private JButton removeEquipmentButton;
    private JButton updateEquipmentButton;

    // Rental management panel components
    private JPanel rentalPanel;
    private JTable rentalRequestsTable;
    private DefaultTableModel rentalRequestsTableModel;
    private JButton approveRentalButton;
    private JButton rejectRentalButton;

    // Revenue report panel components
    private JPanel revenuePanel;

    public AdminHomePage(String username) {
        this.adminUsername = username;

        setTitle("Farm Equipment Rental System - Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create welcome banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, Admin " + username);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        JButton simulateButton = new JButton("Simulate Events");
        simulateButton.addActionListener(e -> simulateNewUserRegistration());
        headerPanel.add(simulateButton, BorderLayout.EAST);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Initialize panels
        initEquipmentPanel();
        initRentalPanel();
        initRevenuePanel();

        // Add tabs
        tabbedPane.addTab("Manage Equipment", equipmentPanel);
        tabbedPane.addTab("Manage Rentals", rentalPanel);
        tabbedPane.addTab("Revenue Reports", revenuePanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        JLabel statusLabel = new JLabel("System Status: Online");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);

        loadEquipmentData();
        loadRentalRequests();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initEquipmentPanel() {
        equipmentPanel = new JPanel(new BorderLayout());

        // Table for displaying equipment
        String[] columns = {"ID", "Name", "Category", "Daily Rate", "Status", "Description"};
        equipmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };

        equipmentTable = new JTable(equipmentTableModel);
        equipmentTable.getTableHeader().setReorderingAllowed(false);
        equipmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(equipmentTable);
        equipmentPanel.add(scrollPane, BorderLayout.CENTER);

        // Control panel for equipment management
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addEquipmentButton = new JButton("Add Equipment");
        removeEquipmentButton = new JButton("Remove Equipment");
        updateEquipmentButton = new JButton("Update Equipment");

        controlPanel.add(addEquipmentButton);
        controlPanel.add(removeEquipmentButton);
        controlPanel.add(updateEquipmentButton);

        equipmentPanel.add(controlPanel, BorderLayout.SOUTH);

        // Action listeners
        addEquipmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddEquipmentDialog();
            }
        });

        removeEquipmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = equipmentTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int equipmentId = (int) equipmentTable.getValueAt(selectedRow, 0);
                    String equipmentName = (String) equipmentTable.getValueAt(selectedRow, 1);
                    int option = JOptionPane.showConfirmDialog(
                            AdminHomePage.this,
                            "Are you sure you want to remove " + equipmentName + "?",
                            "Confirm Removal",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (option == JOptionPane.YES_OPTION) {
                        if (removeEquipmentFromDatabase(equipmentId)) {
                            loadEquipmentData(); // Refresh table
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(AdminHomePage.this,
                            "Please select an equipment to remove.",
                            "No Selection",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        updateEquipmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = equipmentTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int equipmentId = (int) equipmentTable.getValueAt(selectedRow, 0);
                    String name = (String) equipmentTable.getValueAt(selectedRow, 1);
                    String category = (String) equipmentTable.getValueAt(selectedRow, 2);
                    double rate = (double) equipmentTable.getValueAt(selectedRow, 3);
                    String status = (String) equipmentTable.getValueAt(selectedRow, 4);
                    String description = (String) equipmentTable.getValueAt(selectedRow, 5);

                    showUpdateEquipmentDialog(equipmentId, name, category, rate, status, description);
                } else {
                    JOptionPane.showMessageDialog(AdminHomePage.this,
                            "Please select an equipment to update.",
                            "No Selection",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    private void initRentalPanel() {
        rentalPanel = new JPanel(new BorderLayout());

        // Table for displaying rental requests
        String[] columns = {"Request ID", "Farmer", "Equipment", "Start Date", "End Date", "Status"};
        rentalRequestsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rentalRequestsTable = new JTable(rentalRequestsTableModel);
        rentalRequestsTable.getTableHeader().setReorderingAllowed(false);
        rentalRequestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(rentalRequestsTable);
        rentalPanel.add(scrollPane, BorderLayout.CENTER);

        // Control panel for rental management
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        approveRentalButton = new JButton("Approve Request");
        rejectRentalButton = new JButton("Reject Request");

        controlPanel.add(approveRentalButton);
        controlPanel.add(rejectRentalButton);

        rentalPanel.add(controlPanel, BorderLayout.SOUTH);

        // Action listeners
        approveRentalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = rentalRequestsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int requestId = (int) rentalRequestsTable.getValueAt(selectedRow, 0);
                    String farmerName = (String) rentalRequestsTable.getValueAt(selectedRow, 1);
                    String equipmentName = (String) rentalRequestsTable.getValueAt(selectedRow, 2);

                    int option = JOptionPane.showConfirmDialog(
                            AdminHomePage.this,
                            "Approve rental request for " + equipmentName + " by " + farmerName + "?",
                            "Confirm Approval",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (option == JOptionPane.YES_OPTION) {
                        if (updateRentalStatus(requestId, "Approved")) {
                            JOptionPane.showMessageDialog(AdminHomePage.this,
                                    "Rental request approved successfully!",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            loadRentalRequests(); // Refresh the table
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(AdminHomePage.this,
                            "Please select a rental request to approve.",
                            "No Selection",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        rejectRentalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = rentalRequestsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int requestId = (int) rentalRequestsTable.getValueAt(selectedRow, 0);
                    String farmerName = (String) rentalRequestsTable.getValueAt(selectedRow, 1);
                    String equipmentName = (String) rentalRequestsTable.getValueAt(selectedRow, 2);

                    // Ask for rejection reason
                    String rejectionReason = JOptionPane.showInputDialog(
                            AdminHomePage.this,
                            "Provide reason for rejecting the rental request (optional):",
                            "Rejection Reason",
                            JOptionPane.QUESTION_MESSAGE
                    );

                    // User clicked Cancel
                    if (rejectionReason == null) {
                        return;
                    }

                    int option = JOptionPane.showConfirmDialog(
                            AdminHomePage.this,
                            "Reject rental request for " + equipmentName + " by " + farmerName + "?",
                            "Confirm Rejection",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (option == JOptionPane.YES_OPTION) {
                        if (updateRentalStatus(requestId, "Rejected", rejectionReason)) {
                            JOptionPane.showMessageDialog(AdminHomePage.this,
                                    "Rental request rejected successfully!",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            loadRentalRequests(); // Refresh the table
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(AdminHomePage.this,
                            "Please select a rental request to reject.",
                            "No Selection",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Call this method to add the "View Details" button
        addViewDetailsButton();

        // Optionally, also add the refresh button
        addRefreshButton();
    }

//    private void initRentalPanel() {
//        rentalPanel = new JPanel(new BorderLayout());
//
//        // Table for displaying rental requests
//        String[] columns = {"Request ID", "Farmer", "Equipment", "Start Date", "End Date", "Status"};
//        rentalRequestsTableModel = new DefaultTableModel(columns, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//
//        rentalRequestsTable = new JTable(rentalRequestsTableModel);
//        rentalRequestsTable.getTableHeader().setReorderingAllowed(false);
//        rentalRequestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//
//        JScrollPane scrollPane = new JScrollPane(rentalRequestsTable);
//        rentalPanel.add(scrollPane, BorderLayout.CENTER);
//
//        // Control panel for rental management
//        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        approveRentalButton = new JButton("Approve Request");
//        rejectRentalButton = new JButton("Reject Request");
//
//        controlPanel.add(approveRentalButton);
//        controlPanel.add(rejectRentalButton);
//
//        rentalPanel.add(controlPanel, BorderLayout.SOUTH);
//
//        // Action listeners
//        approveRentalButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                int selectedRow = rentalRequestsTable.getSelectedRow();
//                if (selectedRow >= 0) {
//                    int requestId = (int) rentalRequestsTable.getValueAt(selectedRow, 0);
//                    String farmerName = (String) rentalRequestsTable.getValueAt(selectedRow, 1);
//                    String equipmentName = (String) rentalRequestsTable.getValueAt(selectedRow, 2);
//
//                    int option = JOptionPane.showConfirmDialog(
//                            AdminHomePage.this,
//                            "Approve rental request for " + equipmentName + " by " + farmerName + "?",
//                            "Confirm Approval",
//                            JOptionPane.YES_NO_OPTION,
//                            JOptionPane.QUESTION_MESSAGE
//                    );
//
//                    if (option == JOptionPane.YES_OPTION) {
//                        if (updateRentalStatus(requestId, "Approved")) {
//                            JOptionPane.showMessageDialog(AdminHomePage.this,
//                                    "Rental request approved successfully!",
//                                    "Success",
//                                    JOptionPane.INFORMATION_MESSAGE);
//                            loadRentalRequests(); // Refresh the table
//                        }
//                    }
//                } else {
//                    JOptionPane.showMessageDialog(AdminHomePage.this,
//                            "Please select a rental request to approve.",
//                            "No Selection",
//                            JOptionPane.INFORMATION_MESSAGE);
//                }
//            }
//        });
//
//        rejectRentalButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                int selectedRow = rentalRequestsTable.getSelectedRow();
//                if (selectedRow >= 0) {
//                    int requestId = (int) rentalRequestsTable.getValueAt(selectedRow, 0);
//                    String farmerName = (String) rentalRequestsTable.getValueAt(selectedRow, 1);
//                    String equipmentName = (String) rentalRequestsTable.getValueAt(selectedRow, 2);
//
//                    // Ask for rejection reason
//                    String rejectionReason = JOptionPane.showInputDialog(
//                            AdminHomePage.this,
//                            "Provide reason for rejecting the rental request (optional):",
//                            "Rejection Reason",
//                            JOptionPane.QUESTION_MESSAGE
//                    );
//
//                    // User clicked Cancel
//                    if (rejectionReason == null) {
//                        return;
//                    }
//
//                    int option = JOptionPane.showConfirmDialog(
//                            AdminHomePage.this,
//                            "Reject rental request for " + equipmentName + " by " + farmerName + "?",
//                            "Confirm Rejection",
//                            JOptionPane.YES_NO_OPTION,
//                            JOptionPane.WARNING_MESSAGE
//                    );
//
//                    if (option == JOptionPane.YES_OPTION) {
//                        if (updateRentalStatus(requestId, "Rejected", rejectionReason)) {
//                            JOptionPane.showMessageDialog(AdminHomePage.this,
//                                    "Rental request rejected successfully!",
//                                    "Success",
//                                    JOptionPane.INFORMATION_MESSAGE);
//                            loadRentalRequests(); // Refresh the table
//                        }
//                    }
//                } else {
//                    JOptionPane.showMessageDialog(AdminHomePage.this,
//                            "Please select a rental request to reject.",
//                            "No Selection",
//                            JOptionPane.INFORMATION_MESSAGE);
//                }
//            }
//        });
//    }

    private void initRevenuePanel() {
        revenuePanel = new JPanel(new BorderLayout());

        JLabel placeholderLabel = new JLabel("Revenue reporting features will be implemented in future versions.", JLabel.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        revenuePanel.add(placeholderLabel, BorderLayout.CENTER);
    }

    private void loadEquipmentData() {
        // Clear existing data
        equipmentTableModel.setRowCount(0);

        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                // Changed table name from "Equipment" to "farmer_equipment"
                String query = "SELECT id, name, category, rental_rate, condition_status, description FROM farmer_equipment";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("category"));
                    row.add(rs.getDouble("rental_rate"));
                    row.add(rs.getString("condition_status"));
                    row.add(rs.getString("description"));

                    equipmentTableModel.addRow(row);
                }

                rs.close();
                stmt.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading equipment data: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
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

//    private void loadRentalRequests() {
//        // Clear existing data
//        rentalRequestsTableModel.setRowCount(0);
//
//        Connection conn = DatabaseConnection.connect();
//        if (conn != null) {
//            try {
//                // Changed table names to match the actual database tables
//                String query = "SELECT r.id as rental_id, u.username, e.name, r.rental_start, r.rental_end, r.status " +
//                        "FROM farmer_rentals r " +
//                        "JOIN farmer_users u ON r.farmer_id = u.id " +
//                        "JOIN farmer_equipment e ON r.equipment_id = e.id " +
//                        "WHERE r.status = 'Pending'";
//
//                Statement stmt = conn.createStatement();
//                ResultSet rs = stmt.executeQuery(query);
//
//                while (rs.next()) {
//                    Vector<Object> row = new Vector<>();
//                    row.add(rs.getInt("rental_id"));
//                    row.add(rs.getString("username"));
//                    row.add(rs.getString("name"));
//                    row.add(rs.getDate("rental_start"));
//                    row.add(rs.getDate("rental_end"));
//                    row.add(rs.getString("status"));
//
//                    rentalRequestsTableModel.addRow(row);
//                }
//
//                rs.close();
//                stmt.close();
//            } catch (SQLException ex) {
//                JOptionPane.showMessageDialog(this, "Error loading rental requests: " + ex.getMessage(),
//                        "Database Error", JOptionPane.ERROR_MESSAGE);
//                ex.printStackTrace();
//            } finally {
//                try {
//                    conn.close();
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//    }
private void loadRentalRequests() {
    // Clear existing data
    rentalRequestsTableModel.setRowCount(0);

    Connection conn = DatabaseConnection.connect();
    if (conn != null) {
        try {
            String query = "SELECT r.id as rental_id, u.username, e.name, r.rental_start, r.rental_end, r.status, " +
                    "r.rejection_reason " +
                    "FROM farmer_rentals r " +
                    "JOIN farmer_users u ON r.farmer_id = u.id " +
                    "JOIN farmer_equipment e ON r.equipment_id = e.id " +
                    "ORDER BY " +
                    "CASE WHEN r.status = 'Pending' THEN 0 " +
                    "     WHEN r.status = 'Approved' THEN 1 " +
                    "     ELSE 2 END, " +
                    "r.rental_start DESC";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("rental_id"));
                row.add(rs.getString("username"));
                row.add(rs.getString("name"));
                row.add(rs.getDate("rental_start"));
                row.add(rs.getDate("rental_end"));
                row.add(rs.getString("status"));

                rentalRequestsTableModel.addRow(row);
            }

            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading rental requests: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
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

    private boolean updateRentalStatus(int rentalId, String status) {
        return updateRentalStatus(rentalId, status, null);
    }

    /**
     * Updates the status of a rental request with an optional rejection reason
     * @param rentalId The ID of the rental request
     * @param status The new status (Approved/Rejected)
     * @param rejectionReason Optional reason for rejection
     * @return true if update was successful
     */
    private boolean updateRentalStatus(int rentalId, String status, String rejectionReason) {
        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                // First get the equipment ID associated with this rental
                int equipmentId = -1;

                String getEquipmentQuery = "SELECT equipment_id FROM farmer_rentals WHERE id = ?";
                PreparedStatement getEquipPs = conn.prepareStatement(getEquipmentQuery);
                getEquipPs.setInt(1, rentalId);
                ResultSet rs = getEquipPs.executeQuery();

                if (rs.next()) {
                    equipmentId = rs.getInt("equipment_id");
                }
                rs.close();
                getEquipPs.close();

                // Update the rental status
                String query = "UPDATE farmer_rentals SET status = ?, rejection_reason = ? WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, status);

                if (rejectionReason != null && !rejectionReason.trim().isEmpty()) {
                    ps.setString(2, rejectionReason);
                } else {
                    ps.setNull(2, java.sql.Types.VARCHAR);
                }

                ps.setInt(3, rentalId);

                int rowsAffected = ps.executeUpdate();
                ps.close();

                // If approved, update equipment status to Reserved
                if (status.equals("Approved") && equipmentId > 0) {
                    String updateEquipQuery = "UPDATE farmer_equipment SET condition_status = 'Reserved' WHERE id = ?";
                    PreparedStatement updatePs = conn.prepareStatement(updateEquipQuery);
                    updatePs.setInt(1, equipmentId);
                    updatePs.executeUpdate();
                    updatePs.close();
                }

                return rowsAffected > 0;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }
    private void showAddEquipmentDialog() {
        // Create dialog
        JDialog addDialog = new JDialog(this, "Add New Equipment", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setSize(400, 350);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Equipment name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Equipment Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        String[] categories = {"Tractor", "Harvester", "Planter", "Sprayer", "Other"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        formPanel.add(categoryCombo, gbc);

        // Daily rate
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Daily Rate ($):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField rateField = new JTextField(10);
        formPanel.add(rateField, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        String[] statuses = {"Available", "Under Maintenance", "Reserved"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        formPanel.add(statusCombo, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextArea descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane, gbc);

        addDialog.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        addDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate inputs
                if (nameField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(addDialog, "Equipment name is required!");
                    return;
                }

                double rate;
                try {
                    rate = Double.parseDouble(rateField.getText().trim());
                    if (rate <= 0) {
                        JOptionPane.showMessageDialog(addDialog, "Daily rate must be greater than zero!");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addDialog, "Please enter a valid daily rate!");
                    return;
                }

                // Save to database
                if (addEquipmentToDatabase(
                        nameField.getText().trim(),
                        (String) categoryCombo.getSelectedItem(),
                        rate,
                        (String) statusCombo.getSelectedItem(),
                        descriptionArea.getText().trim())) {

                    addDialog.dispose();
                    loadEquipmentData(); // Refresh table
                }
            }
        });

        cancelButton.addActionListener(e -> addDialog.dispose());

        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
    }

    private void showUpdateEquipmentDialog(int equipmentId, String name, String category,
                                           double rate, String status, String description) {
        // Create dialog
        JDialog updateDialog = new JDialog(this, "Update Equipment", true);
        updateDialog.setLayout(new BorderLayout());
        updateDialog.setSize(400, 350);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Equipment name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Equipment Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField nameField = new JTextField(name, 20);
        formPanel.add(nameField, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        String[] categories = {"Tractor", "Harvester", "Planter", "Sprayer", "Other"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        categoryCombo.setSelectedItem(category);
        formPanel.add(categoryCombo, gbc);

        // Daily rate
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Daily Rate ($):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField rateField = new JTextField(String.valueOf(rate), 10);
        formPanel.add(rateField, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        String[] statuses = {"Available", "Under Maintenance", "Reserved"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(status);
        formPanel.add(statusCombo, gbc);
        initNotificationSystem();

        // Description
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextArea descriptionArea = new JTextArea(description, 5, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane, gbc);

        updateDialog.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        updateDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate inputs
                if (nameField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(updateDialog, "Equipment name is required!");
                    return;
                }

                double updatedRate;
                try {
                    updatedRate = Double.parseDouble(rateField.getText().trim());
                    if (updatedRate <= 0) {
                        JOptionPane.showMessageDialog(updateDialog, "Daily rate must be greater than zero!");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(updateDialog, "Please enter a valid daily rate!");
                    return;
                }

                // Update database
                if (updateEquipmentInDatabase(
                        equipmentId,
                        nameField.getText().trim(),
                        (String) categoryCombo.getSelectedItem(),
                        updatedRate,
                        (String) statusCombo.getSelectedItem(),
                        descriptionArea.getText().trim())) {

                    updateDialog.dispose();
                    loadEquipmentData(); // Refresh table
                }
            }
        });

        cancelButton.addActionListener(e -> updateDialog.dispose());

        updateDialog.setLocationRelativeTo(this);
        updateDialog.setVisible(true);
    }

    private boolean addEquipmentToDatabase(String name, String category, double dailyRate,
                                           String status, String description) {
        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                // Updated the table name and column names to match the actual database schema
                String query = "INSERT INTO farmer_equipment (name, category, rental_rate, condition_status, description) " +
                        "VALUES (?, ?, ?, ?, ?)";

                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, name);
                ps.setString(2, category);
                ps.setDouble(3, dailyRate);
                ps.setString(4, status);
                ps.setString(5, description);

                int rowsAffected = ps.executeUpdate();
                ps.close();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Equipment added successfully!");
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add equipment.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    private boolean removeEquipmentFromDatabase(int equipmentId) {
        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                // First check if equipment is being rented or has pending rentals
                String checkQuery = "SELECT COUNT(*) FROM farmer_rentals WHERE equipment_id = ? AND status IN ('Approved', 'Pending')";
                PreparedStatement checkPs = conn.prepareStatement(checkQuery);
                checkPs.setInt(1, equipmentId);
                ResultSet rs = checkPs.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot remove this equipment as it has active or pending rentals.",
                            "Removal Error",
                            JOptionPane.ERROR_MESSAGE);
                    rs.close();
                    checkPs.close();
                    return false;
                }
                rs.close();
                checkPs.close();

                // If no active rentals, proceed with deletion
                String deleteQuery = "DELETE FROM farmer_equipment WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(deleteQuery);
                ps.setInt(1, equipmentId);

                int rowsAffected = ps.executeUpdate();
                ps.close();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Equipment removed successfully!");
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove equipment.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    private boolean updateEquipmentInDatabase(int equipmentId, String name, String category,
                                              double dailyRate, String status, String description) {
        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                String query = "UPDATE farmer_equipment SET name = ?, category = ?, rental_rate = ?, " +
                        "condition_status = ?, description = ? WHERE id = ?";

                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, name);
                ps.setString(2, category);
                ps.setDouble(3, dailyRate);
                ps.setString(4, status);
                ps.setString(5, description);
                ps.setInt(6, equipmentId);

                int rowsAffected = ps.executeUpdate();
                ps.close();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Equipment updated successfully!");
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update equipment.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminHomePage("admin"));
    }
    private void showRentalDetails(int rentalId) {
        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                String query = "SELECT r.id, u.username, u.full_name, u.contact_number, e.name as equipment_name, " +
                        "r.rental_start, r.rental_end, r.rental_purpose, r.status, r.rejection_reason, " +
                        "r.total_cost, r.request_date " +
                        "FROM farmer_rentals r " +
                        "JOIN farmer_users u ON r.farmer_id = u.id " +
                        "JOIN farmer_equipment e ON r.equipment_id = e.id " +
                        "WHERE r.id = ?";

                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, rentalId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    StringBuilder details = new StringBuilder();
                    details.append("Rental ID: ").append(rs.getInt("id")).append("\n\n");
                    details.append("Farmer: ").append(rs.getString("full_name")).append(" (").append(rs.getString("username")).append(")\n");
                    details.append("Contact: ").append(rs.getString("contact_number")).append("\n\n");
                    details.append("Equipment: ").append(rs.getString("equipment_name")).append("\n");
                    details.append("Start Date: ").append(rs.getDate("rental_start")).append("\n");
                    details.append("End Date: ").append(rs.getDate("rental_end")).append("\n");
                    details.append("Purpose: ").append(rs.getString("rental_purpose")).append("\n\n");
                    details.append("Status: ").append(rs.getString("status")).append("\n");

                    String rejectionReason = rs.getString("rejection_reason");
                    if (rejectionReason != null && !rejectionReason.isEmpty()) {
                        details.append("Rejection Reason: ").append(rejectionReason).append("\n");
                    }

                    details.append("\nTotal Cost: $").append(rs.getDouble("total_cost")).append("\n");
                    details.append("Request Date: ").append(rs.getTimestamp("request_date"));

                    JOptionPane.showMessageDialog(this,
                            details.toString(),
                            "Rental Details",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No details found for this rental request.",
                            "Not Found",
                            JOptionPane.WARNING_MESSAGE);
                }

                rs.close();
                ps.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error retrieving rental details: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
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

    // Add a view details button to the rental panel
    private void addViewDetailsButton() {
        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = rentalRequestsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int requestId = (int) rentalRequestsTable.getValueAt(selectedRow, 0);
                    showRentalDetails(requestId);
                } else {
                    JOptionPane.showMessageDialog(AdminHomePage.this,
                            "Please select a rental request to view details.",
                            "No Selection",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Add to the control panel
        JPanel controlPanel = (JPanel) rentalPanel.getComponent(1); // Get the control panel
        controlPanel.add(viewDetailsButton);
    }

// Call this method in your initRentalPanel method, after adding the other buttons
// addViewDetailsButton();

    // Optional: Add a refresh button
    private void addRefreshButton() {
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRentalRequests();
            }
        });

        // Add to the control panel
        JPanel controlPanel = (JPanel) rentalPanel.getComponent(1); // Get the control panel
        controlPanel.add(refreshButton);
    }

    // Add this as a new instance variable in the AdminHomePage class
    private AdminNotificationSystem notificationSystem;

    // Then add the following method to initialize the notification system
    private void initNotificationSystem() {
        notificationSystem = new AdminNotificationSystem(this);

        // Create a notification button for the header
        JButton notificationButton = new JButton(new ImageIcon("resources/notification_icon.png"));
        if (notificationButton.getIcon() == null) {
            // Fallback if icon not found
            notificationButton.setText("🔔");
            notificationButton.setFont(new Font("Arial", Font.PLAIN, 16));
        }
        notificationButton.setToolTipText("Notifications");
        notificationButton.setBorderPainted(false);
        notificationButton.setContentAreaFilled(false);
        notificationButton.setFocusPainted(false);

        // Add the notification counter to the button
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(40, 40));

        notificationButton.setBounds(0, 0, 40, 40);
        layeredPane.add(notificationButton, JLayeredPane.DEFAULT_LAYER);

        JLabel notificationCountLabel = notificationSystem.getNotificationCountLabel();
        notificationCountLabel.setBounds(25, 5, 20, 20);
        layeredPane.add(notificationCountLabel, JLayeredPane.PALETTE_LAYER);

        // Add action listener to show notifications
        notificationButton.addActionListener(e -> notificationSystem.showNotificationDialog());

        // Add to header panel
        JPanel headerPanel = (JPanel) getContentPane().getComponent(0);
        headerPanel.add(layeredPane, BorderLayout.CENTER);

        // Perform initial check for notifications
        notificationSystem.checkForNewNotifications();

        // Add window listener to stop the notification checker when the frame is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                notificationSystem.stopNotificationChecker();
            }
        });
    }

    // Method to switch to the rental tab
    public void switchToRentalTab() {
        tabbedPane.setSelectedIndex(1); // Rental tab is at index 1
    }

    // Method to select a specific rental request
    public void selectRentalRequest(int rentalId) {
        // Find the rental in the table and select it
        for (int i = 0; i < rentalRequestsTable.getRowCount(); i++) {
            if ((int) rentalRequestsTable.getValueAt(i, 0) == rentalId) {
                rentalRequestsTable.setRowSelectionInterval(i, i);
                rentalRequestsTable.scrollRectToVisible(rentalRequestsTable.getCellRect(i, 0, true));
                break;
            }
        }
    }

    // Method to simulate a new user registration (for testing)
    public void simulateNewUserRegistration() {
        // Create a dialog for simulation
        JDialog simulationDialog = new JDialog(this, "Simulation Controls", true);
        simulationDialog.setLayout(new BorderLayout());
        simulationDialog.setSize(300, 200);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Username:"));
        JTextField usernameField = new JTextField("farmer" + System.currentTimeMillis() % 1000);
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Full Name:"));
        JTextField nameField = new JTextField("Test Farmer");
        formPanel.add(nameField);

        JButton simulateButton = new JButton("Simulate Registration");
        simulateButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String fullName = nameField.getText().trim();

            if (!username.isEmpty() && !fullName.isEmpty()) {
                // Insert dummy user in database
                Connection conn = DatabaseConnection.connect();
                if (conn != null) {
                    try {
                        String query = "INSERT INTO farmer_users (username, password, full_name, email, contact_number, role) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";

                        PreparedStatement ps = conn.prepareStatement(query);
                        ps.setString(1, username);
                        ps.setString(2, "password123"); // Dummy password
                        ps.setString(3, fullName);
                        ps.setString(4, username + "@example.com");
                        ps.setString(5, "555-123-4567");
                        ps.setString(6, "farmer");

                        ps.executeUpdate();
                        ps.close();

                        JOptionPane.showMessageDialog(simulationDialog,
                                "Simulated user registration for " + fullName,
                                "Simulation",
                                JOptionPane.INFORMATION_MESSAGE);

                        // Check for new notifications immediately
                        notificationSystem.checkForNewNotifications();

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(simulationDialog,
                                "Error: " + ex.getMessage(),
                                "Simulation Error",
                                JOptionPane.ERROR_MESSAGE);
                    } finally {
                        try {
                            conn.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                simulationDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(simulationDialog,
                        "Please fill in all fields",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(simulateButton);

        simulationDialog.add(formPanel, BorderLayout.CENTER);
        simulationDialog.add(buttonPanel, BorderLayout.SOUTH);
        simulationDialog.setLocationRelativeTo(this);
        simulationDialog.setVisible(true);
    }

    // Add this button to the header panel in the constructor after creating the welcome label


    // Add this line to the constructor after adding the status panel


// Call this method in your initRentalPanel method, after adding the other buttons
// addRefreshButton();

}