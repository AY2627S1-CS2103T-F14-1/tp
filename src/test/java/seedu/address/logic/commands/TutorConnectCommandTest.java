package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.guardian.GuardianId;
import seedu.address.model.person.Name;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Phone;
import seedu.address.model.student.StudentId;

/** Tests the TutorConnect MVP command flow against the model. */
public class TutorConnectCommandTest {

    @Test
    public void execute_contactCommands_assignIdsAndRecordLink() throws CommandException {
        ModelManager model = new ModelManager();

        CommandResult studentResult = new AddStudentCommand(new Name("Alex Tan"), new Phone("91234567"))
                .execute(model);
        CommandResult guardianResult = new AddGuardianCommand(new Name("Amelia Tan"), new Phone("92345678"))
                .execute(model);
        CommandResult linkResult = new LinkCommand(new StudentId("S1"), new GuardianId("G1")).execute(model);

        assertEquals("Added student S1: Alex Tan", studentResult.getFeedbackToUser());
        assertEquals("Added guardian G1: Amelia Tan", guardianResult.getFeedbackToUser());
        assertEquals("Linked G1 to S1.", linkResult.getFeedbackToUser());
        assertTrue(model.hasContact("S1"));
        assertTrue(model.hasContact("G1"));
        assertEquals(1, model.getGuardianLinks().size());
    }

    @Test
    public void execute_linkAfterFind_showsAllContactsAgain() throws CommandException {
        ModelManager model = new ModelManager();
        new AddStudentCommand(new Name("Alex Tan"), new Phone("91234567")).execute(model);
        new AddStudentCommand(new Name("Bella Lim"), new Phone("93456789")).execute(model);
        new AddGuardianCommand(new Name("Amelia Tan"), new Phone("92345678")).execute(model);
        new FindCommand(new NameContainsKeywordsPredicate(List.of("Bella"))).execute(model);
        assertEquals(1, model.getFilteredPersonList().size());

        new LinkCommand(new StudentId("S2"), new GuardianId("G1")).execute(model);

        assertEquals(3, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_deleteContact_removesLinksAndDoesNotReuseId() throws CommandException {
        ModelManager model = new ModelManager();
        new AddStudentCommand(new Name("Alex Tan"), new Phone("91234567")).execute(model);
        new AddGuardianCommand(new Name("Amelia Tan"), new Phone("92345678")).execute(model);
        new LinkCommand(new StudentId("S1"), new GuardianId("G1")).execute(model);

        CommandResult deleteResult = new DeleteContactCommand("S1").execute(model);
        CommandResult nextStudent = new AddStudentCommand(new Name("Bella Lim"), new Phone("93456789"))
                .execute(model);

        assertEquals("Deleted contact S1 and its relationships.", deleteResult.getFeedbackToUser());
        assertEquals(0, model.getGuardianLinks().size());
        assertEquals("Added student S2: Bella Lim", nextStudent.getFeedbackToUser());
    }
}
