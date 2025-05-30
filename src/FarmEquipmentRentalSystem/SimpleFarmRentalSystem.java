package FarmEquipmentRentalSystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SimpleFarmRentalSystem extends JFrame {

    // Color scheme
    private Color primaryColor = new Color(76, 175, 80);  // Green theme
    private Color accentColor = new Color(255, 235, 59);  // Yellow accent

    // UI components
    private JPanel mainPanel, contentPanel;
    private JLabel statusLabel, dateLabel;
    private CardLayout cardLayout;

    public SimpleFarmRentalSystem() {
        setTitle("Farm Equipment Rental System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main UI components
        setupUI();

        // Show dashboard by default
        showDashboard();
    }

    private void setupUI() {
        // Main layout
        mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top menu
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Content panel with card layout for switching views
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(new CompoundBorder(
                new EtchedBorder(),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Status bar
        JPanel statusBar = createStatusBar();

        // Add components to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Equipment menu
        JMenu equipmentMenu = new JMenu("Equipment");
        JMenuItem addEquipItem = new JMenuItem("Add Equipment");
        JMenuItem viewEquipItem = new JMenuItem("View Equipment");
        equipmentMenu.add(addEquipItem);
        equipmentMenu.add(viewEquipItem);

        // Rentals menu
        JMenu rentalMenu = new JMenu("Rentals");
        JMenuItem rentItem = new JMenuItem("Rent Equipment");
        JMenuItem returnItem = new JMenuItem("Return Equipment");
        rentalMenu.add(rentItem);
        rentalMenu.add(returnItem);

        // Reports menu
        JMenu reportsMenu = new JMenu("Reports");
        JMenuItem revenueItem = new JMenuItem("Revenue Report");
        reportsMenu.add(revenueItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);

        // Add menus to menu bar
        menuBar.add(equipmentMenu);
        menuBar.add(rentalMenu);
        menuBar.add(reportsMenu);
        menuBar.add(helpMenu);

        // Add action listeners
        addEquipItem.addActionListener(e -> showAddEquipmentForm());
        viewEquipItem.addActionListener(e -> showEquipmentList());
        rentItem.addActionListener(e -> showRentalForm());
        returnItem.addActionListener(e -> showReturnForm());
        revenueItem.addActionListener(e -> showRevenueReport());
        aboutItem.addActionListener(e -> showAboutDialog());

        return menuBar;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));

        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(new EmptyBorder(2, 5, 2, 5));

        dateLabel = new JLabel();
        dateLabel.setBorder(new EmptyBorder(2, 5, 2, 5));
        updateDateLabel();

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(dateLabel, BorderLayout.EAST);

        return statusBar;
    }

    private void updateDateLabel() {
        LocalDate now = LocalDate.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        dateLabel.setText(dateStr);
    }

    private void setStatus(String text) {
        statusLabel.setText(text);
    }

    // ============ VIEWS ============

    private void showDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout(10, 10));

        // Welcome header
        JLabel welcomeLabel = new JLabel("Welcome to Farm Equipment Rental System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(primaryColor);
        welcomeLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Quick stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.add(createStatPanel("Available Equipment", "15"));
        statsPanel.add(createStatPanel("Active Rentals", "7"));
        statsPanel.add(createStatPanel("Due Today", "3"));

        // Quick access buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JButton rentButton = createActionButton("Rent Equipment", e -> showRentalForm());
        JButton returnButton = createActionButton("Return Equipment", e -> showReturnForm());
        JButton equipButton = createActionButton("View Equipment", e -> showEquipmentList());
        JButton reportButton = createActionButton("Revenue Report", e -> showRevenueReport());

        buttonsPanel.add(rentButton);
        buttonsPanel.add(returnButton);
        buttonsPanel.add(equipButton);
        buttonsPanel.add(reportButton);

        // Add components to dashboard
        dashboard.add(welcomeLabel, BorderLayout.NORTH);
        dashboard.add(statsPanel, BorderLayout.CENTER);
        dashboard.add(buttonsPanel, BorderLayout.SOUTH);

        // Add dashboard to content panel
        contentPanel.add(dashboard, "dashboard");
        cardLayout.show(contentPanel, "dashboard");
        setStatus("Welcome to Farm Equipment Rental System");
    }

    private JPanel createStatPanel(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
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

        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createActionButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(primaryColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(action);

        return button;
    }

    private void showAddEquipmentForm() {
        JPanel formPanel = new JPanel(new BorderLayout(0, 20));

        // Header
        JLabel headerLabel = new JLabel("Add New Equipment", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Form fields
        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
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

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton saveButton = createActionButton("Save Equipment", e -> {
            JOptionPane.showMessageDialog(this, "Equipment added successfully!");
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

        // Add to content and show
        contentPanel.add(formPanel, "addEquipment");
        cardLayout.show(contentPanel, "addEquipment");
        setStatus("Adding new equipment...");
    }

    private void showEquipmentList() {
        JPanel listPanel = new JPanel(new BorderLayout(0, 10));

        // Header with search field
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel headerLabel = new JLabel("Equipment Inventory");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField searchField = new JTextField(15);
        JButton searchButton = createActionButton("Search", null);

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // Equipment table
        String[] columnNames = {"ID", "Name", "Category", "Daily Rate", "Status"};
        Object[][] data = {
                {"EQ001", "John Deere Tractor X100", "Tractor", "$150.00", "Available"},
                {"EQ002", "Harvester H200", "Harvester", "$200.00", "Rented"},
                {"EQ003", "Plough P50", "Plough", "$75.00", "Available"},
                {"EQ004", "Tiller T30", "Tiller", "$50.00", "Maintenance"},
                {"EQ005", "Sprayer S100", "Sprayer", "$45.00", "Available"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable equipmentTable = new JTable(model);
        equipmentTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(equipmentTable);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton = createActionButton("Add New", e -> showAddEquipmentForm());
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> showDashboard());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        // Add components to list panel
        listPanel.add(headerPanel, BorderLayout.NORTH);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        listPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add to content and show
        contentPanel.add(listPanel, "equipmentList");
        cardLayout.show(contentPanel, "equipmentList");
        setStatus("Viewing equipment inventory...");
    }

    private void showRentalForm() {
        JPanel rentalPanel = new JPanel(new BorderLayout(0, 20));

        // Header
        JLabel headerLabel = new JLabel("Rent Equipment", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Form fields
        JPanel fieldsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        fieldsPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        fieldsPanel.add(new JLabel("Customer Name:"));
        JTextField nameField = new JTextField();
        fieldsPanel.add(nameField);

        fieldsPanel.add(new JLabel("Customer Phone:"));
        JTextField phoneField = new JTextField();
        fieldsPanel.add(phoneField);

        fieldsPanel.add(new JLabel("Equipment:"));
        String[] equipment = {"EQ001 - John Deere Tractor X100", "EQ003 - Plough P50", "EQ005 - Sprayer S100"};
        JComboBox<String> equipCombo = new JComboBox<>(equipment);
        fieldsPanel.add(equipCombo);

        fieldsPanel.add(new JLabel("Rental Start Date:"));
        JTextField startDateField = new JTextField(LocalDate.now().toString());
        fieldsPanel.add(startDateField);

        fieldsPanel.add(new JLabel("Rental Duration (days):"));
        JTextField durationField = new JTextField();
        fieldsPanel.add(durationField);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton rentButton = createActionButton("Complete Rental", e -> {
            JOptionPane.showMessageDialog(this, "Equipment rented successfully!");
            showDashboard();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> showDashboard());

        buttonPanel.add(rentButton);
        buttonPanel.add(cancelButton);

        // Add components to rental panel
        rentalPanel.add(headerLabel, BorderLayout.NORTH);
        rentalPanel.add(fieldsPanel, BorderLayout.CENTER);
        rentalPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add to content and show
        contentPanel.add(rentalPanel, "rentalForm");
        cardLayout.show(contentPanel, "rentalForm");
        setStatus("Processing equipment rental...");
    }

    private void showReturnForm() {
        JPanel returnPanel = new JPanel(new BorderLayout(0, 20));

        // Header
        JLabel headerLabel = new JLabel("Return Equipment", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Active rentals table
        String[] columnNames = {"Rental ID", "Equipment", "Customer", "Start Date", "Due Date"};
        Object[][] data = {
                {"RT001", "EQ002 - Harvester H200", "Mary Johnson", "2025-05-10", "2025-05-17"},
                {"RT002", "EQ004 - Tiller T30", "John Smith", "2025-05-15", "2025-05-20"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable rentalsTable = new JTable(model);
        rentalsTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(rentalsTable);

        // Return details panel
        JPanel detailsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        detailsPanel.setBorder(new TitledBorder("Return Details"));

        detailsPanel.add(new JLabel("Selected Rental:"));
        JTextField rentalField = new JTextField();
        rentalField.setEditable(false);
        detailsPanel.add(rentalField);

        detailsPanel.add(new JLabel("Return Date:"));
        JTextField returnDateField = new JTextField(LocalDate.now().toString());
        detailsPanel.add(returnDateField);

        detailsPanel.add(new JLabel("Condition on Return:"));
        String[] conditions = {"Excellent", "Good", "Fair", "Poor", "Damaged"};
        JComboBox<String> conditionCombo = new JComboBox<>(conditions);
        detailsPanel.add(conditionCombo);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton returnButton = createActionButton("Process Return", e -> {
            if (rentalField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a rental first!");
            } else {
                JOptionPane.showMessageDialog(this, "Equipment returned successfully!");
                showDashboard();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> showDashboard());

        buttonPanel.add(returnButton);
        buttonPanel.add(cancelButton);

        // Add listener to populate selected rental
        rentalsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = rentalsTable.getSelectedRow();
                if (row >= 0) {
                    rentalField.setText(data[row][0] + " - " + data[row][1]);
                }
            }
        });

        // Center panel with table and details
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(detailsPanel, BorderLayout.SOUTH);

        // Add components to return panel
        returnPanel.add(headerLabel, BorderLayout.NORTH);
        returnPanel.add(centerPanel, BorderLayout.CENTER);
        returnPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add to content and show
        contentPanel.add(returnPanel, "returnForm");
        cardLayout.show(contentPanel, "returnForm");
        setStatus("Processing equipment return...");
    }

    private void showRevenueReport() {
        JPanel reportPanel = new JPanel(new BorderLayout(0, 20));

        // Header
        JLabel headerLabel = new JLabel("Revenue Report", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        filterPanel.add(new JLabel("Period:"));
        String[] periods = {"This Month", "Last Month", "This Year", "Custom"};
        JComboBox<String> periodCombo = new JComboBox<>(periods);
        filterPanel.add(periodCombo);

        JButton applyButton = new JButton("Apply");
        filterPanel.add(applyButton);

        // Report data
        String[] columnNames = {"Category", "Total Rentals", "Revenue"};
        Object[][] data = {
                {"Tractors", "12", "$1,800.00"},
                {"Harvesters", "5", "$1,000.00"},
                {"Ploughs", "8", "$600.00"},
                {"Tillers", "10", "$500.00"},
                {"Sprayers", "7", "$315.00"},
                {"Other", "3", "$180.00"},
                {"TOTAL", "45", "$4,395.00"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable reportTable = new JTable(model);
        reportTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(reportTable);

        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryPanel.setBorder(new TitledBorder("Summary"));

        summaryPanel.add(createStatPanel("Total Rentals", "45"));
        summaryPanel.add(createStatPanel("Total Revenue", "$4,395.00"));
        summaryPanel.add(createStatPanel("Avg. Rental Value", "$97.67"));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton printButton = new JButton("Print Report");
        JButton exportButton = new JButton("Export to CSV");
        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> showDashboard());

        buttonPanel.add(printButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(backButton);

        // Add components to report panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerLabel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        reportPanel.add(topPanel, BorderLayout.NORTH);
        reportPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.add(summaryPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        reportPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add to content and show
        contentPanel.add(reportPanel, "revenueReport");
        cardLayout.show(contentPanel, "revenueReport");
        setStatus("Viewing revenue report...");
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Farm Equipment Rental System v1.0\n" +
                        "© 2025 Farm Equipment Rental Company\n\n" +
                        "A simplified system for managing farm equipment rentals.",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Main method to run the application
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            SimpleFarmRentalSystem app = new SimpleFarmRentalSystem();
            app.setVisible(true);
        });
    }
}