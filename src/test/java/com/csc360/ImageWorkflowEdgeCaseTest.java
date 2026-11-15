
package com.csc360;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csc360.compression.ImageProcessingOptions;
import com.csc360.compression.ImageProcessingResult;
import com.csc360.compression.ImageProcessingService;
import com.csc360.imageprocessing.ImageLoader;
import com.csc360.imageprocessing.ImageResizer;
import com.csc360.utils.ImageValidator;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ImageWorkflowEdgeCaseTest {

    @TempDir
    Path temporaryDirectory;

    private final ImageProcessingService service =
            new ImageProcessingService();

    @Test
    void processesVerySmallImage() throws Exception {
        File source = createPng("tiny.png", 1, 1);

        ImageProcessingResult result =
                service.process(source, 80);

        assertNotNull(result);
        assertNotNull(result.getCompressedPreview());
        assertTrue(result.getJpegBytes().length > 0);
        assertEquals(1, result.getCompressedPreview().getWidth());
        assertEquals(1, result.getCompressedPreview().getHeight());
    }

    @Test
    void processesLargerImageWithinMemoryLimits() throws Exception {
        File source = createPng("large.png", 1200, 800);

        ImageProcessingResult result =
                service.process(source, 80);

        assertNotNull(result.getCompressedPreview());
        assertEquals(1200, result.getCompressedPreview().getWidth());
        assertEquals(800, result.getCompressedPreview().getHeight());
    }

    @Test
    void resizesPortraitAndLandscapeImages() {
        BufferedImage portrait =
                new BufferedImage(400, 800, BufferedImage.TYPE_INT_RGB);

        BufferedImage landscape =
                new BufferedImage(800, 400, BufferedImage.TYPE_INT_RGB);

        BufferedImage resizedPortrait =
                ImageResizer.resizeImagePreservingAspectRatio(portrait, 200);

        BufferedImage resizedLandscape =
                ImageResizer.resizeImagePreservingAspectRatio(landscape, 200);

        assertEquals(200, resizedPortrait.getWidth());
        assertEquals(400, resizedPortrait.getHeight());

        assertEquals(200, resizedLandscape.getWidth());
        assertEquals(100, resizedLandscape.getHeight());
    }

    @Test
    void processesTransparentPngWithoutChangingDimensions() throws Exception {
        File source = temporaryDirectory.resolve("transparent.png").toFile();

        BufferedImage transparent =
                new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);

        transparent.setRGB(5, 5, new Color(255, 0, 0, 0).getRGB());

        assertTrue(ImageIO.write(transparent, "png", source));

        ImageProcessingResult result =
                service.process(source, 80);

        assertNotNull(result.getCompressedPreview());
        assertEquals(10, result.getCompressedPreview().getWidth());
        assertEquals(10, result.getCompressedPreview().getHeight());

        Color background = new Color(
                result.getCompressedPreview().getRGB(5, 5));

        assertTrue(background.getRed() > 240);
        assertTrue(background.getGreen() > 240);
        assertTrue(background.getBlue() > 240);
    }

    @Test
    void rejectsCorruptImageFile() throws Exception {
        File corrupt = temporaryDirectory.resolve("corrupt.png").toFile();

        Files.writeString(corrupt.toPath(), "This is not a valid image");

        assertThrows(IOException.class,
                () -> ImageLoader.loadImage(corrupt));
    }

    @Test
    void acceptsUppercaseImageExtensionAndRejectsUnusualExtension()
            throws Exception {

        File uppercase = createPng("sample.PNG", 5, 5);
        File unusual = createPng("sample.gif", 5, 5);

        assertTrue(ImageValidator.isSupportedFormat(uppercase));
        assertFalse(ImageValidator.isSupportedFormat(unusual));
    }

    @Test
    void rejectsInvalidResizeDimensions() {
        BufferedImage source =
                new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        assertThrows(IllegalArgumentException.class,
                () -> ImageResizer.resizeImage(source, 0, 10));

        assertThrows(IllegalArgumentException.class,
                () -> ImageResizer.resizeImage(source, 10, -5));

        assertThrows(IllegalArgumentException.class,
                () -> ImageResizer.resizeImage(source, 10_000, 10_000));
    }

    @Test
    void rejectsQualityOutsideSupportedRange() {
        assertThrows(IllegalArgumentException.class,
                () -> ImageProcessingOptions.originalSize(9));

        assertThrows(IllegalArgumentException.class,
                () -> ImageProcessingOptions.originalSize(101));

        assertEquals(10, ImageProcessingOptions.MINIMUM_QUALITY);
        assertEquals(100, ImageProcessingOptions.MAXIMUM_QUALITY);
    }

    private File createPng(String fileName, int width, int height)
            throws IOException {

        File file = temporaryDirectory.resolve(fileName).toFile();

        BufferedImage image =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        image.setRGB(0, 0, Color.BLUE.getRGB());

        assertTrue(ImageIO.write(image, "png", file));

        return file;
    }
}
