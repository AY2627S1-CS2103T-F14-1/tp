package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

public class MainAppTest {

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
}
