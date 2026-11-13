
package com.csc360.imageprocessing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageLoader {

    private ImageLoader() {
        // Prevent object creation
    }

    public static BufferedImage loadImage(File file) throws IOException {

        if (file == null) {
            throw new IllegalArgumentException("Image file cannot be null.");
        }

        if (!file.exists() || !file.isFile()) {
            throw new IOException("The selected file does not exist.");
        }

        BufferedImage image = ImageIO.read(file);

        if (image == null) {
            throw new IOException("Unsupported or invalid image file.");
        }

        return image;
    }
}