package com.csc360.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * Main desktop window for ImageLite.
 */
public final class MainWindow extends JFrame {

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

        JPanel controls = new JPanel();
        controls.setBorder(BorderFactory.createTitledBorder("Image settings"));
        root.add(controls, BorderLayout.SOUTH);
        return root;
    }

    private JPanel createPlaceholder(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JLabel("No image selected", SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }
}
