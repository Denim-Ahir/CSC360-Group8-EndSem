
package com.csc360.imageprocessing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageLoader {

    private static final long MAXIMUM_IMAGE_PIXELS = 50_000_000L;

    private ImageLoader() {
        // Prevent object creation
    }

    public static BufferedImage loadImage(File file) throws IOException {

        if (file == null) {
            throw new IllegalArgumentException("Image file cannot be null.");
        }

        if (!file.exists() || !file.isFile() || file.length() == 0) {
            throw new IOException("The selected image file is missing or empty.");
        }

        validateImageDimensions(file);
        BufferedImage image = ImageIO.read(file);

        if (image == null) {
            throw new IOException("Unsupported or invalid image file.");
        }

        return image;
    }

    private static void validateImageDimensions(File file) throws IOException {
        try (ImageInputStream stream = ImageIO.createImageInputStream(file)) {
            if (stream == null) {
                throw new IOException("Could not read the selected image file.");
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) {
                throw new IOException("Unsupported or invalid image file.");
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(stream, true, true);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                long pixels = (long) width * height;
                if (width <= 0 || height <= 0 || pixels > MAXIMUM_IMAGE_PIXELS) {
                    throw new IOException(
                            "Image dimensions are empty or too large to open safely.");
                }
            } finally {
                reader.dispose();
            }
        }
    }
}
