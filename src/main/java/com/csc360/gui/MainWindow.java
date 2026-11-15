
package com.csc360.gui;

import com.csc360.compression.ImageProcessingOptions;
import com.csc360.compression.ImageProcessingResult;
import com.csc360.compression.ImageProcessingService;
import com.csc360.compression.CompressionMetrics;
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
import java.nio.file.Files;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

    private JPanel compressedPreviewPanel;
    private JLabel compressedPreviewLabel;
    private BufferedImage compressedImage;

    private JSlider qualitySlider;
    private JLabel qualityValueLabel;

    private JTextField widthField;
    private JTextField heightField;
    private JCheckBox preserveAspectRatioCheckBox;

    private JButton saveButton;

    private JLabel originalSizeLabel;
    private JLabel compressedSizeLabel;
    private JLabel bytesSavedLabel;
    private JLabel reductionLabel;
    private JLabel ratioLabel;

    private ImageProcessingResult compressedResult;
    private boolean updatingDimensions;

    private final ImageProcessingService processingService =
            new ImageProcessingService();

    public MainWindow() {
        super("ImageLite - Image Compressor");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        setSize(1200, 850);
        setLocationRelativeTo(null);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel heading = new JLabel("ImageLite");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 26f));
        root.add(heading, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(12, 12));

        JPanel previews = new JPanel(new GridLayout(1, 2, 12, 0));

        originalPreviewPanel = createOriginalPreview();
        compressedPreviewPanel = createCompressedPreview();

        previews.add(originalPreviewPanel);
        previews.add(compressedPreviewPanel);

        center.add(previews, BorderLayout.CENTER);
        center.add(createResultsPanel(), BorderLayout.SOUTH);

        root.add(center, BorderLayout.CENTER);
        root.add(createControlsPanel(), BorderLayout.SOUTH);

        return root;
    }

    private JPanel createControlsPanel() {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setBorder(BorderFactory.createTitledBorder("Image settings"));

        JButton browseButton = new JButton("Browse Image");
        browseButton.addActionListener(event -> selectImage());

        JButton compressButton = new JButton("Compress Image");
        compressButton.addActionListener(event -> compressImage());

        saveButton = new JButton("Save As");
        saveButton.setEnabled(false);
        saveButton.addActionListener(event -> saveCompressedImage());

        selectedFileLabel = new JLabel("No image selected");

        qualitySlider = new JSlider(10, 100, 80);
        qualitySlider.setMajorTickSpacing(10);
        qualitySlider.setMinorTickSpacing(5);
        qualitySlider.setPaintTicks(true);
        qualitySlider.setPaintLabels(true);

        qualityValueLabel = new JLabel("80%");

        qualitySlider.addChangeListener(event ->
                qualityValueLabel.setText(qualitySlider.getValue() + "%"));

        widthField = new JTextField(5);
        heightField = new JTextField(5);

        preserveAspectRatioCheckBox =
                new JCheckBox("Preserve aspect ratio", true);

        addDimensionListeners();

        controls.add(browseButton);
        controls.add(compressButton);
        controls.add(saveButton);

        controls.add(new JLabel("Quality:"));
        controls.add(qualitySlider);
        controls.add(qualityValueLabel);

        controls.add(new JLabel("Width:"));
        controls.add(widthField);

        controls.add(new JLabel("Height:"));
        controls.add(heightField);

        controls.add(preserveAspectRatioCheckBox);
        controls.add(selectedFileLabel);

        return controls;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Compression results"));

        originalSizeLabel = new JLabel("Original size: -");
        compressedSizeLabel = new JLabel("Compressed size: -");
        bytesSavedLabel = new JLabel("Bytes saved: -");
        reductionLabel = new JLabel("Size reduction: -");
        ratioLabel = new JLabel("Compression ratio: -");

        panel.add(originalSizeLabel);
        panel.add(compressedSizeLabel);
        panel.add(bytesSavedLabel);
        panel.add(reductionLabel);
        panel.add(ratioLabel);

        return panel;
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

    private JPanel createCompressedPreview() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Compressed image"));

        compressedPreviewLabel =
                new JLabel("No compressed image yet", SwingConstants.CENTER);

        panel.add(compressedPreviewLabel, BorderLayout.CENTER);

        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(
                    java.awt.event.ComponentEvent event) {
                updateCompressedPreview();
            }
        });

        return panel;
    }

    private void addDimensionListeners() {
        widthField.getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent event) {
                        updateHeightFromWidth();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent event) {
                        updateHeightFromWidth();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent event) {
                        updateHeightFromWidth();
                    }
                });

        heightField.getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent event) {
                        updateWidthFromHeight();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent event) {
                        updateWidthFromHeight();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent event) {
                        updateWidthFromHeight();
                    }
                });
    }

    private void updateHeightFromWidth() {
        if (updatingDimensions || originalImage == null
                || !preserveAspectRatioCheckBox.isSelected()) {
            return;
        }

        try {
            int width = Integer.parseInt(widthField.getText().trim());

            if (width <= 0) {
                return;
            }

            int height = Math.max(1, (int) Math.round(
                    originalImage.getHeight()
                            * (width / (double) originalImage.getWidth())));

            updatingDimensions = true;
            heightField.setText(String.valueOf(height));

        } catch (NumberFormatException exception) {
            // Wait until a valid width is entered.
        } finally {
            updatingDimensions = false;
        }
    }

    private void updateWidthFromHeight() {
        if (updatingDimensions || originalImage == null
                || !preserveAspectRatioCheckBox.isSelected()) {
            return;
        }

        try {
            int height = Integer.parseInt(heightField.getText().trim());

            if (height <= 0) {
                return;
            }

            int width = Math.max(1, (int) Math.round(
                    originalImage.getWidth()
                            * (height / (double) originalImage.getHeight())));

            updatingDimensions = true;
            widthField.setText(String.valueOf(width));

        } catch (NumberFormatException exception) {
            // Wait until a valid height is entered.
        } finally {
            updatingDimensions = false;
        }
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

                BufferedImage loadedImage = ImageLoader.loadImage(file);

                selectedImageFile = file;
                originalImage = loadedImage;

                compressedImage = null;
                compressedResult = null;
                saveButton.setEnabled(false);

                compressedPreviewLabel.setIcon(null);
                compressedPreviewLabel.setText("No compressed image yet");

                selectedFileLabel.setText(file.getName());

                updatingDimensions = true;
                widthField.setText(String.valueOf(originalImage.getWidth()));
                heightField.setText(String.valueOf(originalImage.getHeight()));
                updatingDimensions = false;

                resetResults();
                updateOriginalPreview();

            } catch (IllegalArgumentException | IOException exception) {
                updatingDimensions = false;

                JOptionPane.showMessageDialog(
                        this,
                        exception.getMessage(),
                        "Image Loading Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void compressImage() {
        if (selectedImageFile == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an image first.",
                    "No Image Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            int quality = qualitySlider.getValue();
            int width = Integer.parseInt(widthField.getText().trim());
            int height = Integer.parseInt(heightField.getText().trim());

            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException(
                        "Width and height must be greater than zero.");
            }

            ImageProcessingOptions options =
                    ImageProcessingOptions.resized(
                            quality,
                            width,
                            height,
                            preserveAspectRatioCheckBox.isSelected()
                    );

            ImageProcessingResult result =
                    processingService.process(selectedImageFile, options);

            compressedResult = result;
            compressedImage = result.getCompressedPreview();

            updateCompressedPreview();
            updateResults(result.getMetrics());
            saveButton.setEnabled(true);

        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numeric width and height values.",
                    "Invalid Dimensions",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (IOException | IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Compression Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateResults(CompressionMetrics metrics) {
        originalSizeLabel.setText(
                "Original size: " + formatBytes(metrics.getOriginalBytes()));

        compressedSizeLabel.setText(
                "Compressed size: " + formatBytes(metrics.getCompressedBytes()));

        bytesSavedLabel.setText(
                "Bytes saved: " + formatBytes(metrics.getBytesSaved()));

        reductionLabel.setText(String.format(
                "Size reduction: %.2f%%", metrics.getReductionPercent()));

        double ratio = metrics.getCompressionRatio();

        ratioLabel.setText(Double.isNaN(ratio)
                ? "Compression ratio: N/A"
                : String.format("Compression ratio: %.2f:1", ratio));
    }

    private void resetResults() {
        originalSizeLabel.setText("Original size: -");
        compressedSizeLabel.setText("Compressed size: -");
        bytesSavedLabel.setText("Bytes saved: -");
        reductionLabel.setText("Size reduction: -");
        ratioLabel.setText("Compression ratio: -");
    }

    private String formatBytes(long bytes) {
        if (bytes < 0) {
            return "-" + formatBytes(-bytes);
        }
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        }
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }

    private void saveCompressedImage() {
        if (compressedResult == null || selectedImageFile == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please compress an image before saving.",
                    "No Compressed Image",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String originalName = selectedImageFile.getName();
        int extensionIndex = originalName.lastIndexOf('.');

        String baseName = extensionIndex > 0
                ? originalName.substring(0, extensionIndex)
                : originalName;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Compressed Image");
        fileChooser.setFileFilter(
                new FileNameExtensionFilter("JPEG Image (*.jpg)", "jpg"));

        fileChooser.setSelectedFile(
                new File(baseName + "_compressed.jpg"));

        int result = fileChooser.showSaveDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File destination = fileChooser.getSelectedFile();
        String destinationName = destination.getName();

        int destinationExtension = destinationName.lastIndexOf('.');
        if (destinationExtension > 0) {
            destinationName =
                    destinationName.substring(0, destinationExtension);
        }

        destination = new File(
                destination.getParentFile(),
                destinationName + ".jpg");

        try {
            if (destination.getCanonicalFile().equals(
                    selectedImageFile.getCanonicalFile())) {
                JOptionPane.showMessageDialog(
                        this,
                        "You cannot overwrite the original image. "
                                + "Choose a different filename.",
                        "Original Image Protected",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (destination.exists()) {
                int overwriteChoice = JOptionPane.showConfirmDialog(
                        this,
                        "This file already exists. Do you want to replace it?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (overwriteChoice != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            Files.write(destination.toPath(), compressedResult.getJpegBytes());

            JOptionPane.showMessageDialog(
                    this,
                    "Compressed image saved successfully:\n"
                            + destination.getAbsolutePath(),
                    "Image Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not save the compressed image:\n"
                            + exception.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
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

        Image scaledImage = scaleImage(
                originalImage, panelWidth, panelHeight);

        originalPreviewLabel.setText(null);
        originalPreviewLabel.setIcon(new ImageIcon(scaledImage));
    }

    private void updateCompressedPreview() {
        if (compressedImage == null || compressedPreviewPanel == null) {
            return;
        }

        int panelWidth = compressedPreviewPanel.getWidth() - 20;
        int panelHeight = compressedPreviewPanel.getHeight() - 45;

        if (panelWidth <= 0 || panelHeight <= 0) {
            return;
        }

        Image scaledImage = scaleImage(
                compressedImage, panelWidth, panelHeight);

        compressedPreviewLabel.setText(null);
        compressedPreviewLabel.setIcon(new ImageIcon(scaledImage));
    }

    private Image scaleImage(
            BufferedImage image, int panelWidth, int panelHeight) {

        double widthRatio =
                (double) panelWidth / image.getWidth();

        double heightRatio =
                (double) panelHeight / image.getHeight();

        double scale = Math.min(widthRatio, heightRatio);
        scale = Math.min(scale, 1.0);

        int newWidth =
                Math.max(1, (int) (image.getWidth() * scale));

        int newHeight =
                Math.max(1, (int) (image.getHeight() * scale));

        return image.getScaledInstance(
                newWidth, newHeight, Image.SCALE_SMOOTH);
    }
}
