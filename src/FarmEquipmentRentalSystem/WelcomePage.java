package FarmEquipmentRentalSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class WelcomePage extends JFrame {
    private JButton loginButton;
    private JButton signupButton;
    private JLabel imageLabel;
    private JPanel imagePanel;
    private JPanel contentPanel;
    private JPanel descriptionPanel;

    public WelcomePage() {
        setTitle("Farm Equipment Rental System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(600, 450)); // Set minimum size

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(new Color(245, 245, 240)); // Light cream background

        // Header panel with system name
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 153, 0)); // Green header
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Farm Equipment Rental System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Content panel with responsive layout
        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 245, 240));

        // Left panel for description
        descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBackground(new Color(245, 245, 240));

        JLabel welcomeLabel = new JLabel("Welcome to Agricultural Tools Rental");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(76, 153, 0));

        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setText(
                "Our platform connects farmers with the agricultural tools they need, " +
                        "when they need them, at affordable prices.\n\n" +
                        "Benefits:\n" +
                        "• Access modern farming equipment without high purchase costs\n" +
                        "• Wide range of tools from tractors to specialized harvesting equipment\n" +
                        "• Simple booking and secure payment\n" +
                        "• Equipment delivered to your farm\n" +
                        "• Maintenance and support included\n\n" +
                        "Sign up now to browse our equipment catalog or log in to your existing account."
        );
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(new Color(245, 245, 240));
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(new Color(245, 245, 240));

        loginButton = new JButton("Log In");
        signupButton = new JButton("Sign Up");

        styleButton(loginButton);
        styleButton(signupButton);

        buttonsPanel.add(loginButton);
        buttonsPanel.add(signupButton);

        // Add components to description panel
        descriptionPanel.add(welcomeLabel, BorderLayout.NORTH);
        descriptionPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        descriptionPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Right panel for image
        imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(245, 245, 240));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        imageLabel = new JLabel("Loading image...", JLabel.CENTER);
        imageLabel.setFont(new Font("Arial", Font.ITALIC, 16));

        // Load image using the utility class
        String[] possiblePaths = {
                "resources/farm_equipment.jpg",
                "farm_equipment.jpg",
                "./resources/farm_equipment.jpg",
                "../resources/farm_equipment.jpg",
                "src/resources/farm_equipment.jpg",
                "src/main/resources/farm_equipment.jpg",
                "bin/resources/farm_equipment.jpg",
                "target/classes/resources/farm_equipment.jpg"
        };

        // Try to load the image from any of the possible paths
        boolean imageLoaded = ImageLoader.tryMultiplePaths(
                imageLabel,
                possiblePaths,
                350, 350,
                "Agricultural Tools Image"
        );

        if (imageLoaded) {
            System.out.println("Image loaded successfully!");
        } else {
            createDefaultImage();
        }

        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Add panels to content panel with GridBagLayout for responsive behavior
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 10);
        contentPanel.add(descriptionPanel, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 0, 0);
        contentPanel.add(imagePanel, gbc);

        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(245, 245, 240));

        JLabel footerLabel = new JLabel("© 2025 Farm Equipment Rental System");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerLabel.setForeground(Color.GRAY);

        footerPanel.add(footerLabel);

        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Register action listeners
        loginButton.addActionListener(e -> {
            new LoginScreen();
            dispose();
        });

        signupButton.addActionListener(e -> {
            new RegistrationScreen();
            dispose();
        });

        // Add component listener for resize functionality
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                handleResize();
            }
        });

        setVisible(true);
    }


    private void handleResize() {
        int width = getWidth();

        // If window is narrow, switch to vertical layout
        if (width < 700) {
            contentPanel.removeAll();
            contentPanel.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 0.6;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 10, 0);
            contentPanel.add(descriptionPanel, gbc);

            gbc.weighty = 0.4;
            gbc.gridy = 1;
            gbc.insets = new Insets(10, 0, 0, 0);
            contentPanel.add(imagePanel, gbc);
        } else {
            // Switch to horizontal layout for wider windows
            contentPanel.removeAll();
            contentPanel.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 0.5;
            gbc.weighty = 1.0;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 0, 10);
            contentPanel.add(descriptionPanel, gbc);

            gbc.gridx = 1;
            gbc.insets = new Insets(0, 10, 0, 0);
            contentPanel.add(imagePanel, gbc);
        }

        contentPanel.revalidate();
        contentPanel.repaint();


        resizeImage();
    }


    private void resizeImage() {
        // If we have a placeholder instead of an image, don't resize
        if (imageLabel.getIcon() == null) {
            return;
        }

        int panelWidth = imagePanel.getWidth() - 20; // Padding
        int panelHeight = imagePanel.getHeight() - 20; // Padding

        if (panelWidth > 0 && panelHeight > 0) {
            // Try to use the ImageLoader's resize functionality first
            Object originalImg = imageLabel.getClientProperty("originalImage");

            if (originalImg instanceof Image) {
                // Use high quality resizing from ImageLoader
                ImageIcon resizedIcon = ImageLoader.resizeImage((Image)originalImg, panelWidth, panelHeight);
                imageLabel.setIcon(resizedIcon);
            } else if (imageLabel.getIcon() instanceof ImageIcon) {
                // If we don't have the original stored, recreate the default image
                createDefaultImage(panelWidth, panelHeight);
            }
        }
    }

    /**
     * Creates a default image if no external image can be loaded
     */
    private void createDefaultImage() {
        createDefaultImage(350, 350);
    }

    /**
     * Creates a default image with custom dimensions
     */
    private void createDefaultImage(int width, int height) {
        // Create a blank image
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // Set rendering hints for better quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill background
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, width, height);

        // Draw a simple tractor - scale based on image size
        int scale = Math.min(width, height) / 400;
        if (scale < 1) scale = 1;

        // Calculate positions relative to dimensions
        int tractorX = width / 8;
        int tractorY = height / 3;
        int tractorWidth = width / 2;
        int tractorHeight = height / 6;

        // Tractor body
        g2d.setColor(new Color(0, 128, 0)); // Green
        g2d.fillRect(tractorX, tractorY, tractorWidth, tractorHeight);

        // Tractor cabin
        g2d.setColor(new Color(70, 130, 180)); // Steel blue
        g2d.fillRect(tractorX + tractorWidth - (tractorWidth/5), tractorY - (tractorHeight),
                tractorWidth/3, tractorHeight);

        // Wheels
        int wheelSize = tractorHeight * 3/4;
        g2d.setColor(Color.BLACK);
        g2d.fillOval(tractorX + wheelSize/2, tractorY + tractorHeight - wheelSize/2, wheelSize, wheelSize);
        g2d.fillOval(tractorX + tractorWidth - wheelSize - wheelSize/2, tractorY + tractorHeight - wheelSize/2, wheelSize, wheelSize);

        // Wheel centers
        g2d.setColor(Color.LIGHT_GRAY);
        int wheelCenterSize = wheelSize / 2;
        g2d.fillOval(tractorX + wheelSize/2 + wheelSize/4, tractorY + tractorHeight - wheelSize/2 + wheelSize/4, wheelCenterSize, wheelCenterSize);
        g2d.fillOval(tractorX + tractorWidth - wheelSize - wheelSize/2 + wheelSize/4, tractorY + tractorHeight - wheelSize/2 + wheelSize/4, wheelCenterSize, wheelCenterSize);

        // Text
        g2d.setColor(Color.BLACK);
        int fontSize = Math.max(12, height / 15);
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
        g2d.drawString("Farm Equipment", width/10, height/10);
        g2d.drawString("Rental System", width/8, height/6);

        g2d.dispose();

        // Set the image on the label
        imageLabel.setIcon(new ImageIcon(bufferedImage));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(76, 153, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        // Ensure the text is visible against the background
        button.setOpaque(true); // Make sure the background color is applied
        button.setBorderPainted(false); // Remove the default border

        // Button hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 180, 0));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(76, 153, 0));
            }
        });
    }

    // Main method to start the application
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new WelcomePage());
    }
}