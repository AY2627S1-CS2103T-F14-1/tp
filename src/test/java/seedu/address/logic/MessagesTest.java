package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.testutil.PersonBuilder;

/** Tests the user-facing wording of command results. */
public class MessagesTest {

    @Test
    public void format_contactWithId_showsIdNameAndPhoneOnly() {
        assertEquals("S1 Alex Tan, 91234567", Messages.format(new PersonBuilder().withName("Alex Tan")
                .withPhone("91234567").withTags("student", "S1").build()));
    }

    @Test
    public void format_contactWithoutId_showsNameAndPhoneOnly() {
        assertEquals("Alex Tan, 91234567", Messages.format(new PersonBuilder().withName("Alex Tan")
                .withPhone("91234567").withTags().build()));
    }

    @Test
    public void commandResults_useTutorConnectWording() throws Exception {
        Model model = new ModelManager();
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("add-student n/Alex Tan p/91234567").execute(model);
        parser.parseCommand("add-guardian n/Amelia Tan p/92345678").execute(model);

        assertEquals("Edited contact: S1 Alex Tan, 90000000",
                parser.parseCommand("edit S1 p/90000000").execute(model).getFeedbackToUser());
        assertEquals("Deleted contact: S1 Alex Tan, 90000000",
                parser.parseCommand("delete 1").execute(model).getFeedbackToUser());
        assertEquals("All contacts and links have been cleared.",
                parser.parseCommand("clear confirm").execute(model).getFeedbackToUser());
    }
}
