package com.csc360.compression;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

/**
 * Writes a generated JPEG to a destination chosen by the user.
 */
public final class CompressedImageWriter {

    private CompressedImageWriter() {
        // Utility class.
    }

    /**
     * Writes JPEG bytes without replacing an existing file.
     */
    public static void write(byte[] jpegBytes, File destination) throws IOException {
        write(jpegBytes, destination, false);
    }

    /**
     * Writes JPEG bytes, replacing a file only when the caller explicitly allows it.
     */
    public static void write(byte[] jpegBytes, File destination, boolean overwrite)
            throws IOException {
        if (jpegBytes == null || jpegBytes.length == 0) {
            throw new IllegalArgumentException("Compressed image data cannot be empty.");
        }
        if (destination == null) {
            throw new IllegalArgumentException("Destination file cannot be null.");
        }

        Path target = destination.toPath().toAbsolutePath().normalize();
        Path parent = target.getParent();
        if (parent == null) {
            throw new IOException("The destination must be inside a folder.");
        }
        Files.createDirectories(parent);
        if (Files.isDirectory(target)) {
            throw new IOException("The selected destination is a folder.");
        }
        if (Files.exists(target) && !overwrite) {
            throw new IOException("The destination already exists.");
        }

        Path temporary = Files.createTempFile(parent, "imagelite-", ".tmp");
        try {
            Files.write(temporary, jpegBytes, StandardOpenOption.TRUNCATE_EXISTING);
            moveIntoPlace(temporary, target, overwrite);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static void moveIntoPlace(Path temporary, Path target, boolean overwrite)
            throws IOException {
        try {
            if (overwrite) {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE);
            }
        } catch (AtomicMoveNotSupportedException exception) {
            if (overwrite) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.move(temporary, target);
            }
        }
    }
}
