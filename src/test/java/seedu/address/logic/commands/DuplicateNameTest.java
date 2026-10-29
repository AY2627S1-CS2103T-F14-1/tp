package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

/** Tests how duplicate student and guardian names are detected. */
public class DuplicateNameTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void add_nameDiffersOnlyInCaseOrSpacing_throwsCommandException() throws Exception {
        Model model = new ModelManager();
        parser.parseCommand("add-student n/Alex Tan p/91234567").execute(model);
        parser.parseCommand("add-guardian n/Amelia Tan p/92345678").execute(model);

        assertThrows(CommandException.class, AddStudentCommand.MESSAGE_DUPLICATE_STUDENT, () ->
                parser.parseCommand("add-student n/alex tan p/91112222").execute(model));
        assertThrows(CommandException.class, AddStudentCommand.MESSAGE_DUPLICATE_STUDENT, () ->
                parser.parseCommand("add-student n/Alex  Tan p/91112222").execute(model));
        assertThrows(CommandException.class, AddGuardianCommand.MESSAGE_DUPLICATE_GUARDIAN, () ->
                parser.parseCommand("add-guardian n/AMELIA TAN p/91112222").execute(model));
    }

    @Test
    public void add_studentAndGuardianWithSameName_success() throws Exception {
        Model model = new ModelManager();
        parser.parseCommand("add-student n/Alex Tan p/91234567").execute(model);

        assertEquals("Added guardian G1: Alex Tan",
                parser.parseCommand("add-guardian n/Alex Tan p/98887777").execute(model).getFeedbackToUser());
    }

    @Test
    public void add_nameWithRepeatedSpaces_storesSingleSpaces() throws Exception {
        Model model = new ModelManager();

        assertEquals("Added student S1: Alex Tan",
                parser.parseCommand("add-student n/Alex   Tan p/91234567").execute(model).getFeedbackToUser());
    }

    @Test
    public void edit_duplicateNames_detectedPerRole() throws Exception {
        Model model = new ModelManager();
        parser.parseCommand("add-student n/Alex Tan p/91234567").execute(model);
        parser.parseCommand("add-student n/Ben Lim p/93456789").execute(model);
        parser.parseCommand("add-guardian n/Amelia Tan p/92345678").execute(model);

        // a guardian may take a student's name
        parser.parseCommand("edit G1 n/Ben Lim").execute(model);
        // a contact may change the case of its own name
        parser.parseCommand("edit S1 n/alex tan").execute(model);
        // a student may not take another student's name, in any case
        assertThrows(CommandException.class, EditCommand.MESSAGE_DUPLICATE_PERSON, () ->
                parser.parseCommand("edit S1 n/BEN LIM").execute(model));
    }
}
