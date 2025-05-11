package FarmEquipmentRentalSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class GuestPage extends JFrame {
    private JTabbedPane tabbedPane;

    // Equipment catalog panel components
    private JPanel catalogPanel;
    private JTable equipmentTable;
    private DefaultTableModel equipmentTableModel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JButton viewDetailsButton;
    private JButton rentEquipmentButton;

    // About panel components
    private JPanel aboutPanel;

    // Login/Register panel
    private JPanel loginPanel;

    public GuestPage() {
        setTitle("Farm Equipment Rental System - Guest Access");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome to Farm Equipment Rental System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Initialize panels
        initCatalogPanel();
        initAboutPanel();
        initLoginPanel();

        // Add tabs
        tabbedPane.addTab("Browse Equipment", catalogPanel);
        tabbedPane.addTab("About Us", aboutPanel);
        tabbedPane.addTab("Login/Register", loginPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        JLabel statusLabel = new JLabel("Guest Access Mode - Limited Features Available");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);

        // Action listeners for login/register buttons
        loginButton.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });

        registerButton.addActionListener(e -> {
            dispose();
            new RegistrationScreen();
        });

        loadEquipmentData();

        setLocationRelativeTo(null);
        setVisible(true);
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
        viewDetailsButton = new JButton("View Details");
        rentEquipmentButton = new JButton("Rent Equipment");

        controlPanel.add(viewDetailsButton);
        controlPanel.add(rentEquipmentButton);
        catalogPanel.add(controlPanel, BorderLayout.SOUTH);

        // Action listeners
        searchButton.addActionListener(e -> filterEquipment());

        searchField.addActionListener(e -> filterEquipment());

        categoryFilter.addActionListener(e -> filterEquipment());

        viewDetailsButton.addActionListener(e -> {
            int selectedRow = equipmentTable.getSelectedRow();
            if (selectedRow >= 0) {
                selectedRow = equipmentTable.convertRowIndexToModel(selectedRow);
                String equipmentName = (String) equipmentTableModel.getValueAt(selectedRow, 1);
                String category = (String) equipmentTableModel.getValueAt(selectedRow, 2);
                double dailyRate = (double) equipmentTableModel.getValueAt(selectedRow, 3);
                String description = (String) equipmentTableModel.getValueAt(selectedRow, 5);

                showEquipmentDetails(equipmentName, category, dailyRate, description);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an equipment to view details.");
            }
        });

        rentEquipmentButton.addActionListener(e -> {
            // Prompt user to log in
            int response = JOptionPane.showConfirmDialog(this,
                    "You need to login to rent equipment. Would you like to login now?",
                    "Login Required", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                dispose();
                new LoginScreen();
            }
        });
    }

    private void initAboutPanel() {
        aboutPanel = new JPanel(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // About us content
        JLabel titleLabel = new JLabel("About Farm Equipment Rental System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea aboutText = new JTextArea(
                "The Farm Equipment Rental System is a comprehensive platform designed to connect " +
                        "farmers with the agricultural equipment they need, when they need it. Our mission " +
                        "is to make farming more accessible and efficient by providing affordable rental options " +
                        "for a wide range of equipment.\n\n" +

                        "Our system offers:\n" +
                        "• A diverse catalog of farming equipment\n" +
                        "• Competitive daily rental rates\n" +
                        "• Simple booking and return processes\n" +
                        "• Equipment maintenance and quality assurance\n\n" +

                        "By creating an account, you can:\n" +
                        "• Browse all available equipment\n" +
                        "• Rent equipment for specific dates\n" +
                        "• Track your rental history\n" +
                        "• Return equipment when you're finished\n\n" +

                        "Contact us at farmequipment@example.com or call 555-123-4567 for more information."
        );
        aboutText.setEditable(false);
        aboutText.setLineWrap(true);
        aboutText.setWrapStyleWord(true);
        aboutText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        aboutText.setBackground(this.getBackground());
        aboutText.setMargin(new Insets(10, 10, 10, 10));

        JButton contactButton = new JButton("Contact Us");
        contactButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(aboutText);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(contactButton);

        aboutPanel.add(contentPanel, BorderLayout.CENTER);

        // Contact button functionality - requires login
        contactButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this,
                    "You need to login to contact us directly. Would you like to login now?",
                    "Login Required", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                dispose();
                new LoginScreen();
            }
        });
    }

    private void initLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Login to Access Full Features");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel infoLabel = new JLabel("<html><div style='text-align: center; width: 300px;'>" +
                "As a registered user, you can rent equipment, track your rentals, and more." +
                "</div></html>");

        JButton loginButton = new JButton("Go to Login Screen");
        JButton registerButton = new JButton("Register New Account");

        // Add components
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        loginPanel.add(infoLabel, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(registerButton, gbc);

        // Feature list
        JPanel featurePanel = new JPanel();
        featurePanel.setLayout(new BoxLayout(featurePanel, BoxLayout.Y_AXIS));
        featurePanel.setBorder(BorderFactory.createTitledBorder("Available Features"));

        String[] features = {
                "✅ Browse equipment catalog",
                "✅ View equipment details",
                "✅ Search and filter equipment",
                "❌ Rent equipment (login required)",
                "❌ Track your rentals (login required)",
                "❌ Return equipment (login required)",
                "❌ Contact support (login required)"
        };

        for (String feature : features) {
            JLabel featureLabel = new JLabel(feature);
            featureLabel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
            featurePanel.add(featureLabel);
        }

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(featurePanel, gbc);

        // Button actions
        loginButton.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });

        registerButton.addActionListener(e -> {
            dispose();
            new RegistrationScreen();
        });
    }

    private void loadEquipmentData() {
        // Clear existing data
        equipmentTableModel.setRowCount(0);

        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                String query = "SELECT * FROM Equipment WHERE status = 'Available'";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("equipment_id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("category"));
                    row.add(rs.getDouble("daily_rate"));
                    row.add(rs.getString("status"));
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

    private void filterEquipment() {
        String searchText = searchField.getText().toLowerCase();
        String category = (String) categoryFilter.getSelectedItem();

        Connection conn = DatabaseConnection.connect();
        if (conn != null) {
            try {
                String query;
                PreparedStatement ps;

                if (category.equals("All Categories")) {
                    query = "SELECT * FROM Equipment WHERE " +
                            "(name LIKE ? OR description LIKE ?) AND status = 'Available'";
                    ps = conn.prepareStatement(query);
                    ps.setString(1, "%" + searchText + "%");
                    ps.setString(2, "%" + searchText + "%");
                } else {
                    query = "SELECT * FROM Equipment WHERE " +
                            "(name LIKE ? OR description LIKE ?) AND category = ? AND status = 'Available'";
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
                    row.add(rs.getInt("equipment_id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("category"));
                    row.add(rs.getDouble("daily_rate"));
                    row.add(rs.getString("status"));
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

    private void showEquipmentDetails(String name, String category, double dailyRate, String description) {
        JDialog detailsDialog = new JDialog(this, "Equipment Details", true);
        detailsDialog.setLayout(new BorderLayout());
        detailsDialog.setSize(400, 300);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel nameLabel = new JLabel("Name: " + name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel categoryLabel = new JLabel("Category: " + category);
        JLabel rateLabel = new JLabel(String.format("Daily Rate: $%.2f", dailyRate));

        JTextArea descArea = new JTextArea(description);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBorder(BorderFactory.createTitledBorder("Description"));
        JScrollPane scrollPane = new JScrollPane(descArea);
        scrollPane.setPreferredSize(new Dimension(350, 150));

        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsPanel.add(categoryLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(rateLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsPanel.add(scrollPane);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton rentButton = new JButton("Rent This Equipment");
        JButton closeButton = new JButton("Close");

        buttonPanel.add(rentButton);
        buttonPanel.add(closeButton);

        detailsDialog.add(detailsPanel, BorderLayout.CENTER);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        rentButton.addActionListener(e -> {
            // Prompt user to log in
            int response = JOptionPane.showConfirmDialog(detailsDialog,
                    "You need to login to rent equipment. Would you like to login now?",
                    "Login Required", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                detailsDialog.dispose();
                dispose();
                new LoginScreen();
            }
        });

        closeButton.addActionListener(e -> detailsDialog.dispose());

        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuestPage());
    }
}