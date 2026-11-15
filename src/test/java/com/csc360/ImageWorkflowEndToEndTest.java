
package com.csc360;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csc360.compression.ImageProcessingOptions;
import com.csc360.compression.ImageProcessingResult;
import com.csc360.compression.ImageProcessingService;
import com.csc360.imageprocessing.ImageLoader;
import com.csc360.utils.ImageValidator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ImageWorkflowEndToEndTest {

    @TempDir
    Path temporaryDirectory;

    private final ImageProcessingService processingService =
            new ImageProcessingService();

    @Test
    void processesJpgJpegAndPngThroughCompleteWorkflow() throws Exception {
        String[] formats = {"jpg", "jpeg", "png"};

        for (String format : formats) {
            File source = temporaryDirectory
                    .resolve("sample-" + format + "." + format)
                    .toFile();

            BufferedImage fixture = createRgbFixture();

            assertTrue(ImageIO.write(fixture, format, source),
                    "Could not create " + format + " test image.");

            verifyCompleteWorkflow(source, format);
        }
    }

    @Test
    void processesTransparentPngAndCompositesTransparencyOntoWhite()
            throws Exception {

        File source = temporaryDirectory
                .resolve("transparent-sample.png")
                .toFile();

        BufferedImage fixture = createTransparentFixture();

        assertTrue(ImageIO.write(fixture, "png", source));

        ImageValidator.validateImage(source);

        BufferedImage original = ImageLoader.loadImage(source);

        // Check actual transparency instead of the internal image type.
        assertTrue(original.getColorModel().hasAlpha());

        int originalAlpha = (original.getRGB(0, 0) >>> 24) & 0xff;
        assertEquals(0, originalAlpha);

        ImageProcessingResult result = processingService.process(
                source, ImageProcessingOptions.originalSize(80));

        assertNotNull(result.getCompressedPreview());
        assertTrue(result.getJpegBytes().length > 0);

        File output = temporaryDirectory
                .resolve("transparent-output.jpg")
                .toFile();

        Files.write(output.toPath(), result.getJpegBytes());

        BufferedImage reopened = ImageIO.read(output);

        assertNotNull(reopened);
        assertEquals(40, reopened.getWidth());
        assertEquals(30, reopened.getHeight());

        // Transparent background should become white in JPEG.
        Color corner = new Color(reopened.getRGB(0, 0));

        assertTrue(corner.getRed() > 240);
        assertTrue(corner.getGreen() > 240);
        assertTrue(corner.getBlue() > 240);

        assertTrue(output.exists());
        assertTrue(output.length() > 0);

        assertEquals(source.length(),
                result.getMetrics().getOriginalBytes());

        assertEquals(output.length(),
                result.getMetrics().getCompressedBytes());
    }

    private void verifyCompleteWorkflow(File source, String format)
            throws Exception {

        ImageValidator.validateImage(source);

        BufferedImage original = ImageLoader.loadImage(source);

        assertNotNull(original);
        assertEquals(40, original.getWidth());
        assertEquals(30, original.getHeight());

        ImageProcessingResult result = processingService.process(
                source, ImageProcessingOptions.originalSize(80));

        assertNotNull(result.getCompressedPreview());
        assertTrue(result.getJpegBytes().length > 0);

        assertEquals(40, result.getCompressedPreview().getWidth());
        assertEquals(30, result.getCompressedPreview().getHeight());

        File output = temporaryDirectory
                .resolve("output-" + format + ".jpg")
                .toFile();

        Files.write(output.toPath(), result.getJpegBytes());

        assertTrue(output.exists());
        assertTrue(output.length() > 0);

        BufferedImage reopened = ImageIO.read(output);

        assertNotNull(reopened);
        assertEquals(40, reopened.getWidth());
        assertEquals(30, reopened.getHeight());

        assertEquals(source.length(),
                result.getMetrics().getOriginalBytes());

        assertEquals(output.length(),
                result.getMetrics().getCompressedBytes());
    }

    private BufferedImage createRgbFixture() {
        BufferedImage image = new BufferedImage(
                40, 30, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();

        try {
            graphics.setColor(Color.BLUE);
            graphics.fillRect(0, 0, 40, 30);

            graphics.setColor(Color.YELLOW);
            graphics.fillOval(8, 5, 24, 20);

            graphics.setColor(Color.RED);
            graphics.fillRect(15, 10, 10, 10);
        } finally {
            graphics.dispose();
        }

        return image;
    }

    private BufferedImage createTransparentFixture() {
        BufferedImage image = new BufferedImage(
                40, 30, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = image.createGraphics();

        try {
            graphics.setColor(new Color(0, 0, 0, 0));
            graphics.fillRect(0, 0, 40, 30);

            graphics.setColor(new Color(30, 100, 220, 255));
            graphics.fillOval(8, 5, 24, 20);
        } finally {
            graphics.dispose();
        }

        return image;
    }
}
