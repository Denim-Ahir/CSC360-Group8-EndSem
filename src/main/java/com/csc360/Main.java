
package com.csc360;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame window = new JFrame("ImageLite - Image Compressor");

            window.setSize(800, 500);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setLocationRelativeTo(null);

            JLabel message = new JLabel(
                "Welcome to ImageLite - Image Compressor",
                SwingConstants.CENTER
            );

            window.add(message);
            window.setVisible(true);
        });
    }
}