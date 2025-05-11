package FarmEquipmentRentalSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeComboBox;
    private JButton loginButton;
    private JButton registerButton;
    private JPanel mainPanel;
    private String layoutStyle = "leftImageStyle"; // Default layout
    private JCheckBox showPasswordCheckBox;
    private JLabel forgotPasswordLabel;

    public LoginScreen() {
        setTitle("Farmer Equipment Rental System - Login");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the layout based on selection
        createLayout(layoutStyle);

        setVisible(true);
    }

    private void createLayout(String layoutStyle) {
        switch(layoutStyle) {
            case "leftImageStyle":
                createLeftImageLayout();
                break;
            case "backgroundImageStyle":
                createBackgroundImageLayout();
                break;
            case "headerImageStyle":
                createHeaderImageLayout();
                break;
            default:
                createLeftImageLayout();
        }
    }

    private void createLeftImageLayout() {
        mainPanel = new JPanel(new GridBagLayout());
        setContentPane(mainPanel);
        mainPanel.setBackground(new Color(245, 245, 245));

        // Split into two panels
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(450, 500));
        leftPanel.setBackground(new Color(76, 175, 80)); // Green farm-like color

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(245, 245, 245));

        // Add tractor image to the left panel
        try {
            BufferedImage myPicture = ImageIO.read(new File("resources/tractor.jpg"));
            // If you don't have the image, this will create a placeholder
            if (myPicture == null) {
                JLabel imageLabel = new JLabel("Tractor Image");
                imageLabel.setFont(new Font("Arial", Font.BOLD, 24));
                imageLabel.setForeground(Color.WHITE);
                leftPanel.add(imageLabel);
            } else {
                JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(400, 400, Image.SCALE_SMOOTH)));
                leftPanel.add(picLabel);
            }
        } catch (IOException e) {
            // If image fails to load, add a text placeholder
            JLabel imageLabel = new JLabel("FARMER EQUIPMENT RENTAL");
            imageLabel.setFont(new Font("Arial", Font.BOLD, 24));
            imageLabel.setForeground(Color.WHITE);
            leftPanel.add(imageLabel);
        }

        // Add company name or slogan
        JLabel sloganLabel = new JLabel("Empowering Farmers with Premium Equipment");
        sloganLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        sloganLabel.setForeground(Color.WHITE);
        leftPanel.add(sloganLabel);

        // Setup right panel with form
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Welcome Back!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(76, 175, 80));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Please login to your account");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 20, 10);
        rightPanel.add(subtitleLabel, gbc);

        // Reset insets
        gbc.insets = new Insets(10, 10, 10, 10);

        // User type selection with icon
        JLabel userTypeLabel = new JLabel("Login as: ");
        userTypeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        String[] userTypes = {"Farmer", "Admin", "Guest House"};
        userTypeComboBox = new JComboBox<>(userTypes);
        userTypeComboBox.setPreferredSize(new Dimension(200, 30));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        rightPanel.add(userTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        rightPanel.add(userTypeComboBox, gbc);

        // Username field with icon
        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField = new JTextField(15);
        usernameField.setPreferredSize(new Dimension(200, 30));

        gbc.gridx = 0;
        gbc.gridy = 3;
        rightPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        rightPanel.add(usernameField, gbc);

        // Password field with icon
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField = new JPasswordField(15);
        passwordField.setPreferredSize(new Dimension(200, 30));

        gbc.gridx = 0;
        gbc.gridy = 4;
        rightPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        rightPanel.add(passwordField, gbc);

        // Show/Hide password checkbox
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setBackground(new Color(245, 245, 245));
        showPasswordCheckBox.addActionListener(e -> togglePasswordVisibility());

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        rightPanel.add(showPasswordCheckBox, gbc);

        // Remember me checkbox
        JCheckBox rememberMeBox = new JCheckBox("Remember me");
        rememberMeBox.setBackground(new Color(245, 245, 245));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        rightPanel.add(rememberMeBox, gbc);

        // Buttons
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(76, 175, 80));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(100, 35));

        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(200, 200, 200));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(100, 35));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        rightPanel.add(buttonPanel, gbc);

        // Forgot password link
        forgotPasswordLabel = new JLabel("Forgot Password?");
        forgotPasswordLabel.setForeground(new Color(0, 102, 204));
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.addMouseListener(new ForgotPasswordMouseListener());

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(forgotPasswordLabel, gbc);

        // Add both panels to main container
        mainPanel.setLayout(new GridLayout(1, 2));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        // Button actions
        loginButton.addActionListener(new LoginButtonListener());
        registerButton.addActionListener(e -> {
            dispose();
            // You would need to implement this class
            new RegistrationScreen();
        });
    }

    private void createBackgroundImageLayout() {
        // Create a custom panel that uses a background image
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    BufferedImage bgImage = ImageIO.read(new File("resources/farm_background.jpg"));
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } catch (IOException e) {
                    // If image fails to load, paint a gradient background
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gp = new GradientPaint(
                            0, 0, new Color(100, 180, 100),
                            0, getHeight(), new Color(200, 240, 200));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        setContentPane(mainPanel);
        mainPanel.setLayout(new GridBagLayout());

        // Create a semi-transparent panel for the login form
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(255, 255, 255, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(400, 380)); // Increased height for new components

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 12, 8, 12);

        // Form title
        JLabel titleLabel = new JLabel("Farmer Equipment Rental", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(50, 120, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        // User type selection
        JLabel userTypeLabel = new JLabel("Login as:");
        userTypeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        String[] userTypes = {"Farmer", "Admin", "Guest House"};
        userTypeComboBox = new JComboBox<>(userTypes);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(userTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(userTypeComboBox, gbc);

        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(usernameField, gbc);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(passwordField, gbc);

        // Show/Hide password checkbox
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.addActionListener(e -> togglePasswordVisibility());

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(showPasswordCheckBox, gbc);

        // Buttons
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(76, 175, 80));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);

        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(200, 200, 200));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Forgot password link
        forgotPasswordLabel = new JLabel("Forgot Password?", JLabel.CENTER);
        forgotPasswordLabel.setForeground(new Color(0, 102, 204));
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.addMouseListener(new ForgotPasswordMouseListener());

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(forgotPasswordLabel, gbc);

        // Add form panel to main panel
        mainPanel.add(formPanel);

        // Button actions
        loginButton.addActionListener(new LoginButtonListener());
        registerButton.addActionListener(e -> {
            dispose();
            new RegistrationScreen();
        });
    }

    private void createHeaderImageLayout() {
        mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header panel with image
        JPanel headerPanel = new JPanel();
        headerPanel.setPreferredSize(new Dimension(900, 150));
        headerPanel.setBackground(new Color(76, 175, 80));

        try {
            BufferedImage headerImage = ImageIO.read(new File("resources/farm_banner.png"));
            JLabel headerLabel = new JLabel(new ImageIcon(headerImage.getScaledInstance(900, 150, Image.SCALE_SMOOTH)));
            headerPanel.add(headerLabel);
        } catch (IOException e) {
            // Create a text banner if image is not available
            JLabel headerText = new JLabel("FARMER EQUIPMENT RENTAL SYSTEM");
            headerText.setFont(new Font("Arial", Font.BOLD, 28));
            headerText.setForeground(Color.WHITE);
            headerPanel.add(headerText);
        }

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to the Rental System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(76, 175, 80));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(welcomeLabel, gbc);

        // User type selection
        JLabel userTypeLabel = new JLabel("Login as:");
        userTypeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        String[] userTypes = {"Farmer", "Admin", "Guest House"};
        userTypeComboBox = new JComboBox<>(userTypes);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(userTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(userTypeComboBox, gbc);

        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(usernameField, gbc);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(passwordField, gbc);

        // Show/Hide password checkbox
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setBackground(new Color(245, 245, 245));
        showPasswordCheckBox.addActionListener(e -> togglePasswordVisibility());

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(showPasswordCheckBox, gbc);

        // Buttons
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(76, 175, 80));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(100, 35));

        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(200, 200, 200));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(100, 35));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Forgot password link
        forgotPasswordLabel = new JLabel("Forgot Password?", JLabel.CENTER);
        forgotPasswordLabel.setForeground(new Color(0, 102, 204));
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.addMouseListener(new ForgotPasswordMouseListener());

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(forgotPasswordLabel, gbc);

        // Add panels to main container
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button actions
        loginButton.addActionListener(new LoginButtonListener());
        registerButton.addActionListener(e -> {
            dispose();
            new RegistrationScreen();
        });
    }

    // Toggle password visibility method
    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setEchoChar((char) 0); // Show password
        } else {
            passwordField.setEchoChar('•'); // Hide password with bullet
        }
    }

    // Forgot Password listener
    class ForgotPasswordMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            showForgotPasswordDialog();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            forgotPasswordLabel.setForeground(new Color(0, 51, 102)); // Darker blue on hover
            forgotPasswordLabel.setText("<html><u>Forgot Password?</u></html>"); // Underline text
        }

        @Override
        public void mouseExited(MouseEvent e) {
            forgotPasswordLabel.setForeground(new Color(0, 102, 204)); // Reset to original color
            forgotPasswordLabel.setText("Forgot Password?"); // Remove underline
        }
    }

    // Forgot Password Dialog
    private void showForgotPasswordDialog() {
        JDialog resetDialog = new JDialog(this, "Password Reset", true);
        resetDialog.setSize(400, 250);
        resetDialog.setLocationRelativeTo(this);
        resetDialog.setLayout(new BorderLayout());

        JPanel resetPanel = new JPanel(new GridBagLayout());
        resetPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Instructions
        JLabel instructionLabel = new JLabel("Enter your email to reset your password");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        resetPanel.add(instructionLabel, gbc);

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        resetPanel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        resetPanel.add(emailField, gbc);

        // User type
        JLabel userTypeLabel = new JLabel("Account Type:");
        userTypeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        resetPanel.add(userTypeLabel, gbc);

        String[] userTypes = {"Farmer", "Admin", "Guest House"};
        JComboBox<String> userTypeBox = new JComboBox<>(userTypes);
        gbc.gridx = 1;
        gbc.gridy = 2;
        resetPanel.add(userTypeBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(76, 175, 80));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(200, 200, 200));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        resetPanel.add(buttonPanel, gbc);

        // Add action listeners
        submitButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(resetDialog, "Please enter your email address",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Here you would implement the actual password reset logic
            // For now, we'll just show a confirmation
            JOptionPane.showMessageDialog(resetDialog,
                    "Password reset instructions have been sent to your email",
                    "Reset Initiated", JOptionPane.INFORMATION_MESSAGE);
            resetDialog.dispose();
        });

        cancelButton.addActionListener(e -> resetDialog.dispose());

        resetDialog.add(resetPanel, BorderLayout.CENTER);
        resetDialog.setVisible(true);
    }

    class LoginButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String userType = (String) userTypeComboBox.getSelectedItem();

            if (username.trim().isEmpty() || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both username and password",
                        "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (validateLogin(username, password, userType)) {
                JOptionPane.showMessageDialog(null, "Login successful as " + userType,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                openUserInterface(userType, username);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateLogin(String username, String password, String userType) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Connect to database using the DatabaseConnection class
            conn = DatabaseConnection.connect();

            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Database connection failed",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Choose the appropriate table based on user type
            String tableName;
            switch(userType) {
                case "Farmer":
                    tableName = "farmer_users";
                    break;
                case "Admin":
                    tableName = "farmer_admin";
                    break;
                case "Guest House":
                    tableName = "farmer_guesthouse";
                    break;
                default:
                    return false;
            }

            String query = "SELECT password_hash FROM " + tableName + " WHERE username = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password_hash");
                // In a real app, use secure password hashing like BCrypt
                return password.equals(storedPassword);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    private void openUserInterface(String userType, String username) {
        // Open appropriate interface based on user type
        switch(userType) {
            case "Farmer":
                new FarmerHomePage(username);
                break;
            case "Admin":
                new AdminHomePage(username);
                break;
            case "Guest House":
                new GuestPage();
                break;
            default:
                // Fallback
                JOptionPane.showMessageDialog(null, "Interface not implemented yet");
        }
    }

    // Method to switch between layouts
    public void switchLayout(String newLayoutStyle) {
        getContentPane().removeAll();
        layoutStyle = newLayoutStyle;
        createLayout(layoutStyle);
        revalidate();
        repaint();
    }

    // Main method for testing
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
}