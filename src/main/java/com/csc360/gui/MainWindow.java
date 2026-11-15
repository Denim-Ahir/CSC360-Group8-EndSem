
package com.csc360.gui;

import com.csc360.utils.ImageValidator;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import javax.swing.BorderFactory;
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
        previews.add(createPlaceholder("Original image"));
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

                selectedImageFile = file;
                selectedFileLabel.setText(file.getName());

            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(
                        this,
                        exception.getMessage(),
                        "Invalid Image",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
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
