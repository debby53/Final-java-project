package FarmEquipmentRentalSystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HomePage extends JFrame {

    private JMenuBar menuBar;
    private JMenu equipmentMenu, rentalMenu, adminMenu, helpMenu;
    private JMenuItem addEquipmentItem, viewEquipmentItem, updateEquipmentItem, deleteEquipmentItem;
    private JMenuItem rentEquipmentItem, returnEquipmentItem, viewRentalsItem;
    private JMenuItem viewRevenueReportItem, viewCustomersItem;
    private JMenuItem aboutItem, helpDocItem;
    private JPanel contentPanel, sidebarPanel, dashboardPanel;
    private JLabel statusLabel, dateTimeLabel, userLabel;
    private JButton rentButton, returnButton, viewEquipButton, reportButton;
    private Timer dateTimeTimer;
    private Color primaryColor = new Color(76, 175, 80);  // Green theme for farm equipment
    private Color secondaryColor = new Color(46, 125, 50);
    private Color accentColor = new Color(255, 235, 59);  // Yellow accent

    public HomePage() {
        setTitle("Farm Equipment Rental System");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set the application icon
        ImageIcon appIcon = createImageIcon("tractor_icon.png", "App Icon");
        if (appIcon != null) {
            setIconImage(appIcon.getImage());
        }

        // Create a styled menu bar
        createMenuBar();

        // Main layout with sidebar and content area
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create sidebar panel
        createSidebarPanel();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Content Panel for displaying different functionalities
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new CompoundBorder(
                new EtchedBorder(),
                new EmptyBorder(10, 10, 10, 10)
        ));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Show dashboard as default view
        showDashboard();

        // Status bar at the bottom
        JPanel statusPanel = createStatusBar();
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        add(mainPanel);

        // Start the date/time timer
        startDateTimeTimer();
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBorder(new EmptyBorder(2, 2, 2, 2));

        // Equipment menu
        equipmentMenu = new JMenu("Equipment");
        equipmentMenu.setMnemonic(KeyEvent.VK_E);

        addEquipmentItem = createMenuItem("Add Equipment", "add.png", KeyEvent.VK_A);
        viewEquipmentItem = createMenuItem("View Equipment", "view.png", KeyEvent.VK_V);
        updateEquipmentItem = createMenuItem("Update Equipment", "update.png", KeyEvent.VK_U);
        deleteEquipmentItem = createMenuItem("Delete Equipment", "delete.png", KeyEvent.VK_D);

        equipmentMenu.add(addEquipmentItem);
        equipmentMenu.add(viewEquipmentItem);
        equipmentMenu.add(updateEquipmentItem);
        equipmentMenu.add(deleteEquipmentItem);

        // Rental menu
        rentalMenu = new JMenu("Rentals");
        rentalMenu.setMnemonic(KeyEvent.VK_R);

        rentEquipmentItem = createMenuItem("Rent Equipment", "rent.png", KeyEvent.VK_R);
        returnEquipmentItem = createMenuItem("Return Equipment", "return.png", KeyEvent.VK_T);
        viewRentalsItem = createMenuItem("View My Rentals", "history.png", KeyEvent.VK_M);

        rentalMenu.add(rentEquipmentItem);
        rentalMenu.add(returnEquipmentItem);
        rentalMenu.add(viewRentalsItem);

        // Admin menu
        adminMenu = new JMenu("Admin");
        adminMenu.setMnemonic(KeyEvent.VK_A);

        viewRevenueReportItem = createMenuItem("Revenue Report", "report.png", KeyEvent.VK_R);
        viewCustomersItem = createMenuItem("Manage Customers", "customers.png", KeyEvent.VK_C);

        adminMenu.add(viewRevenueReportItem);
        adminMenu.add(viewCustomersItem);

        // Help menu
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        aboutItem = createMenuItem("About", "about.png", KeyEvent.VK_A);
        helpDocItem = createMenuItem("Documentation", "help.png", KeyEvent.VK_D);

        helpMenu.add(aboutItem);
        helpMenu.add(helpDocItem);

        // Add menus to the menu bar
        menuBar.add(equipmentMenu);
        menuBar.add(rentalMenu);
        menuBar.add(adminMenu);
        menuBar.add(helpMenu);

        // Set the menu bar for the frame
        setJMenuBar(menuBar);

        // Handle actions for menu items
        addEquipmentItem.addActionListener(e -> showAddEquipmentForm());
        viewEquipmentItem.addActionListener(e -> viewEquipment());
        updateEquipmentItem.addActionListener(e -> updateEquipment());
        deleteEquipmentItem.addActionListener(e -> deleteEquipment());
        rentEquipmentItem.addActionListener(e -> rentEquipment());
        returnEquipmentItem.addActionListener(e -> returnEquipment());
        viewRentalsItem.addActionListener(e -> viewRentals());
        viewRevenueReportItem.addActionListener(e -> viewRevenueReport());
        viewCustomersItem.addActionListener(e -> manageCustomers());
        aboutItem.addActionListener(e -> showAboutDialog());
        helpDocItem.addActionListener(e -> showHelp());
    }

    private JMenuItem createMenuItem(String text, String iconName, int mnemonic) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setMnemonic(mnemonic);

        ImageIcon icon = createImageIcon(iconName, text);
        if (icon != null) {
            menuItem.setIcon(icon);
        }

        return menuItem;
    }

    private void createSidebarPanel() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(240, 240, 240));
        sidebarPanel.setBorder(new CompoundBorder(
                new EtchedBorder(),
                new EmptyBorder(10, 10, 10, 10)
        ));
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));

        // Add logo at the top
        JLabel logoLabel = new JLabel("Farm Equipment Rental", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        logoLabel.setForeground(secondaryColor);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        ImageIcon logoIcon = createImageIcon("farm_logo.png", "Farm Logo");
        if (logoIcon != null) {
            logoLabel.setIcon(logoIcon);
            logoLabel.setHorizontalTextPosition(JLabel.CENTER);
            logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
        }

        sidebarPanel.add(logoLabel);

        // Quick access buttons
        rentButton = createSidebarButton("Rent Equipment", "rent_large.png");
        returnButton = createSidebarButton("Return Equipment", "return_large.png");
        viewEquipButton = createSidebarButton("View Equipment", "view_large.png");
        reportButton = createSidebarButton("Reports", "report_large.png");

        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(rentButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(returnButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(viewEquipButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(reportButton);

        // Add action listeners
        rentButton.addActionListener(e -> rentEquipment());
        returnButton.addActionListener(e -> returnEquipment());
        viewEquipButton.addActionListener(e -> viewEquipment());
        reportButton.addActionListener(e -> viewRevenueReport());

        // Add filler to push logout to bottom
        sidebarPanel.add(Box.createVerticalGlue());

        // Add logout button at bottom
        JButton logoutButton = createSidebarButton("Logout", "logout.png");
        logoutButton.addActionListener(e -> logout());
        sidebarPanel.add(logoutButton);
    }

    private JButton createSidebarButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setFocusPainted(false);
        button.setBackground(primaryColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(5, 15, 5, 15));

        ImageIcon icon = createImageIcon(iconName, text);
        if (icon != null) {
            button.setIcon(icon);
        }

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(secondaryColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(primaryColor);
            }
        });

        return button;
    }

    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(new EmptyBorder(2, 5, 2, 5));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        JPanel rightStatusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        // User label
        userLabel = new JLabel("User: Admin");
        userLabel.setBorder(new EmptyBorder(2, 5, 2, 5));

        // Date/time label
        dateTimeLabel = new JLabel();
        dateTimeLabel.setBorder(new EmptyBorder(2, 5, 2, 5));

        rightStatusPanel.add(userLabel);
        rightStatusPanel.add(dateTimeLabel);
        statusPanel.add(rightStatusPanel, BorderLayout.EAST);

        return statusPanel;
    }

    private void startDateTimeTimer() {
        // Update date/time every second
        dateTimeTimer = new Timer(1000, e -> {
            LocalDate now = LocalDate.now();
            String dateStr = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
            dateTimeLabel.setText(dateStr);
        });
        dateTimeTimer.start();
    }

    private void showDashboard() {
        contentPanel.removeAll();

        dashboardPanel = new JPanel(new BorderLayout());

        // Welcome header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel welcomeLabel = new JLabel("Welcome to Farm Equipment Rental System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(secondaryColor);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        // Dashboard stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));

        statsPanel.add(createStatPanel("Available Equipment", "42", "tractor_icon.png"));
        statsPanel.add(createStatPanel("Active Rentals", "12", "rental_icon.png"));
        statsPanel.add(createStatPanel("Due Returns Today", "5", "calendar_icon.png"));

        // Recent activities panel
        JPanel recentPanel = new JPanel(new BorderLayout());
        recentPanel.setBorder(BorderFactory.createTitledBorder("Recent Activities"));

        String[] columnNames = {"ID", "Equipment", "Customer", "Action", "Date"};
        Object[][] data = {
                {"RT001", "Tractor X100", "John Smith", "Rented", "2025-05-07"},
                {"RT002", "Harvester H200", "Mary Johnson", "Returned", "2025-05-07"},
                {"RT003", "Plough P50", "Robert Brown", "Rented", "2025-05-06"},
                {"RT004", "Tiller T30", "Susan Davis", "Maintenance", "2025-05-06"},
                {"RT005", "Sprayer S100", "James Wilson", "Rented", "2025-05-05"}
        };

        JTable activityTable = new JTable(data, columnNames);
        activityTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(activityTable);
        recentPanel.add(scrollPane);

        // Quick access panels
        JPanel quickAccessPanel = new JPanel(new GridLayout(1, 4, 10, 0));

        quickAccessPanel.add(createQuickAccessPanel("Rent Equipment", "Start a new equipment rental", "rent_quick.png", e -> rentEquipment()));
        quickAccessPanel.add(createQuickAccessPanel("Return Equipment", "Process equipment returns", "return_quick.png", e -> returnEquipment()));
        quickAccessPanel.add(createQuickAccessPanel("Equipment List", "View all available equipment", "list_quick.png", e -> viewEquipment()));
        quickAccessPanel.add(createQuickAccessPanel("Reports", "Access system reports", "report_quick.png", e -> viewRevenueReport()));

        // Add all components to dashboard
        dashboardPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(recentPanel, BorderLayout.CENTER);
        centerPanel.add(quickAccessPanel, BorderLayout.SOUTH);

        dashboardPanel.add(centerPanel, BorderLayout.CENTER);

        contentPanel.add(dashboardPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createStatPanel(String title, String value, String iconName) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(primaryColor);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel);
        textPanel.add(valueLabel);

        ImageIcon icon = createImageIcon(iconName, title);
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            panel.add(iconLabel, BorderLayout.WEST);
        }

        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createQuickAccessPanel(String title, String description, String iconName, ActionListener action) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        textPanel.setBackground(new Color(245, 245, 245));
        textPanel.add(titleLabel);
        textPanel.add(descLabel);

        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        iconPanel.setBackground(new Color(245, 245, 245));

        ImageIcon icon = createImageIcon(iconName, title);
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            iconPanel.add(iconLabel);
        }

        panel.add(iconPanel, BorderLayout.NORTH);
        panel.add(textPanel, BorderLayout.CENTER);

        // Make panel clickable
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) {
                    action.actionPerformed(new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, title));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(235, 235, 235));
                textPanel.setBackground(new Color(235, 235, 235));
                iconPanel.setBackground(new Color(235, 235, 235));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(new Color(245, 245, 245));
                textPanel.setBackground(new Color(245, 245, 245));
                iconPanel.setBackground(new Color(245, 245, 245));
            }
        });

        return panel;
    }

    // Helper method to create ImageIcon objects
    private ImageIcon createImageIcon(String path, String description) {
        // In a real application, you would load actual images
        // For this example, we'll just return null since we don't have the actual image files
        return null;

        /* Uncomment this in a real application
        java.net.URL imgURL = getClass().getResource("/images/" + path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
        */
    }

    // Method to show the form for adding new equipment
    private void showAddEquipmentForm() {
        contentPanel.removeAll();

        JPanel formPanel = new JPanel(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Add New Equipment", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Form fields
        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        fieldsPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        fieldsPanel.add(new JLabel("Equipment ID:"));
        JTextField idField = new JTextField();
        fieldsPanel.add(idField);

        fieldsPanel.add(new JLabel("Equipment Name:"));
        JTextField nameField = new JTextField();
        fieldsPanel.add(nameField);

        fieldsPanel.add(new JLabel("Category:"));
        String[] categories = {"Tractor", "Harvester", "Plough", "Tiller", "Sprayer", "Other"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        fieldsPanel.add(categoryCombo);

        fieldsPanel.add(new JLabel("Daily Rental Rate:"));
        JTextField rateField = new JTextField();
        fieldsPanel.add(rateField);

        fieldsPanel.add(new JLabel("Condition:"));
        String[] conditions = {"New", "Excellent", "Good", "Fair", "Poor"};
        JComboBox<String> conditionCombo = new JComboBox<>(conditions);
        fieldsPanel.add(conditionCombo);

        fieldsPanel.add(new JLabel("Available:"));
        JCheckBox availableCheck = new JCheckBox();
        availableCheck.setSelected(true);
        fieldsPanel.add(availableCheck);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton saveButton = new JButton("Save Equipment");
        saveButton.setBackground(primaryColor);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Equipment saved successfully!");
            showDashboard();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> showDashboard());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add components to form panel
        formPanel.add(headerLabel, BorderLayout.NORTH);
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.add(formPanel);
        contentPanel.revalidate();
        contentPanel.repaint();

        setStatusText("Adding new equipment...");
    }

    // Method to view equipment
    private void viewEquipment() {
        contentPanel.removeAll();

        JPanel viewPanel = new JPanel(new BorderLayout(0, 10));

        // Header with search field
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel headerLabel = new JLabel("Equipment Inventory");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(primaryColor);
        searchButton.setForeground(Color.WHITE);

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        filterPanel.add(new JLabel("Category:"));
        String[] categories = {"All", "Tractor", "Harvester", "Plough", "Tiller", "Sprayer", "Other"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        filterPanel.add(categoryCombo);

        filterPanel.add(new JLabel("Status:"));
        String[] statuses = {"All", "Available", "Rented", "Maintenance"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        filterPanel.add(statusCombo);

        JButton filterButton = new JButton("Apply Filter");
        filterPanel.add(filterButton);

        // Equipment table
        String[] columnNames = {"ID", "Name", "Category", "Daily Rate", "Status", "Condition"};
        Object[][] data = {
                {"EQ001", "John Deere Tractor X100", "Tractor", "$150.00", "Available", "Excellent"},
                {"EQ002", "Harvester H200", "Harvester", "$200.00", "Rented", "Good"},
                {"EQ003", "Plough P50", "Plough", "$75.00", "Available", "Good"},
                {"EQ004", "Tiller T30", "Tiller", "$50.00", "Maintenance", "Fair"},
                {"EQ005", "Sprayer S100", "Sprayer", "$45.00", "Available", "Excellent"},
                {"EQ006", "Tractor T150", "Tractor", "$175.00", "Available", "New"},
                {"EQ007", "Seeder S20", "Other", "$60.00", "Rented", "Good"}
        };

        JTable equipmentTable = new JTable(data, columnNames);
        equipmentTable.setFillsViewportHeight(true);
        equipmentTable.setRowHeight(25);

        // Add custom cell renderer for the Status column
        equipmentTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String status = (String) value;
                if ("Available".equals(status)) {
                    comp.setForeground(new Color(0, 128, 0));  // Green
                } else if ("Rented".equals(status)) {
                    comp.setForeground(new Color(200, 0, 0));  // Red
                } else if ("Maintenance".equals(status)) {
                    comp.setForeground(new Color(255, 140, 0));  // Orange
                }

                return comp;
            }
        });

        JScrollPane scrollPane = new JScrollPane(equipmentTable);

        // Actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = new JButton("Add New");
        addButton.setBackground(primaryColor);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> showAddEquipmentForm());

        JButton editButton = new JButton("Edit Selected");
        editButton.addActionListener(e -> {
            int selected = equipmentTable.getSelectedRow();
            if (selected >= 0) {
                updateEquipment();
            } else {
                JOptionPane.showMessageDialog(this, "Please select an equipment item to edit.");
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            int selected = equipmentTable.getSelectedRow();
            if (selected >= 0) {
                deleteEquipment();
            } else {
                JOptionPane.showMessageDialog(this, "Please select an equipment item to delete.");
            }
        });

        actionsPanel.add(addButton);
        actionsPanel.add(editButton);
        actionsPanel.add(deleteButton);

        // Add all components to view panel
        viewPanel.add(headerPanel, BorderLayout.NORTH);
        viewPanel.add(filterPanel, BorderLayout.CENTER);
        viewPanel.add(scrollPane, BorderLayout.SOUTH);

        contentPanel.add(viewPanel, BorderLayout.CENTER);
        contentPanel.add(actionsPanel, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();

        setStatusText("Viewing equipment inventory...");
    }

    // Method to update equipment
    private void updateEquipment() {
        JOptionPane.showMessageDialog(this, "This will show the Update Equipment form.");
        setStatusText("Updating equipment...");
    }

    // Method to delete equipment
    private void deleteEquipment() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this equipment?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Equipment deleted successfully!");
            setStatusText("Equipment deleted.");
        }
    }

    // Method to rent equipment
    private void rentEquipment() {
        JOptionPane.showMessageDialog(this, "This will show the Rent Equipment form.");
        setStatusText("Processing equipment rental...");
    }

    // Method to return rented equipment
    private void returnEquipment() {
        JOptionPane.showMessageDialog(this, "This will show the Return Equipment form.");
        setStatusText("Processing equipment return...");
    }

    // Method to view the rentals of a user
    private void viewRentals() {
        JOptionPane.showMessageDialog(this, "This will show the list of rentals for the user.");
        setStatusText("Viewing rental history...");
    }

    // Method to view revenue report
    private void viewRevenueReport() {
        JOptionPane.showMessageDialog(this, "This will show the Revenue Report.");
        setStatusText("Generating revenue report...");
    }

    // Method to manage customers
    private void manageCustomers() {
        JOptionPane.showMessageDialog(this, "This will show the Customer Management interface.");
        setStatusText("Managing customers...");
    }

    // Method to show help documentation
    private void showHelp() {
        JOptionPane.showMessageDialog(this,
                "Farm Equipment Rental System Help\n\n" +
                        "This system allows you to manage farm equipment rentals.\n" +
                        "- Use the Equipment menu to add, view, update or delete equipment\n" +
                        "- Use the Rentals menu to rent or return equipment\n" +
                        "- Use the Admin menu to view reports and manage customers\n\n" +
                        "For more information, please contact support.",
                "Help Documentation",
                JOptionPane.INFORMATION_MESSAGE);
        setStatusText("Viewing help documentation...");
    }

    // Method to show about dialog
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Farm Equipment Rental System v1.0\n" +
                        "© 2025 Farm Equipment Rental Company\n\n" +
                        "A comprehensive system for managing farm equipment rentals.\n" +
                        "Developed by: Your Development Team",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Method to handle logout
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "You have been logged out successfully!");
            // In a real application, this would redirect to login screen
            System.exit(0);
        }
    }

    // Method to set status bar text
    private void setStatusText(String text) {
        statusLabel.setText(text);
    }

    public static void main(String[] args) {
        try {
            // Set look and feel to system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            HomePage homePage = new HomePage();
            homePage.setVisible(true);
        });
    }
}