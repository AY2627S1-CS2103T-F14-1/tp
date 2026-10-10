package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileUtilTest {

    @TempDir
    public Path testFolder;

    @Test
    public void writeToFile_newFile_fileCreatedWithContent() throws IOException {
        Path file = testFolder.resolve("data.json");

        FileUtil.writeToFile(file, "hello");

        assertEquals("hello", FileUtil.readFromFile(file));
    }

    @Test
    public void writeToFile_existingFile_contentReplaced() throws IOException {
        Path file = testFolder.resolve("data.json");
        FileUtil.writeToFile(file, "old");

        FileUtil.writeToFile(file, "new");

        assertEquals("new", FileUtil.readFromFile(file));
    }

    @Test
    public void writeToFile_success_noTemporaryFileLeftBehind() throws IOException {
        Path file = testFolder.resolve("data.json");

        FileUtil.writeToFile(file, "hello");

        try (Stream<Path> files = Files.list(testFolder)) {
            assertEquals(1, files.count());
        }
    }

    @Test
    public void writeToFile_targetIsDirectory_throwsIoExceptionAndLeavesNoTemporaryFile() throws IOException {
        Path directoryTarget = Files.createDirectory(testFolder.resolve("data.json"));
        Files.createFile(directoryTarget.resolve("child"));

        assertThrows(IOException.class, () -> FileUtil.writeToFile(directoryTarget, "hello"));

        try (Stream<Path> files = Files.list(testFolder)) {
            assertTrue(files.allMatch(path -> path.equals(directoryTarget)));
        }
    }

    @Test
    public void writeToFile_missingParentDirectory_throwsIoException() {
        Path file = testFolder.resolve("missing").resolve("data.json");

        assertThrows(IOException.class, () -> FileUtil.writeToFile(file, "hello"));
    }
}
