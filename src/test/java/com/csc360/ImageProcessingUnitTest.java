package com.csc360;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csc360.compression.CompressionMetrics;
import com.csc360.imageprocessing.ImageConverter;
import com.csc360.imageprocessing.ImageResizer;
import com.csc360.utils.ImageMetadata;
import com.csc360.utils.ImageValidator;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ImageProcessingUnitTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void validatesAndExtractsMetadataFromPng() throws Exception {
        File imageFile = temporaryDirectory.resolve("sample.png").toFile();
        BufferedImage image = new BufferedImage(6, 4, BufferedImage.TYPE_INT_RGB);
        assertTrue(ImageIO.write(image, "png", imageFile));

        assertTrue(ImageValidator.isSupportedFormat(imageFile));
        ImageMetadata metadata = ImageMetadata.extractMetadata(imageFile);

        assertEquals("sample.png", metadata.getFileName());
        assertEquals("png", metadata.getFormat());
        assertEquals(6, metadata.getWidth());
        assertEquals(4, metadata.getHeight());
        assertTrue(metadata.getFileSize() > 0);
        assertFalse(ImageValidator.isSupportedFormat(
                temporaryDirectory.resolve("sample.txt").toFile()));
    }

    @Test
    void resizesToExactDimensionsAndPreservesAspectRatio() {
        BufferedImage source = new BufferedImage(3, 2, BufferedImage.TYPE_INT_RGB);

        BufferedImage exact = ImageResizer.resizeImage(source, 12, 8);
        BufferedImage proportional = ImageResizer.resizeImagePreservingAspectRatio(source, 15);

        assertEquals(12, exact.getWidth());
        assertEquals(8, exact.getHeight());
        assertEquals(15, proportional.getWidth());
        assertEquals(10, proportional.getHeight());
    }

    @Test
    void compositesTransparentPixelsOntoWhiteForJpegOutput() {
        BufferedImage transparent = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        transparent.setRGB(0, 0, 0x00000000);

        BufferedImage rgb = ImageConverter.convertToRGB(transparent);

        assertEquals(Color.WHITE.getRGB(), rgb.getRGB(0, 0));
    }

    @Test
    void calculatesMetricsIncludingAnOutputThatIsLarger() {
        CompressionMetrics smaller = new CompressionMetrics(1_000, 750);
        CompressionMetrics larger = new CompressionMetrics(750, 1_000);
        CompressionMetrics emptyOutput = new CompressionMetrics(750, 0);

        assertEquals(250, smaller.getBytesSaved());
        assertEquals(25.0, smaller.getReductionPercent());
        assertEquals(1_000.0 / 750.0, smaller.getCompressionRatio());
        assertEquals(-250, larger.getBytesSaved());
        assertEquals(-100.0 / 3.0, larger.getReductionPercent());
        assertTrue(Double.isNaN(emptyOutput.getCompressionRatio()));
    }
}
