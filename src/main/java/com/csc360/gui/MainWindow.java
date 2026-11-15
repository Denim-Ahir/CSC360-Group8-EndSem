
package com.csc360.gui;

import com.csc360.imageprocessing.ImageLoader;
import com.csc360.utils.ImageValidator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Main desktop window for ImageLite.
 */
public final class MainWindow extends JFrame {

    private File selectedImageFile;
    private JLabel selectedFileLabel;

    private JPanel originalPreviewPanel;
    private JLabel originalPreviewLabel;
    private BufferedImage originalImage;

    public MainWindow() {
        super("ImageLite - Image Compressor");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel heading = new JLabel("ImageLite");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 26f));
        root.add(heading, BorderLayout.NORTH);

        JPanel previews = new JPanel(new GridLayout(1, 2, 12, 0));

        originalPreviewPanel = createOriginalPreview();
        previews.add(originalPreviewPanel);
        previews.add(createPlaceholder("Compressed image"));

        root.add(previews, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setBorder(BorderFactory.createTitledBorder("Image settings"));

        JButton browseButton = new JButton("Browse Image");
        browseButton.addActionListener(event -> selectImage());

        selectedFileLabel = new JLabel("No image selected");

        controls.add(browseButton);
        controls.add(selectedFileLabel);

        root.add(controls, BorderLayout.SOUTH);

        return root;
    }

    private JPanel createOriginalPreview() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Original image"));

        originalPreviewLabel =
                new JLabel("No image selected", SwingConstants.CENTER);

        panel.add(originalPreviewLabel, BorderLayout.CENTER);

        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(
                    java.awt.event.ComponentEvent event) {
                updateOriginalPreview();
            }
        });

        return panel;
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Image");

        FileNameExtensionFilter filter =
                new FileNameExtensionFilter(
                        "Image Files (JPG, JPEG, PNG)",
                        "jpg", "jpeg", "png"
                );

        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                ImageValidator.validateImage(file);

                BufferedImage loadedImage =
                        ImageLoader.loadImage(file);

                selectedImageFile = file;
                originalImage = loadedImage;

                selectedFileLabel.setText(file.getName());

                updateOriginalPreview();

            } catch (IllegalArgumentException | IOException exception) {
                JOptionPane.showMessageDialog(
                        this,
                        exception.getMessage(),
                        "Image Loading Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void updateOriginalPreview() {
        if (originalImage == null || originalPreviewPanel == null) {
            return;
        }

        int panelWidth = originalPreviewPanel.getWidth() - 20;
        int panelHeight = originalPreviewPanel.getHeight() - 45;

        if (panelWidth <= 0 || panelHeight <= 0) {
            return;
        }

        double widthRatio =
                (double) panelWidth / originalImage.getWidth();

        double heightRatio =
                (double) panelHeight / originalImage.getHeight();

        double scale = Math.min(widthRatio, heightRatio);
        scale = Math.min(scale, 1.0);

        int newWidth =
                Math.max(1, (int) (originalImage.getWidth() * scale));

        int newHeight =
                Math.max(1, (int) (originalImage.getHeight() * scale));

        Image scaledImage = originalImage.getScaledInstance(
                newWidth,
                newHeight,
                Image.SCALE_SMOOTH
        );

        originalPreviewLabel.setText(null);
        originalPreviewLabel.setIcon(new ImageIcon(scaledImage));
    }

    private JPanel createPlaceholder(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        panel.add(
                new JLabel("No image selected", SwingConstants.CENTER),
                BorderLayout.CENTER
        );

        return panel;
    }
}
