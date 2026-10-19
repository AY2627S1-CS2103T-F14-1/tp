package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ContactOrder;

/** Tests editing TutorConnect records by stable ID. */
public class EditContactIdTest {
    @Test
    public void editById_ignoresCurrentFilterAndPreservesId() throws Exception {
        Model model = new ModelManager(new AddressBook(), new UserPrefs());
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("add-student n/Alice p/91234567").execute(model);
        parser.parseCommand("add-guardian n/Bob p/92345678").execute(model);
        parser.parseCommand("link s/S1 g/G1").execute(model);
        parser.parseCommand("find Bob").execute(model);
        parser.parseCommand("edit S1 p/93456789").execute(model);

        var student = model.getAddressBook().getPersonList().stream()
                .filter(person -> ContactOrder.getContactId(person).orElse("").equals("S1"))
                .findFirst().orElseThrow();
        assertEquals("93456789", student.getPhone().value);
        assertEquals(1, model.getGuardianLinks().size());
        assertThrows(CommandException.class, () -> parser.parseCommand("edit G9 n/Carol").execute(model));
        assertThrows(CommandException.class, () -> parser.parseCommand("edit G1 t/replacement").execute(model));
    }
}
