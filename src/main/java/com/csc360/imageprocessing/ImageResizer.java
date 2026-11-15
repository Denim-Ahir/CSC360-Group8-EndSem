
package com.csc360.imageprocessing;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageResizer {

    private static final long MAXIMUM_OUTPUT_PIXELS = 50_000_000L;

    private ImageResizer() {
        // Prevent object creation
    }

    public static BufferedImage resizeImage(
            BufferedImage originalImage,
            int newWidth,
            int newHeight) {

        if (originalImage == null) {
            throw new IllegalArgumentException(
                "Original image cannot be null."
            );
        }

        if (newWidth <= 0 || newHeight <= 0) {
            throw new IllegalArgumentException(
                "Image dimensions must be greater than zero."
            );
        }

        if ((long) newWidth * newHeight > MAXIMUM_OUTPUT_PIXELS) {
            throw new IllegalArgumentException(
                    "Requested image is too large to resize safely.");
        }

        int imageType = originalImage.getColorModel().hasAlpha()
                ? BufferedImage.TYPE_INT_ARGB
                : BufferedImage.TYPE_INT_RGB;

        BufferedImage resizedImage = new BufferedImage(
                newWidth,
                newHeight,
                imageType
        );

        Graphics2D graphics = resizedImage.createGraphics();
        try {
            graphics.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );

            graphics.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );

            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            graphics.drawImage(
                    originalImage,
                    0,
                    0,
                    newWidth,
                    newHeight,
                    null
            );
        } finally {
            graphics.dispose();
        }

        return resizedImage;
    }

    public static BufferedImage resizeImagePreservingAspectRatio(
            BufferedImage originalImage,
            int newWidth) {

        if (originalImage == null) {
            throw new IllegalArgumentException(
                "Original image cannot be null."
            );
        }

        if (newWidth <= 0) {
            throw new IllegalArgumentException(
                "New width must be greater than zero."
            );
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        int newHeight = (int) Math.round(
                (double) originalHeight * newWidth / originalWidth
        );

        return resizeImage(
                originalImage,
                newWidth,
                Math.max(1, newHeight)
        );
    }
}
