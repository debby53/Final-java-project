package FarmEquipmentRentalSystem;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.util.regex.*;

public class RegistrationScreen extends JFrame {
    // Components
    private JPanel mainPanel;
    private JTextField usernameField, fullNameField, emailField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private JTextArea addressArea;
    private JComboBox<String> userTypeCombo;
    private JRadioButton maleRadio, femaleRadio, otherRadio;
    private ButtonGroup genderGroup;
    private JButton registerButton, resetButton;
    private JLabel titleLabel;
    private JPanel formPanel, buttonPanel;
    private JSpinner farmSizeSpinner;
    private JSpinner capacitySpinner;
    private JComboBox<String> roleCombo;
    private JSpinner accessLevelSpinner;
    private JTextField houseNameField;

    // Database connection
    private Connection conn;
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1522:orcl";
    private static final String USER = "system";
    private static final String PASS = "123";

    // Dynamic fields based on user type
    private JPanel dynamicFieldsPanel;

    public RegistrationScreen() {
        // Set up the frame
        super("Farm Management System - Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);

        // Initialize panels
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        dynamicFieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        // Initialize components
        titleLabel = new JLabel("Farm Management System Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(46, 125, 50));

        usernameField = new JTextField(20);
        fullNameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        addressArea = new JTextArea(4, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScrollPane = new JScrollPane(addressArea);

        // User type combo box
        userTypeCombo = new JComboBox<>(new String[]{"select","Farmer", "Admin", "Guesthouse"});
        userTypeCombo.addActionListener(e -> updateDynamicFields());

        // Gender radio buttons
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        otherRadio = new JRadioButton("Other");
        genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderGroup.add(otherRadio);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        genderPanel.add(otherRadio);

        // Dynamic fields for different user types
        farmSizeSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 10000.0, 0.1));
        roleCombo = new JComboBox<>(new String[]{"System Admin", "Manager", "Support"});
        accessLevelSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        houseNameField = new JTextField(20);
        capacitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        // Buttons
        registerButton = new JButton("Register");
        registerButton.addActionListener(e -> registerUser());
        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetForm());

        // Add components to panels
        formPanel.add(new JLabel("User Type:"));
        formPanel.add(userTypeCombo);
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(fullNameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Confirm Password:"));
        formPanel.add(confirmPasswordField);
        formPanel.add(new JLabel("Gender:"));
        formPanel.add(genderPanel);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressScrollPane);

        buttonPanel.add(registerButton);
        buttonPanel.add(resetButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(dynamicFieldsPanel, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        updateDynamicFields(); // Initialize dynamic fields

        setVisible(true);
    }

    private void updateDynamicFields() {
        dynamicFieldsPanel.removeAll();
        String userType = (String) userTypeCombo.getSelectedItem();

        if ("Farmer".equals(userType)) {
            dynamicFieldsPanel.add(new JLabel("Farm Size (acres):"));
            dynamicFieldsPanel.add(farmSizeSpinner);
        } else if ("Admin".equals(userType)) {
            dynamicFieldsPanel.add(new JLabel("Role:"));
            dynamicFieldsPanel.add(roleCombo);
            dynamicFieldsPanel.add(new JLabel("Access Level:"));
            dynamicFieldsPanel.add(accessLevelSpinner);
        } else if ("Guesthouse".equals(userType)) {
            dynamicFieldsPanel.add(new JLabel("House Name:"));
            dynamicFieldsPanel.add(houseNameField);
            dynamicFieldsPanel.add(new JLabel("Capacity:"));
            dynamicFieldsPanel.add(capacitySpinner);
        }

        dynamicFieldsPanel.revalidate();
        dynamicFieldsPanel.repaint();
    }

    private void registerUser() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        try {
            // Establish database connection
            establishConnection();

            // Insert user data into appropriate table
            String userType = (String) userTypeCombo.getSelectedItem();

            // Hash the password (in a real app, use a proper hashing algorithm)
            String passwordHash = String.valueOf(passwordField.getPassword()); // Normally you'd hash this

            PreparedStatement pstmt = null;

            if ("Farmer".equals(userType)) {
                pstmt = prepareInsertFarmerStatement(passwordHash);
            } else if ("Admin".equals(userType)) {
                pstmt = prepareInsertAdminStatement(passwordHash);
            } else if ("Guesthouse".equals(userType)) {
                pstmt = prepareInsertGuesthouseStatement(passwordHash);
            }

            if (pstmt != null) {
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                            userType + " registered successfully!",
                            "Registration Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                    resetForm();
                }
                pstmt.close();
            }

            conn.close();

        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getMessage().contains("USERNAME")) {
                JOptionPane.showMessageDialog(this,
                        "Username already exists. Please choose a different username.",
                        "Registration Failed",
                        JOptionPane.ERROR_MESSAGE);
            } else if (e.getMessage().contains("EMAIL")) {
                JOptionPane.showMessageDialog(this,
                        "Email already exists. Please use a different email.",
                        "Registration Failed",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Database constraint violation: " + e.getMessage(),
                        "Registration Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Registration Failed",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + e.getMessage(),
                    "Registration Failed",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private PreparedStatement prepareInsertFarmerStatement(String passwordHash) throws SQLException {
        String sql = "INSERT INTO farmer_users (username, password_hash, full_name, email, phone, address, farm_size) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, usernameField.getText());
        pstmt.setString(2, passwordHash);
        pstmt.setString(3, fullNameField.getText());
        pstmt.setString(4, emailField.getText());
        pstmt.setString(5, phoneField.getText());
        pstmt.setString(6, addressArea.getText());
        pstmt.setDouble(7, (Double) farmSizeSpinner.getValue());
        return pstmt;
    }

    private PreparedStatement prepareInsertAdminStatement(String passwordHash) throws SQLException {
        String sql = "INSERT INTO farmer_admin (username, password_hash, full_name, email, role, access_level) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, usernameField.getText());
        pstmt.setString(2, passwordHash);
        pstmt.setString(3, fullNameField.getText());
        pstmt.setString(4, emailField.getText());
        pstmt.setString(5, (String) roleCombo.getSelectedItem());
        pstmt.setInt(6, (Integer) accessLevelSpinner.getValue());
        return pstmt;
    }

    private PreparedStatement prepareInsertGuesthouseStatement(String passwordHash) throws SQLException {
        String sql = "INSERT INTO farmer_guesthouse (username, password_hash, house_name, email, phone, address, capacity) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, usernameField.getText());
        pstmt.setString(2, passwordHash);
        pstmt.setString(3, houseNameField.getText());
        pstmt.setString(4, emailField.getText());
        pstmt.setString(5, phoneField.getText());
        pstmt.setString(6, addressArea.getText());
        pstmt.setInt(7, (Integer) capacitySpinner.getValue());
        return pstmt;
    }

    private void establishConnection() throws SQLException {
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private boolean validateInput() {
        // Username validation
        if (usernameField.getText().trim().isEmpty()) {
            showError("Username cannot be empty");
            return false;
        }

        if (usernameField.getText().length() < 4 || usernameField.getText().length() > 50) {
            showError("Username must be between 4 and 50 characters");
            return false;
        }

        // Full name validation
        if (fullNameField.getText().trim().isEmpty()) {
            showError("Full name cannot be empty");
            return false;
        }

        // Email validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (!pattern.matcher(emailField.getText()).matches()) {
            showError("Please enter a valid email address");
            return false;
        }

        // Password validation
        char[] password = passwordField.getPassword();
        if (password.length < 8) {
            showError("Password must be at least 8 characters long");
            return false;
        }

        // Confirm password
        char[] confirmPassword = confirmPasswordField.getPassword();
        if (!String.valueOf(password).equals(String.valueOf(confirmPassword))) {
            showError("Passwords do not match");
            return false;
        }

        // Gender validation
        if (!maleRadio.isSelected() && !femaleRadio.isSelected() && !otherRadio.isSelected()) {
            showError("Please select a gender");
            return false;
        }

        // Phone validation (optional)
        if (!phoneField.getText().trim().isEmpty()) {
            String phoneRegex = "^[0-9+()-]{6,20}$";
            if (!Pattern.compile(phoneRegex).matcher(phoneField.getText()).matches()) {
                showError("Please enter a valid phone number");
                return false;
            }
        }

        // User type specific validations
        String userType = (String) userTypeCombo.getSelectedItem();

        if ("Guesthouse".equals(userType) && houseNameField.getText().trim().isEmpty()) {
            showError("House name cannot be empty for Guesthouse registration");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    private void resetForm() {
        usernameField.setText("");
        fullNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        addressArea.setText("");
        genderGroup.clearSelection();
        userTypeCombo.setSelectedIndex(0);
        farmSizeSpinner.setValue(1.0);
        roleCombo.setSelectedIndex(0);
        accessLevelSpinner.setValue(1);
        houseNameField.setText("");
        capacitySpinner.setValue(1);
    }

    public static void main(String[] args) {
        try {
            // Set Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new RegistrationScreen());
    }
}