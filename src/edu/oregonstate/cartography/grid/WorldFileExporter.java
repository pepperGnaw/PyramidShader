/*
 * WorldFileExporter.java
 *
 * Created on June 6, 2007, 9:24 PM
 *
 */
package edu.oregonstate.cartography.grid;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Write world file for a georeferenced image.
 *
 * @author Bernhard Jenny, Institute of Cartography, ETH Zurich
 */
public class WorldFileExporter {

    private WorldFileExporter() {
    }

    /**
     * Construct a world file path for for a passed image file path.
     *
     * @param imageFilePath The path to the image file.
     * @return The path to the world file.
     */
    public static String constructPath(String imageFilePath) {
        String ext = getFileExtension(imageFilePath);
        switch (ext.length()) {
            case 0:
                ext = "w";
                break;
            case 1:
            case 2:
                ext = ext + "w";
                break;
            default:
                ext = ext.substring(0, 1)
                        + ext.substring(ext.length() - 1, ext.length())
                        + "w";
                break;
        }
        return replaceExtension(imageFilePath, ext);
    }

    public static void writeWorldFile(String worldFilePath, double cellSize,
            double west, double north)
            throws IOException {

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(worldFilePath)));
            writer.println(cellSize);
            writer.println(0);
            writer.println(0);
            writer.println(-cellSize);
            writer.println(west);
            writer.println(north);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Change the extension of a file path. The extension is what follows the
     * last dot '.' in the path. If no dot exists in the path, the passed
     * extension is simply appended without replacing anything.
     *
     * @param filePath The path of the file with the extension to replace.
     * @param newExtension The new extension for the file, e.g. "tif".
     * @return A new path to a file. The file may not actually exist on the hard
     * disk.
     */
    public static String replaceExtension(String filePath, String newExtension) {
        final int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex == -1) {
            return filePath + "." + newExtension;
        }
        return filePath.substring(0, dotIndex + 1) + newExtension;
    }

    /**
     * Returns the file extension from a passed file path.
     * @param fileName The file path.
     * @return The file extension
     */
    public static String getFileExtension(String fileName) {
        final int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return new String();
        }
        return fileName.substring(dotIndex + 1);
    }
}
