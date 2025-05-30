package FarmEquipmentRentalSystem;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.util.regex.*;

/**
 * Registration Screen for Farm Equipment Rental System
 * Enhanced with a professional color scheme
 */
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

    // Colors
    private static final Color PRIMARY_COLOR = new Color(76, 175, 80);     // Green
    private static final Color SECONDARY_COLOR = new Color(56, 142, 60);   // Darker Green
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Light Blue Background
    private static final Color TEXT_COLOR = new Color(33, 33, 33);         // Dark Gray
    private static final Color BUTTON_COLOR = new Color(76, 175, 80);      // Green
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;            // White
    private static final Color FIELD_BACKGROUND = new Color(255, 255, 255); // White
    private static final Color FIELD_BORDER = new Color(200, 230, 201);    // Light Green

    public RegistrationScreen() {
        // Set up the frame
        super("Farm Management System - Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);

        // Set the background color of the frame
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Initialize panels
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(BACKGROUND_COLOR);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        dynamicFieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        dynamicFieldsPanel.setBackground(BACKGROUND_COLOR);

        // Initialize components
        titleLabel = new JLabel("Farm Management System Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);

        // Add a border to title
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, SECONDARY_COLOR),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));

        usernameField = createStyledTextField();
        fullNameField = createStyledTextField();
        emailField = createStyledTextField();
        phoneField = createStyledTextField();
        passwordField = createStyledPasswordField();
        confirmPasswordField = createStyledPasswordField();

        addressArea = new JTextArea(4, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBackground(FIELD_BACKGROUND);
        addressArea.setForeground(TEXT_COLOR);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane addressScrollPane = new JScrollPane(addressArea);
        addressScrollPane.setBorder(BorderFactory.createLineBorder(FIELD_BORDER, 1));

        // User type combo box
        userTypeCombo = new JComboBox<>(new String[]{"select","Farmer", "Admin", "Guesthouse"});
        userTypeCombo.setBackground(FIELD_BACKGROUND);
        userTypeCombo.setForeground(TEXT_COLOR);
        userTypeCombo.setBorder(BorderFactory.createLineBorder(FIELD_BORDER, 1));
        userTypeCombo.addActionListener(e -> updateDynamicFields());

        // Gender radio buttons
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        otherRadio = new JRadioButton("Other");

        // Style radio buttons
        styleRadioButton(maleRadio);
        styleRadioButton(femaleRadio);
        styleRadioButton(otherRadio);

        genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderGroup.add(otherRadio);

        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setBackground(BACKGROUND_COLOR);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        genderPanel.add(otherRadio);

        // Dynamic fields for different user types
        farmSizeSpinner = createStyledSpinner(new SpinnerNumberModel(1.0, 0.1, 10000.0, 0.1));
        roleCombo = new JComboBox<>(new String[]{"System Admin", "Manager", "Support"});
        styleComboBox(roleCombo);

        accessLevelSpinner = createStyledSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        houseNameField = createStyledTextField();
        capacitySpinner = createStyledSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        // Buttons
        registerButton = new JButton("Register");
        styleButton(registerButton, true);
        registerButton.addActionListener(e -> registerUser());

        resetButton = new JButton("Reset");
        styleButton(resetButton, false);
        resetButton.addActionListener(e -> resetForm());
        JButton backToLoginButton = new JButton("Back to Login");
        styleButton(backToLoginButton, false); // Optional

        backToLoginButton.addActionListener(e -> {
            dispose(); // Close current window
             new LoginScreen().setVisible(true); // Uncomment if LoginScreen exists
        });




        // Style labels and add components to panels
        formPanel.add(createStyledLabel("User Type:"));
        formPanel.add(userTypeCombo);
        formPanel.add(createStyledLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(createStyledLabel("Full Name:"));
        formPanel.add(fullNameField);
        formPanel.add(createStyledLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(createStyledLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(createStyledLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(createStyledLabel("Confirm Password:"));
        formPanel.add(confirmPasswordField);
        formPanel.add(createStyledLabel("Gender:"));
        formPanel.add(genderPanel);
        formPanel.add(createStyledLabel("Address:"));
        formPanel.add(addressScrollPane);

        buttonPanel.add(registerButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(backToLoginButton);

        // Add a header panel with some padding
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Create a panel for the dynamic fields and buttons
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(BACKGROUND_COLOR);
        southPanel.add(dynamicFieldsPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);
        updateDynamicFields(); // Initialize dynamic fields

        setVisible(true);
    }

    // Helper methods for styling components
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setBackground(FIELD_BACKGROUND);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setBackground(FIELD_BACKGROUND);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

//    private JSpinner createStyledSpinner(SpinnerModel model) {
//        JSpinner spinner = new JSpinner(model);
//        spinner.setBackground(FIELD_BACKGROUND);
//        spinner.setBorder(BorderFactory.createLineBorder(FIELD_BORDER, 1));
//
//        JComponent editor = spinner.getEditor();
//        if (editor instanceof JSpinner.DefaultEditor) {
//            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor)editor;
//            spinnerEditor.getTextField().setBackground(FIELD_BACKGROUND);
//            spinnerEditor.getTextField().setForeground(TEXT_COLOR);
//        }
//
//        return spinner;
//    }
private JSpinner createStyledSpinner(SpinnerModel model) {
    JSpinner spinner = new JSpinner(model);
    spinner.setBackground(new Color(240, 244, 248));
    spinner.setForeground(new Color(51, 51, 51));
    spinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(144, 202, 249), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
    ));

    JComponent editor = spinner.getEditor();
    if (editor instanceof JSpinner.DefaultEditor) {
        JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
        spinnerEditor.getTextField().setBackground(new Color(240, 244, 248));
        spinnerEditor.getTextField().setForeground(new Color(51, 51, 51));
        spinnerEditor.getTextField().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    return spinner;
}


//    private void styleComboBox(JComboBox<String> comboBox) {
//        comboBox.setBackground(FIELD_BACKGROUND);
//        comboBox.setForeground(TEXT_COLOR);
//        comboBox.setBorder(BorderFactory.createLineBorder(FIELD_BORDER, 1));
//    }
private void styleComboBox(JComboBox<String> comboBox) {
    comboBox.setBackground(new Color(240, 244, 248));
    comboBox.setForeground(new Color(51, 51, 51));
    comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(144, 202, 249), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
    ));
}


    private void styleRadioButton(JRadioButton radio) {
        radio.setBackground(BACKGROUND_COLOR);
        radio.setForeground(TEXT_COLOR);
        radio.setFocusPainted(false);
        radio.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void styleButton(JButton button, boolean isPrimary) {
        if (isPrimary) {
            button.setBackground(BUTTON_COLOR);
            button.setForeground(BUTTON_TEXT_COLOR);
        } else {
            button.setBackground(Color.LIGHT_GRAY);
            button.setForeground(TEXT_COLOR);
        }

        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 40));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(SECONDARY_COLOR);
                } else {
                    button.setBackground(new Color(220, 220, 220));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(BUTTON_COLOR);
                } else {
                    button.setBackground(Color.LIGHT_GRAY);
                }
            }
        });
    }

    private void updateDynamicFields() {
        dynamicFieldsPanel.removeAll();
        String userType = (String) userTypeCombo.getSelectedItem();

        if ("Farmer".equals(userType)) {
            dynamicFieldsPanel.add(createStyledLabel("Farm Size (acres):"));
            dynamicFieldsPanel.add(farmSizeSpinner);
        } else if ("Admin".equals(userType)) {
            dynamicFieldsPanel.add(createStyledLabel("Role:"));
            dynamicFieldsPanel.add(roleCombo);
            dynamicFieldsPanel.add(createStyledLabel("Access Level:"));
            dynamicFieldsPanel.add(accessLevelSpinner);
        } else if ("Guesthouse".equals(userType)) {
            dynamicFieldsPanel.add(createStyledLabel("House Name:"));
            dynamicFieldsPanel.add(houseNameField);
            dynamicFieldsPanel.add(createStyledLabel("Capacity:"));
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
//        if (!phoneField.getText().trim().isEmpty()) {
//            String phoneRegex = "^[0-9+()-]{6,20}$";
//            if (!Pattern.compile(phoneRegex).matcher(phoneField.getText()).matches()) {
//                showError("Please enter a valid phone number");
//                return false;
//            }
//        }

        String phoneNumber = phoneField.getText().trim(); // Get user input

        if (phoneNumber.matches("^07\\d{8}$")) {
            // Valid phone number
        } else {
            JOptionPane.showMessageDialog(null,
                    "Phone number must start with 07 and be exactly 10 digits.",
                    "Invalid Phone Number", JOptionPane.ERROR_MESSAGE);
        }


        // User type specific validations
        String userType = (String) userTypeCombo.getSelectedItem();

        if ("select".equals(userType)) {
            showError("Please select a user type");
            return false;
        }

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