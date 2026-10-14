package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.guardian.GuardianId;
import seedu.address.model.person.ContactOrder;
import seedu.address.model.person.Name;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Phone;
import seedu.address.model.student.StudentId;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_NO_CONTACTS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_NO_CONTACTS, expectedModel);
    }

    @Test
    public void execute_emptyModel_showsNoContactsMessage() {
        CommandResult result = new ListCommand().execute(new ModelManager());
        assertEquals(ListCommand.MESSAGE_NO_CONTACTS, result.getFeedbackToUser());
    }

    @Test
    public void execute_singleUnlinkedStudent_showsSingularSummaryAndNoLinks() throws Exception {
        Model tutorConnectModel = new ModelManager();
        new AddStudentCommand(new Name("Alex Tan"), new Phone("91234567")).execute(tutorConnectModel);

        String expected = "Listed 1 student and 0 guardians.\n"
                + "Students:\n"
                + "S1 Alex Tan, 91234567 (guardians: none)";
        assertEquals(expected, new ListCommand().execute(tutorConnectModel).getFeedbackToUser());
    }

    @Test
    public void execute_studentsAndGuardians_listsInIdOrderWithRelationships() throws Exception {
        Model tutorConnectModel = new ModelManager();
        new AddGuardianCommand(new Name("Amelia Tan"), new Phone("92345678")).execute(tutorConnectModel);
        new AddStudentCommand(new Name("Alex Tan"), new Phone("91234567")).execute(tutorConnectModel);
        new AddStudentCommand(new Name("Ben Lim"), new Phone("93456789")).execute(tutorConnectModel);
        new AddGuardianCommand(new Name("Chloe Ng"), new Phone("94567890")).execute(tutorConnectModel);
        new LinkCommand(new StudentId("S1"), new GuardianId("G2")).execute(tutorConnectModel);
        new LinkCommand(new StudentId("S1"), new GuardianId("G1")).execute(tutorConnectModel);
        new LinkCommand(new StudentId("S2"), new GuardianId("G1")).execute(tutorConnectModel);
        tutorConnectModel.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Ben")));

        CommandResult result = new ListCommand().execute(tutorConnectModel);

        String expected = "Listed 2 students and 2 guardians.\n"
                + "Students:\n"
                + "S1 Alex Tan, 91234567 (guardians: G1, G2)\n"
                + "S2 Ben Lim, 93456789 (guardians: G1)\n"
                + "Guardians:\n"
                + "G1 Amelia Tan, 92345678 (students: S1, S2)\n"
                + "G2 Chloe Ng, 94567890 (students: S1)";
        assertEquals(expected, result.getFeedbackToUser());
        List<String> displayedIds = tutorConnectModel.getFilteredPersonList().stream()
                .map(person -> ContactOrder.getContactId(person).orElseThrow())
                .toList();
        assertEquals(List.of("S1", "S2", "G1", "G2"), displayedIds);
    }
}
