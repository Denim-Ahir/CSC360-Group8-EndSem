
package com.csc360.imageprocessing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageConverter {

    private ImageConverter() {
        // Prevent object creation
    }

    public static BufferedImage convertToRGB(
            BufferedImage originalImage) {

        if (originalImage == null) {
            throw new IllegalArgumentException(
                "Original image cannot be null."
            );
        }

        BufferedImage rgbImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D graphics = rgbImage.createGraphics();

        // JPEG does not support transparency.
        // Fill transparent areas with white.
        graphics.setColor(Color.WHITE);

        graphics.fillRect(
                0,
                0,
                rgbImage.getWidth(),
                rgbImage.getHeight()
        );

        graphics.drawImage(
                originalImage,
                0,
                0,
                null
        );

        graphics.dispose();

        return rgbImage;
    }
}