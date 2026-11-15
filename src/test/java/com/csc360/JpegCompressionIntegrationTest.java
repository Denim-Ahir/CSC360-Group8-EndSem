package com.csc360;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csc360.compression.CompressedImageWriter;
import com.csc360.compression.ImageProcessingOptions;
import com.csc360.compression.ImageProcessingResult;
import com.csc360.compression.ImageProcessingService;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JpegCompressionIntegrationTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void createsAReadableJpegWithExpectedDimensions() throws Exception {
        File source = temporaryDirectory.resolve("fixture.png").toFile();
        BufferedImage fixture = new BufferedImage(48, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = fixture.createGraphics();
        try {
            graphics.setColor(new Color(40, 120, 220, 255));
            graphics.fillRect(0, 0, 48, 32);
            graphics.setColor(new Color(255, 180, 40, 150));
            graphics.fillOval(8, 4, 30, 24);
        } finally {
            graphics.dispose();
        }
        assertTrue(ImageIO.write(fixture, "png", source));

        ImageProcessingService service = new ImageProcessingService();
        ImageProcessingResult result = service.process(source,
                ImageProcessingOptions.originalSize(70));
        File output = temporaryDirectory.resolve("fixture-compressed.jpg").toFile();
        CompressedImageWriter.write(result.getJpegBytes(), output);

        BufferedImage decoded = ImageIO.read(output);
        assertNotNull(decoded);
        assertEquals(48, decoded.getWidth());
        assertEquals(32, decoded.getHeight());
        assertTrue(output.length() > 0);
    }
}
