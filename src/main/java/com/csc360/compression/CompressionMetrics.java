package com.csc360.compression;

/**
 * Size comparison values for an original image and generated JPEG output.
 */
public final class CompressionMetrics {

    private final long originalBytes;
    private final long compressedBytes;

    public CompressionMetrics(long originalBytes, long compressedBytes) {
        if (originalBytes < 0 || compressedBytes < 0) {
            throw new IllegalArgumentException("Image sizes cannot be negative.");
        }
        this.originalBytes = originalBytes;
        this.compressedBytes = compressedBytes;
    }

    public long getOriginalBytes() {
        return originalBytes;
    }

    public long getCompressedBytes() {
        return compressedBytes;
    }

    public long getBytesSaved() {
        return originalBytes - compressedBytes;
    }

    /**
     * Returns the actual percentage difference. A larger output produces a negative value.
     */
    public double getReductionPercent() {
        if (originalBytes == 0) {
            return 0.0;
        }
        return getBytesSaved() * 100.0 / originalBytes;
    }

    /**
     * Returns {@link Double#NaN} when no non-zero output size exists for a ratio.
     */
    public double getCompressionRatio() {
        if (compressedBytes == 0) {
            return Double.NaN;
        }
        return (double) originalBytes / compressedBytes;
    }
}
