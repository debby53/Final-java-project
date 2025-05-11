package FarmEquipmentRentalSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class AdminHomePage extends JFrame {
    private JTabbedPane tabbedPane;
    private String adminUsername;

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
                    JOptionPane.showMessageDialog(AdminHomePage.this,
                            "Feature to approve rental request ID " + requestId + " will be implemented in future versions.");
                } else {
                    JOptionPane.showMessageDialog(AdminHomePage.this,
                            "Please select a rental request to approve.");
                }
            }
        });

        rejectRentalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = rentalRequestsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int requestId = (int) rentalRequestsTable.getValueAt(selectedRow, 0);
                    JOptionPane.showMessageDialog(AdminHomePage.this,
                            "Feature to reject rental request ID " + requestId + " will be implemented in future versions.");
                } else {
                    JOptionPane.showMessageDialog(AdminHomePage.this,
                            "Please select a rental request to reject.");
                }
            }
        });
    }

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

    private void loadRentalRequests() {
        // Clear existing data
        rentalRequestsTableModel.setRowCount(0);

        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                // Changed table names to match the actual database tables
                String query = "SELECT r.id as rental_id, u.username, e.name, r.rental_start, r.rental_end, r.status " +
                        "FROM farmer_rentals r " +
                        "JOIN farmer_users u ON r.farmer_id = u.id " +
                        "JOIN farmer_equipment e ON r.equipment_id = e.id " +
                        "WHERE r.status = 'Pending'";

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
}