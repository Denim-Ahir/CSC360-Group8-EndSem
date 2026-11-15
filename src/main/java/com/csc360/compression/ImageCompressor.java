package com.csc360.compression;

import com.csc360.imageprocessing.ImageConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/**
 * Encodes images as JPEG using the JPEG writer supplied by Java ImageIO.
 */
public final class ImageCompressor {

    private ImageCompressor() {
        // Utility class.
    }

    /**
     * Compresses an image as JPEG bytes.
     *
     * @param image source image
     * @param qualityPercent JPEG quality from 1 to 100
     * @return encoded JPEG bytes
     * @throws IOException when no JPEG writer is available or encoding fails
     */
    public static byte[] compressToJpeg(BufferedImage image, int qualityPercent)
            throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null.");
        }
        if (qualityPercent < 1 || qualityPercent > 100) {
            throw new IllegalArgumentException(
                    "JPEG quality must be between 1 and 100.");
        }

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) {
            throw new IOException("No JPEG writer is available in this Java runtime.");
        }

        ImageWriter writer = writers.next();
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                ImageOutputStream imageStream = ImageIO.createImageOutputStream(byteStream)) {
            if (imageStream == null) {
                throw new IOException("Could not create a JPEG output stream.");
            }

            ImageWriteParam parameters = writer.getDefaultWriteParam();
            if (!parameters.canWriteCompressed()) {
                throw new IOException("The available JPEG writer cannot set compression quality.");
            }

            parameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            parameters.setCompressionQuality(qualityPercent / 100.0f);
            writer.setOutput(imageStream);
            writer.write(null, new IIOImage(ImageConverter.convertToRGB(image), null, null),
                    parameters);
            imageStream.flush();
            return byteStream.toByteArray();
        } finally {
            writer.dispose();
        }
    }
}
