package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;

public class MainAppTest {

    @Test
    public void version_isV13() {
        assertEquals("V1.3", MainApp.VERSION);
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
}
