package com.csc360.compression;

/**
 * User-selected processing settings. JPEG quality is expressed as a percentage.
 */
public record ImageProcessingOptions(int qualityPercent) {

    public static final int MINIMUM_QUALITY = 10;
    public static final int MAXIMUM_QUALITY = 100;
    public static final int DEFAULT_QUALITY = 80;

    public ImageProcessingOptions {
        if (qualityPercent < MINIMUM_QUALITY || qualityPercent > MAXIMUM_QUALITY) {
            throw new IllegalArgumentException("JPEG quality must be between 10 and 100.");
        }
    }

    public static ImageProcessingOptions defaults() {
        return new ImageProcessingOptions(DEFAULT_QUALITY);
    }
}
