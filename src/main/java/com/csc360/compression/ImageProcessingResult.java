package com.csc360.compression;

import java.awt.image.BufferedImage;

/**
 * Immutable output from one image-processing operation.
 */
public final class ImageProcessingResult {

    private final BufferedImage compressedPreview;
    private final byte[] jpegBytes;
    private final CompressionMetrics metrics;

    public ImageProcessingResult(BufferedImage compressedPreview, byte[] jpegBytes,
            CompressionMetrics metrics) {
        if (compressedPreview == null || jpegBytes == null || metrics == null) {
            throw new IllegalArgumentException("Processing result values cannot be null.");
        }
        this.compressedPreview = compressedPreview;
        this.jpegBytes = jpegBytes.clone();
        this.metrics = metrics;
    }

    public BufferedImage getCompressedPreview() {
        return compressedPreview;
    }

    public byte[] getJpegBytes() {
        return jpegBytes.clone();
    }

    public CompressionMetrics getMetrics() {
        return metrics;
    }
}
