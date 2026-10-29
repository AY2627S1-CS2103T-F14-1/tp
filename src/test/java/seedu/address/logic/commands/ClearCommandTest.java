package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

public class ClearCommandTest {

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(true), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ClearCommand(true), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_unconfirmedNonEmptyAddressBook_asksForConfirmationAndKeepsData() throws Exception {
        Model model = new ModelManager();
        new AddStudentCommand(new Name("Alex Tan"), new Phone("91234567")).execute(model);
        new AddStudentCommand(new Name("Bella Lim"), new Phone("93456789")).execute(model);
        new AddGuardianCommand(new Name("Amelia Tan"), new Phone("92345678")).execute(model);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(new ClearCommand(false), model, "This will delete 2 students and 1 guardian and "
                + "cannot be undone.\nTo go ahead, type: clear confirm", expectedModel);
    }

    @Test
    public void execute_unconfirmedEmptyAddressBook_success() {
        assertCommandSuccess(new ClearCommand(false), new ModelManager(), ClearCommand.MESSAGE_SUCCESS,
                new ModelManager());
    }

    @Test
    public void equals() {
        assertEquals(new ClearCommand(true), new ClearCommand(true));
        assertEquals(new ClearCommand(false), new ClearCommand(false));
        assertNotEquals(new ClearCommand(true), new ClearCommand(false));
        assertNotEquals(new ClearCommand(true), null);
    }

    @Test
    public void execute_afterAddingContacts_doesNotReuseIds() throws Exception {
        Model model = new ModelManager();
        new AddStudentCommand(new Name("Alex Tan"), new Phone("91234567")).execute(model);
        new AddGuardianCommand(new Name("Amelia Tan"), new Phone("92345678")).execute(model);

        new ClearCommand(true).execute(model);
        CommandResult studentResult = new AddStudentCommand(new Name("Bella Lim"), new Phone("93456789"))
                .execute(model);
        CommandResult guardianResult = new AddGuardianCommand(new Name("Brenda Lim"), new Phone("94567890"))
                .execute(model);

        assertEquals("Added student S2: Bella Lim", studentResult.getFeedbackToUser());
        assertEquals("Added guardian G2: Brenda Lim", guardianResult.getFeedbackToUser());
    }

}
