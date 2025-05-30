package FarmEquipmentRentalSystem;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class RegistrationScreenDesign {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Farm Management System - Registration");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 700);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(createRegistrationPanel());
            frame.setVisible(true);
        });
    }

    public static JPanel createRegistrationPanel() {
        // Colors
        Color PRIMARY_COLOR = new Color(76, 175, 80);     // Green
        Color SECONDARY_COLOR = new Color(56, 142, 60);   // Darker Green
        Color BACKGROUND_COLOR = new Color(240, 248, 255); // Light Blue Background
        Color TEXT_COLOR = new Color(33, 33, 33);         // Dark Gray
        Color BUTTON_COLOR = new Color(76, 175, 80);      // Green
        Color BUTTON_TEXT_COLOR = Color.WHITE;            // White
        Color FIELD_BACKGROUND = new Color(255, 255, 255); // White
        Color FIELD_BORDER = new Color(200, 230, 201);    // Light Green

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Title Label
        JLabel titleLabel = new JLabel("Farm Management System Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, SECONDARY_COLOR),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(BACKGROUND_COLOR);

        // Labels and Input Fields (placeholders)
        formPanel.add(createStyledLabel("User Type:", TEXT_COLOR));
        formPanel.add(createStyledComboBox(new String[]{"select", "Farmer", "Admin", "Guesthouse"}, FIELD_BACKGROUND, TEXT_COLOR, FIELD_BORDER));
        formPanel.add(createStyledLabel("Username:", TEXT_COLOR));
        formPanel.add(createStyledTextField(FIELD_BACKGROUND, TEXT_COLOR, FIELD_BORDER));
        formPanel.add(createStyledLabel("Full Name:", TEXT_COLOR));
        formPanel.add(createStyledTextField(FIELD_BACKGROUND, TEXT_COLOR, FIELD_BORDER));
        formPanel.add(createStyledLabel("Email:", TEXT_COLOR));
        formPanel.add(createStyledTextField(FIELD_BACKGROUND, TEXT_COLOR, FIELD_BORDER));
        formPanel.add(createStyledLabel("Phone:", TEXT_COLOR));
        formPanel.add(createStyledTextField(FIELD_BACKGROUND, TEXT_COLOR, FIELD_BORDER));
        formPanel.add(createStyledLabel("Password:", TEXT_COLOR));
        formPanel.add(createStyledPasswordField(FIELD_BACKGROUND, TEXT_COLOR, FIELD_BORDER));
        formPanel.add(createStyledLabel("Confirm Password:", TEXT_COLOR));
        formPanel.add(createStyledPasswordField(FIELD_BACKGROUND, TEXT_COLOR, FIELD_BORDER));
        formPanel.add(createStyledLabel("Gender:", TEXT_COLOR));
        formPanel.add(createGenderRadioPanel(BACKGROUND_COLOR, TEXT_COLOR));
        formPanel.add(createStyledLabel("Address:", TEXT_COLOR));
        formPanel.add(createStyledTextAreaScrollPane(FIELD_BACKGROUND, TEXT_COLOR, FIELD_BORDER));

        // Dynamic Fields Panel (initially empty)
        JPanel dynamicFieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        dynamicFieldsPanel.setBackground(BACKGROUND_COLOR);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(createStyledButton("Register", true, BUTTON_COLOR, BUTTON_TEXT_COLOR, SECONDARY_COLOR, TEXT_COLOR));
        buttonPanel.add(createStyledButton("Reset", false, Color.LIGHT_GRAY, TEXT_COLOR, new Color(220, 220, 220), TEXT_COLOR));
        buttonPanel.add(createStyledButton("Back to Login", false, Color.LIGHT_GRAY, TEXT_COLOR, new Color(220, 220, 220), TEXT_COLOR));

        // Layout the main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(BACKGROUND_COLOR);
        southPanel.add(dynamicFieldsPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private static JLabel createStyledLabel(String text, Color textColor) {
        JLabel label = new JLabel(text);
        label.setForeground(textColor);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }

    private static JTextField createStyledTextField(Color background, Color textColor, Color borderColor) {
        JTextField field = new JTextField(20);
        field.setBackground(background);
        field.setForeground(textColor);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private static JPasswordField createStyledPasswordField(Color background, Color textColor, Color borderColor) {
        JPasswordField field = new JPasswordField(20);
        field.setBackground(background);
        field.setForeground(textColor);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private static JComboBox<String> createStyledComboBox(String[] items, Color background, Color textColor, Color borderColor) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setBackground(background);
        comboBox.setForeground(textColor);
        comboBox.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        return comboBox;
    }

    private static JPanel createGenderRadioPanel(Color backgroundColor, Color textColor) {
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setBackground(backgroundColor);
        JRadioButton maleRadio = createStyledRadioButton("Male", backgroundColor, textColor);
        JRadioButton femaleRadio = createStyledRadioButton("Female", backgroundColor, textColor);
        JRadioButton otherRadio = createStyledRadioButton("Other", backgroundColor, textColor);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderGroup.add(otherRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        genderPanel.add(otherRadio);
        return genderPanel;
    }

    private static JRadioButton createStyledRadioButton(String text, Color backgroundColor, Color textColor) {
        JRadioButton radio = new JRadioButton(text);
        radio.setBackground(backgroundColor);
        radio.setForeground(textColor);
        radio.setFocusPainted(false);
        radio.setFont(new Font("Arial", Font.PLAIN, 14));
        return radio;
    }

    private static JScrollPane createStyledTextAreaScrollPane(Color background, Color textColor, Color borderColor) {
        JTextArea addressArea = new JTextArea(4, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBackground(background);
        addressArea.setForeground(textColor);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane addressScrollPane = new JScrollPane(addressArea);
        addressScrollPane.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        return addressScrollPane;
    }

    private static JButton createStyledButton(String text, boolean isPrimary, Color primaryColor, Color primaryTextColor, Color secondaryColor, Color secondaryTextColor) {
        JButton button = new JButton(text);
        if (isPrimary) {
            button.setBackground(primaryColor);
            button.setForeground(primaryTextColor);
        } else {
            button.setBackground(Color.LIGHT_GRAY);
            button.setForeground(secondaryTextColor);
        }
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 40));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(secondaryColor);
                } else {
                    button.setBackground(new Color(220, 220, 220));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(primaryColor);
                } else {
                    button.setBackground(Color.LIGHT_GRAY);
                }
            }
        });
        return button;
    }
}