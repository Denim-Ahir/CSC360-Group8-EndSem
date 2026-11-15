
package com.csc360;

import com.csc360.gui.MainWindow;
import javax.swing.SwingUtilities;

public class Main {

    private Main() {
        // Application entry point class.
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
