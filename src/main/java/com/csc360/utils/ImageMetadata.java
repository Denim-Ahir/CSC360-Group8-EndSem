
package com.csc360.utils;

import com.csc360.imageprocessing.ImageLoader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class ImageMetadata {

    private final String fileName;
    private final String format;
    private final long fileSize;
    private final int width;
    private final int height;

    private ImageMetadata(
            String fileName,
            String format,
            long fileSize,
            int width,
            int height) {

        this.fileName = fileName;
        this.format = format;
        this.fileSize = fileSize;
        this.width = width;
        this.height = height;
    }

    public static ImageMetadata extractMetadata(File file)
            throws IOException {

        ImageValidator.validateImage(file);

        BufferedImage image = ImageLoader.loadImage(file);

        String fileName = file.getName();

        int extensionIndex = fileName.lastIndexOf('.');
        String format = fileName.substring(extensionIndex + 1)
                .toLowerCase(Locale.ROOT);

        return new ImageMetadata(
                fileName,
                format,
                file.length(),
                image.getWidth(),
                image.getHeight()
        );
    }

    public String getFileName() {
        return fileName;
    }

    public String getFormat() {
        return format;
    }

    public long getFileSize() {
        return fileSize;
    }

    public double getFileSizeInKB() {
        return fileSize / 1024.0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getDimensions() {
        return width + " x " + height;
    }
}