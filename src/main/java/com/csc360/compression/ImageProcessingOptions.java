
package com.csc360.compression;

/**
 * User-selected processing settings. JPEG quality is expressed as a percentage.
 */
public record ImageProcessingOptions(int qualityPercent, boolean resizeEnabled,
        int targetWidth, int targetHeight, boolean preserveAspectRatio) {

    public static final int MINIMUM_QUALITY = 10;
    public static final int MAXIMUM_QUALITY = 100;
    public static final int DEFAULT_QUALITY = 80;

    public ImageProcessingOptions {
        if (qualityPercent < MINIMUM_QUALITY || qualityPercent > MAXIMUM_QUALITY) {
            throw new IllegalArgumentException("JPEG quality must be between 10 and 100.");
        }
        if (resizeEnabled && (targetWidth <= 0 || targetHeight <= 0)) {
            throw new IllegalArgumentException(
                    "Resize width and height must be greater than zero.");
        }
    }

    public static ImageProcessingOptions defaults() {
        return originalSize(DEFAULT_QUALITY);
    }

    public static ImageProcessingOptions originalSize(int qualityPercent) {
        return new ImageProcessingOptions(qualityPercent, false, 0, 0, true);
    }

    public static ImageProcessingOptions resized(int qualityPercent, int targetWidth,
            int targetHeight, boolean preserveAspectRatio) {
        return new ImageProcessingOptions(qualityPercent, true, targetWidth, targetHeight,
                preserveAspectRatio);
    }
}
