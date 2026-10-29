package seedu.address.commons.util;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Writes and reads files
 */
public class FileUtil {

    private static final String CHARSET = "UTF-8";

    /**
     * Creates a file if it does not exist along with its missing parent directories.
     * @throws IOException if the file or directory cannot be created.
     */
    public static void createIfMissing(Path file) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        createParentDirsOfFile(file);

        Files.createFile(file);
    }

    /**
     * Creates parent directories of file if it has a parent directory
     */
    private static void createParentDirsOfFile(Path file) throws IOException {
        Path parentDir = file.getParent();

        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Assumes file exists
     */
    public static String readFromFile(Path file) throws IOException {
        return new String(Files.readAllBytes(file), CHARSET);
    }

    /**
     * Writes given string to a file.
     * Will create the file if it does not exist yet.
     * The content is first written to a temporary file in the same directory, which then replaces {@code file}.
     * If writing fails, the existing content of {@code file} is left untouched.
     *
     * @throws AccessDeniedException naming {@code file}, not the temporary file, if the directory is not writable
     */
    public static void writeToFile(Path file, String content) throws IOException {
        try {
            writeThroughTemporaryFile(file, content);
        } catch (AccessDeniedException e) {
            AccessDeniedException deniedForFile = new AccessDeniedException(file.toString());
            deniedForFile.initCause(e);
            throw deniedForFile;
        }
    }

    private static void writeThroughTemporaryFile(Path file, String content) throws IOException {
        Path directory = file.toAbsolutePath().getParent();
        assert directory != null : "Cannot write to a path with no parent directory: " + file;
        Path tempFile = Files.createTempFile(directory, file.getFileName().toString(), ".tmp");
        try {
            Files.write(tempFile, content.getBytes(CHARSET));
            Files.move(tempFile, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            assert Files.isRegularFile(file) : "Atomic move did not produce " + file;
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

}
