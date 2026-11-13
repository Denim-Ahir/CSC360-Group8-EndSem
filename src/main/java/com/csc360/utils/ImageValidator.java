
package com.csc360.utils;

import java.io.File;
import java.util.Locale;

public class ImageValidator {

    private static final String[] SUPPORTED_FORMATS = {
        "jpg", "jpeg", "png"
    };

    private ImageValidator() {
        // Prevent object creation
    }

    public static boolean isSupportedFormat(File file) {

        if (file == null || !file.isFile()) {
            return false;
        }

        String fileName = file.getName().toLowerCase(Locale.ROOT);

        int extensionIndex = fileName.lastIndexOf('.');

        if (extensionIndex == -1) {
            return false;
        }

        String extension = fileName.substring(extensionIndex + 1);

        for (String format : SUPPORTED_FORMATS) {
            if (extension.equals(format)) {
                return true;
            }
        }

        return false;
    }

    public static void validateImage(File file) {

        if (file == null) {
            throw new IllegalArgumentException(
                "No image file was selected."
            );
        }

        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException(
                "The selected file does not exist or is invalid."
            );
        }

        if (!isSupportedFormat(file)) {
            throw new IllegalArgumentException(
                "Unsupported image format. Please select JPG, JPEG or PNG."
            );
        }
    }
}