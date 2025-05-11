package FarmEquipmentRentalSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class FarmerHomePage extends JFrame {
    private String farmerUsername;
    private int farmerId;
    private JTabbedPane tabbedPane;

    // Equipment catalog panel components
    private JPanel catalogPanel;
    private JTable equipmentTable;
    private DefaultTableModel equipmentTableModel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JButton rentEquipmentButton;

    // My rentals panel components
    private JPanel myRentalsPanel;
    private JTable myRentalsTable;
    private DefaultTableModel myRentalsTableModel;
    private JButton returnEquipmentButton;

    public FarmerHomePage(String username) {
        this.farmerUsername = username;
        this.farmerId = getFarmerId(username);

        setTitle("Farm Equipment Rental System - Farmer Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create welcome banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + username);
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
        initCatalogPanel();
        initMyRentalsPanel();

        // Add tabs
        tabbedPane.addTab("Available Equipment", catalogPanel);
        tabbedPane.addTab("My Rentals", myRentalsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        JLabel statusLabel = new JLabel("System Status: Online");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);

        loadEquipmentData();
        loadMyRentals();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private int getFarmerId(String username) {
        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                String query = "SELECT id FROM farmer_users WHERE username = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return rs.getInt("id");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error retrieving farmer ID: " + ex.getMessage(),
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
        return -1; // Invalid ID
    }

    private void initCatalogPanel() {
        catalogPanel = new JPanel(new BorderLayout());

        // Search and filter panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(15);
        JLabel categoryLabel = new JLabel("Category:");

        String[] categories = {"All Categories", "Tractor", "Harvester", "Planter", "Sprayer", "Other"};
        categoryFilter = new JComboBox<>(categories);
        JButton searchButton = new JButton("Search");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(categoryLabel);
        searchPanel.add(categoryFilter);
        searchPanel.add(searchButton);

        catalogPanel.add(searchPanel, BorderLayout.NORTH);

        // Table for displaying equipment
        String[] columns = {"ID", "Name", "Category", "Daily Rate", "Status", "Description"};
        equipmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        equipmentTable = new JTable(equipmentTableModel);
        equipmentTable.getTableHeader().setReorderingAllowed(false);
        equipmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Make table sortable
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(equipmentTableModel);
        equipmentTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(equipmentTable);
        catalogPanel.add(scrollPane, BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rentEquipmentButton = new JButton("Rent Selected Equipment");
        controlPanel.add(rentEquipmentButton);
        catalogPanel.add(controlPanel, BorderLayout.SOUTH);

        // Action listeners
        searchButton.addActionListener(e -> filterEquipment());

        searchField.addActionListener(e -> filterEquipment());

        categoryFilter.addActionListener(e -> filterEquipment());

        rentEquipmentButton.addActionListener(e -> {
            int selectedRow = equipmentTable.getSelectedRow();
            if (selectedRow >= 0) {
                selectedRow = equipmentTable.convertRowIndexToModel(selectedRow);
                int equipmentId = (int) equipmentTableModel.getValueAt(selectedRow, 0);
                String equipmentName = (String) equipmentTableModel.getValueAt(selectedRow, 1);
                String status = (String) equipmentTableModel.getValueAt(selectedRow, 4);

                if (status.equals("Available")) {
                    showRentalDialog(equipmentId, equipmentName);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "This equipment is currently " + status + " and cannot be rented.",
                            "Equipment Unavailable", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an equipment to rent.");
            }
        });
    }

    private void initMyRentalsPanel() {
        myRentalsPanel = new JPanel(new BorderLayout());

        // Table for displaying rentals
        String[] columns = {"Rental ID", "Equipment", "Start Date", "End Date", "Status", "Daily Rate", "Total Cost"};
        myRentalsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        myRentalsTable = new JTable(myRentalsTableModel);
        myRentalsTable.getTableHeader().setReorderingAllowed(false);
        myRentalsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(myRentalsTable);
        myRentalsPanel.add(scrollPane, BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        returnEquipmentButton = new JButton("Return Selected Equipment");
        controlPanel.add(returnEquipmentButton);
        myRentalsPanel.add(controlPanel, BorderLayout.SOUTH);

        // Action listeners
        returnEquipmentButton.addActionListener(e -> {
            int selectedRow = myRentalsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int rentalId = (int) myRentalsTable.getValueAt(selectedRow, 0);
                String status = (String) myRentalsTable.getValueAt(selectedRow, 4);

                if (status.equals("Active")) {
                    JOptionPane.showMessageDialog(this,
                            "Feature to return equipment for rental ID " + rentalId +
                                    " will be implemented in future versions.",
                            "Feature Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Only active rentals can be returned.",
                            "Invalid Action", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a rental to return.");
            }
        });
    }

    private void loadEquipmentData() {
        // Clear existing data
        equipmentTableModel.setRowCount(0);

        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                String query = "SELECT * FROM farmer_equipment WHERE is_available = 'Y'";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("category"));
                    row.add(rs.getDouble("rental_rate"));
                    row.add(rs.getString("is_available").equals("Y") ? "Available" : "Unavailable");
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

    private void loadMyRentals() {
        // Clear existing data
        myRentalsTableModel.setRowCount(0);

        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                String query = "SELECT r.id, e.name, r.rental_start, r.rental_end, r.status, e.rental_rate, " +
                        "r.total_cost " +
                        "FROM farmer_rentals r " +
                        "JOIN farmer_equipment e ON r.equipment_id = e.id " +
                        "WHERE r.farmer_id = ?";

                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, farmerId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getDate("rental_start"));
                    row.add(rs.getDate("rental_end"));
                    row.add(rs.getString("status"));
                    row.add(rs.getDouble("rental_rate"));
                    row.add(rs.getDouble("total_cost"));

                    myRentalsTableModel.addRow(row);
                }

                rs.close();
                ps.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading rental data: " + ex.getMessage(),
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

    private void filterEquipment() {
        String searchText = searchField.getText().toLowerCase();
        String category = (String) categoryFilter.getSelectedItem();

        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                String query;
                PreparedStatement ps;

                if (category.equals("All Categories")) {
                    query = "SELECT * FROM farmer_equipment WHERE " +
                            "(LOWER(name) LIKE ? OR LOWER(description) LIKE ?) AND is_available = 'Y'";
                    ps = conn.prepareStatement(query);
                    ps.setString(1, "%" + searchText + "%");
                    ps.setString(2, "%" + searchText + "%");
                } else {
                    query = "SELECT * FROM farmer_equipment WHERE " +
                            "(LOWER(name) LIKE ? OR LOWER(description) LIKE ?) AND category = ? AND is_available = 'Y'";
                    ps = conn.prepareStatement(query);
                    ps.setString(1, "%" + searchText + "%");
                    ps.setString(2, "%" + searchText + "%");
                    ps.setString(3, category);
                }

                ResultSet rs = ps.executeQuery();

                // Clear existing data
                equipmentTableModel.setRowCount(0);

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("category"));
                    row.add(rs.getDouble("rental_rate"));
                    row.add(rs.getString("is_available").equals("Y") ? "Available" : "Unavailable");
                    row.add(rs.getString("description"));

                    equipmentTableModel.addRow(row);
                }

                rs.close();
                ps.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error filtering equipment: " + ex.getMessage(),
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

    private void showRentalDialog(int equipmentId, String equipmentName) {
        // Create dialog
        JDialog rentalDialog = new JDialog(this, "Rent Equipment", true);
        rentalDialog.setLayout(new BorderLayout());
        rentalDialog.setSize(400, 250);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Equipment info
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("Renting: " + equipmentName);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(infoLabel, gbc);

        // Start date
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Start Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField startDateField = new JTextField(10);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        startDateField.setText(dateFormat.format(new Date()));
        formPanel.add(startDateField, gbc);

        // End date
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("End Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField endDateField = new JTextField(10);
        // Set default end date to 7 days from now
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        endDateField.setText(dateFormat.format(cal.getTime()));
        formPanel.add(endDateField, gbc);

        // No purpose field in schema, so removed it

        rentalDialog.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Submit Request");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        rentalDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date startDate, endDate;
                try {
                    startDate = dateFormat.parse(startDateField.getText().trim());
                    endDate = dateFormat.parse(endDateField.getText().trim());

                    // Validate dates
                    Date today = new Date();
                    Calendar todayCal = Calendar.getInstance();
                    todayCal.set(Calendar.HOUR_OF_DAY, 0);
                    todayCal.set(Calendar.MINUTE, 0);
                    todayCal.set(Calendar.SECOND, 0);
                    todayCal.set(Calendar.MILLISECOND, 0);
                    today = todayCal.getTime();

                    if (startDate.before(today)) {
                        JOptionPane.showMessageDialog(rentalDialog, "Start date cannot be in the past.");
                        return;
                    }

                    if (endDate.before(startDate)) {
                        JOptionPane.showMessageDialog(rentalDialog, "End date must be after start date.");
                        return;
                    }
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(rentalDialog, "Please enter valid dates in yyyy-MM-dd format.");
                    return;
                }

                // Submit rental request
                if (submitRentalRequest(equipmentId, startDate, endDate)) {
                    rentalDialog.dispose();
                    loadEquipmentData(); // Refresh equipment list
                    loadMyRentals();     // Refresh rentals list
                    tabbedPane.setSelectedIndex(1); // Switch to My Rentals tab
                }
            }
        });

        cancelButton.addActionListener(e -> rentalDialog.dispose());

        rentalDialog.setLocationRelativeTo(this);
        rentalDialog.setVisible(true);
    }

    private boolean submitRentalRequest(int equipmentId, Date startDate, Date endDate) {
        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                // Check if the equipment is still available
                String checkQuery = "SELECT is_available, rental_rate FROM farmer_equipment WHERE id = ?";
                PreparedStatement checkPs = conn.prepareStatement(checkQuery);
                checkPs.setInt(1, equipmentId);
                ResultSet rs = checkPs.executeQuery();

                if (rs.next() && rs.getString("is_available").equals("Y")) {
                    double dailyRate = rs.getDouble("rental_rate");

                    // Calculate total cost - days between start and end date multiplied by daily rate
                    long diffInMillies = endDate.getTime() - startDate.getTime();
                    int diffInDays = (int) (diffInMillies / (24 * 60 * 60 * 1000)) + 1; // Include start day
                    double totalCost = diffInDays * dailyRate;

                    // Insert rental request
                    String insertQuery = "INSERT INTO farmer_rentals (farmer_id, equipment_id, rental_start, rental_end, " +
                            "total_cost, status, booking_date) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

                    PreparedStatement ps = conn.prepareStatement(insertQuery);
                    ps.setInt(1, farmerId);
                    ps.setInt(2, equipmentId);
                    ps.setDate(3, new java.sql.Date(startDate.getTime()));
                    ps.setDate(4, new java.sql.Date(endDate.getTime()));
                    ps.setDouble(5, totalCost);
                    ps.setString(6, "Pending");

                    int rowsAffected = ps.executeUpdate();

                    if (rowsAffected > 0) {
                        // Update equipment availability to 'N' (not available)
                        String updateQuery = "UPDATE farmer_equipment SET is_available = 'N' WHERE id = ?";
                        PreparedStatement updatePs = conn.prepareStatement(updateQuery);
                        updatePs.setInt(1, equipmentId);
                        updatePs.executeUpdate();
                        updatePs.close();

                        JOptionPane.showMessageDialog(this,
                                "Rental request submitted successfully! Your request is pending admin approval.");
                        return true;
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Sorry, this equipment is no longer available. It may have been rented by someone else.",
                            "Equipment Unavailable", JOptionPane.WARNING_MESSAGE);
                }

                rs.close();
                checkPs.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error submitting rental request: " + ex.getMessage(),
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
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FarmerHomePage("farmer1"));
    }
}