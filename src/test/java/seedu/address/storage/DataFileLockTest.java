package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DataFileLockTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void lockFileFor_addsLockSuffixBesideDataFile() {
        Path dataFile = temporaryFolder.resolve("tutorconnect.json");
        assertEquals(temporaryFolder.resolve("tutorconnect.json.lock"), DataFileLock.lockFileFor(dataFile));
    }

    @Test
    public void lockFileFor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> DataFileLock.lockFileFor(null));
    }

    @Test
    public void tryAcquire_dataFolderMissing_createsFolderAndLocks() throws IOException {
        Path dataFile = temporaryFolder.resolve("data").resolve("tutorconnect.json");

        Optional<DataFileLock> lock = DataFileLock.tryAcquire(dataFile);

        assertTrue(lock.isPresent());
        assertTrue(Files.exists(DataFileLock.lockFileFor(dataFile)));
        assertFalse(Files.exists(dataFile)); // the data file itself is not touched
        lock.get().close();
    }

    @Test
    public void tryAcquire_alreadyLocked_returnsEmpty() throws IOException {
        Path dataFile = temporaryFolder.resolve("tutorconnect.json");

        try (DataFileLock ignored = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
        }
    }

    @Test
    public void tryAcquire_afterClose_canLockAgain() throws IOException {
        Path dataFile = temporaryFolder.resolve("tutorconnect.json");

        DataFileLock first = DataFileLock.tryAcquire(dataFile).orElseThrow();
        first.close();
        first.close(); // closing twice is harmless

        Optional<DataFileLock> second = DataFileLock.tryAcquire(dataFile);
        assertTrue(second.isPresent());
        second.get().close();
    }

    @Test
    public void tryAcquire_differentDataFiles_lockIndependently() throws IOException {
        Path folderA = Files.createDirectory(temporaryFolder.resolve("a"));
        Path folderB = Files.createDirectory(temporaryFolder.resolve("b"));

        try (DataFileLock ignoredA = DataFileLock.tryAcquire(folderA.resolve("tutorconnect.json")).orElseThrow();
                DataFileLock ignoredB = DataFileLock.tryAcquire(folderB.resolve("tutorconnect.json")).orElseThrow()) {
            assertTrue(Files.exists(folderA.resolve("tutorconnect.json.lock")));
            assertTrue(Files.exists(folderB.resolve("tutorconnect.json.lock")));
        }
    }
}
