package FarmEquipmentRentalSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Utility class for loading and managing images in the Farm Equipment Rental System
 */
public class ImageLoader {

    // Store the original image for resizing operations
    private static Image originalImage = null;

    /**
     * Load an image for display in a JLabel
     *
     * @param imagePath The path to the image file
     * @param width The desired width of the image
     * @param height The desired height of the image
     * @param defaultText The text to display if the image cannot be loaded
     * @return An ImageIcon that can be used with a JLabel, or null if loading failed
     */
    public static ImageIcon loadImageIcon(String imagePath, int width, int height, String defaultText) {
        try {
            // Method 1: Try direct file access
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                originalImage = ImageIO.read(imageFile);
                return resizeImage(originalImage, width, height);
            }

            // Method 2: Try using ClassLoader resource stream
            InputStream is = ImageLoader.class.getClassLoader().getResourceAsStream(imagePath);
            if (is != null) {
                originalImage = ImageIO.read(is);
                return resizeImage(originalImage, width, height);
            }

            // Method 3: Try with Class.getResource
            java.net.URL imgURL = ImageLoader.class.getResource("/" + imagePath);
            if (imgURL != null) {
                originalImage = new ImageIcon(imgURL).getImage();
                return resizeImage(originalImage, width, height);
            }

            // Method 4: Try with absolute path from user directory
            String absolutePath = System.getProperty("user.dir") + File.separator + imagePath;
            File absoluteFile = new File(absolutePath);
            if (absoluteFile.exists()) {
                originalImage = ImageIO.read(absoluteFile);
                return resizeImage(originalImage, width, height);
            }

            // Log failure information
            System.out.println("Failed to load image: " + imagePath);
            System.out.println("Tried file path: " + imageFile.getAbsolutePath());
            System.out.println("Tried absolute path: " + absolutePath);

            return null;

        } catch (IOException e) {
            System.out.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Resize an image to the specified dimensions
     *
     * @param image The original image to resize
     * @param width The target width
     * @param height The target height
     * @return A resized ImageIcon
     */
    public static ImageIcon resizeImage(Image image, int width, int height) {
        if (image == null) return null;

        // Calculate proportional scaling
        int originalWidth = image.getWidth(null);
        int originalHeight = image.getHeight(null);

        if (originalWidth <= 0 || originalHeight <= 0) {
            // If we can't get dimensions, just do direct scaling
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }

        // Calculate scaling ratio to maintain aspect ratio
        double widthRatio = (double) width / originalWidth;
        double heightRatio = (double) height / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int scaledWidth = (int) (originalWidth * ratio);
        int scaledHeight = (int) (originalHeight * ratio);

        // Create a high-quality scaled image using BufferedImage and RenderingHints
        BufferedImage bufferedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // Set high quality rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the image with high quality scaling
        g2d.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return new ImageIcon(bufferedImage);
    }

    /**
     * Set an image on a JLabel with a fallback text if loading fails
     *
     * @param label The JLabel to set the image on
     * @param imagePath The path to the image file
     * @param width The desired width of the image
     * @param height The desired height of the image
     * @param fallbackText The text to display if the image cannot be loaded
     * @return true if the image was successfully loaded, false otherwise
     */
    public static boolean setLabelImage(JLabel label, String imagePath, int width, int height, String fallbackText) {
        ImageIcon icon = loadImageIcon(imagePath, width, height, fallbackText);
        if (icon != null) {
            label.setIcon(icon);
            // Store the original image in the label's client property for later resizing
            if (originalImage != null) {
                label.putClientProperty("originalImage", originalImage);
            }
            return true;
        } else {
            label.setText(fallbackText);
            label.setFont(new Font("Arial", Font.ITALIC, 16));
            return false;
        }
    }

    /**
     * Try loading an image from multiple possible paths
     *
     * @param label The JLabel to set the image on
     * @param possiblePaths Array of paths to try
     * @param width The desired width of the image
     * @param height The desired height of the image
     * @param fallbackText The text to display if no image can be loaded
     * @return true if an image was successfully loaded, false otherwise
     */
    public static boolean tryMultiplePaths(JLabel label, String[] possiblePaths, int width, int height, String fallbackText) {
        for (String path : possiblePaths) {
            if (setLabelImage(label, path, width, height, fallbackText)) {
                System.out.println("Successfully loaded image from: " + path);
                return true;
            }
        }

        System.out.println("Failed to load image from any path.");
        if (!fallbackText.isEmpty()) {
            label.setText(fallbackText);
            label.setFont(new Font("Arial", Font.ITALIC, 16));
        } else {
            // Just don't set any text if fallbackText is empty
            label.setText(null);
        }
        return false;
    }

    /**
     * Resizes an image already loaded in a JLabel to new dimensions
     *
     * @param label The JLabel containing the image
     * @param width The new width
     * @param height The new height
     * @return true if resizing was successful, false otherwise
     */
    public static boolean resizeLoadedImage(JLabel label, int width, int height) {
        // Get the original image if stored
        Image image = (Image) label.getClientProperty("originalImage");

        // If no stored image, try to get from current icon
        if (image == null && label.getIcon() instanceof ImageIcon) {
            image = ((ImageIcon) label.getIcon()).getImage();
        }

        if (image != null) {
            ImageIcon resizedIcon = resizeImage(image, width, height);
            label.setIcon(resizedIcon);
            return true;
        }

        return false;
    }
}