package smac.util;

import java.io.File;

/**
 * SmacFileUtils is a class which groups static methods pertaining to file operations used throughout the SMAC daemon.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class SmacFileUtils {

    /**
     * Move a given file defined by its path and name to a target folder
     *
     * @param filename Path and name of file that needs to be moved
     * @param destinationFolder Path of destination folder
     */
    public static void moveFile(String filename, String destinationFolder) {

        // File that needs to be moved
        File targetFile = new File(filename);

        // Destination folder
        File destination = new File(destinationFolder);

        // Check if the destination folder exists
        if (destination.exists()) {

            // Check if the destination is a folder
            if (destination.isDirectory()) {

                // Move the file to its destination folder
                targetFile.renameTo(new File(destination, targetFile.getName()));
            }
        }
    }
}
