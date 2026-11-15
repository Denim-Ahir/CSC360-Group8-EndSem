package com.csc360.compression;

import com.csc360.imageprocessing.ImageConverter;
import com.csc360.imageprocessing.ImageLoader;
import com.csc360.utils.ImageValidator;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Runs the image-processing sequence independently from the Swing interface.
 */
public final class ImageProcessingService {

    public ImageProcessingResult process(File source, int qualityPercent) throws IOException {
        return process(source, new ImageProcessingOptions(qualityPercent));
    }

    public ImageProcessingResult process(File source, ImageProcessingOptions options)
            throws IOException {
        if (options == null) {
            throw new IllegalArgumentException("Processing options cannot be null.");
        }
        ImageValidator.validateImage(source);

        BufferedImage sourceImage = ImageLoader.loadImage(source);
        BufferedImage jpegReadyImage = ImageConverter.convertToRGB(sourceImage);
        byte[] jpegBytes = ImageCompressor.compressToJpeg(jpegReadyImage,
                options.qualityPercent());
        BufferedImage compressedPreview = ImageIO.read(new ByteArrayInputStream(jpegBytes));
        if (compressedPreview == null) {
            throw new IOException("The JPEG writer produced unreadable output.");
        }

        return new ImageProcessingResult(compressedPreview, jpegBytes,
                new CompressionMetrics(source.length(), jpegBytes.length));
    }
}
