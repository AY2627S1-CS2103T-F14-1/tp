package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

public class MainAppTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void version_isV15() {
        assertEquals("V1.5", MainApp.VERSION);
    }

    @Test
    public void getInitialData_missingData_returnsEmptyWorkspace() {
        assertEquals(0, MainApp.getInitialData(Optional.empty()).getPersonList().size());
    }

    @Test
    public void getInitialData_savedData_returnsSavedWorkspace() {
        ReadOnlyAddressBook savedData = new AddressBook();

        assertEquals(savedData, MainApp.getInitialData(Optional.of(savedData)));
    }

    @Test
    public void initModelManager_missingData_returnsEmptyWorkspace() {
        Storage storage = new StorageManager(new JsonAddressBookStorage(Path.of("missing-data.json")),
                new JsonUserPrefsStorage(Path.of("missing-preferences.json")));

        assertEquals(0, new MainApp().initModelManager(storage, new UserPrefs())
                .getAddressBook().getPersonList().size());
    }

    private Storage storageFor(Path dataFile) {
        return new StorageManager(new JsonAddressBookStorage(dataFile),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
    }

    @Test
    public void initModelManager_unreadableData_backsUpFileAndStartsEmpty() throws IOException {
        Path dataFile = temporaryFolder.resolve("tutorconnect.json");
        Files.writeString(dataFile, "not valid json");

        assertEquals(0, new MainApp().initModelManager(storageFor(dataFile), new UserPrefs())
                .getAddressBook().getPersonList().size());

        assertEquals("not valid json", Files.readString(temporaryFolder.resolve("tutorconnect.json.bak")));
        assertEquals("not valid json", Files.readString(dataFile));
    }

    @Test
    public void backUpUnreadableDataFile_missingFile_returnsEmpty() {
        assertTrue(MainApp.backUpUnreadableDataFile(temporaryFolder.resolve("missing.json")).isEmpty());
    }

    @Test
    public void backUpUnreadableDataFile_sameContentTwice_reusesBackup() throws IOException {
        Path dataFile = temporaryFolder.resolve("tutorconnect.json");
        Files.writeString(dataFile, "broken");

        Path first = MainApp.backUpUnreadableDataFile(dataFile).orElseThrow();
        Path second = MainApp.backUpUnreadableDataFile(dataFile).orElseThrow();

        assertEquals(first, second);
        assertFalse(Files.exists(temporaryFolder.resolve("tutorconnect.json.bak1")));
    }

    @Test
    public void backUpUnreadableDataFile_differentContent_keepsEarlierBackup() throws IOException {
        Path dataFile = temporaryFolder.resolve("tutorconnect.json");
        Files.writeString(dataFile, "first broken file");
        MainApp.backUpUnreadableDataFile(dataFile);
        Files.writeString(dataFile, "second broken file");

        Path second = MainApp.backUpUnreadableDataFile(dataFile).orElseThrow();

        assertEquals(temporaryFolder.resolve("tutorconnect.json.bak1"), second);
        assertEquals("first broken file", Files.readString(temporaryFolder.resolve("tutorconnect.json.bak")));
        assertEquals("second broken file", Files.readString(second));
    }
}
