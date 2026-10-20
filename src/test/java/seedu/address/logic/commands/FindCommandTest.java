package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager();
        new AddStudentCommand(new Name("Alex Tan"), new Phone("91234567")).execute(model);
        new AddStudentCommand(new Name("Ben Lim"), new Phone("93456789")).execute(model);
        new AddGuardianCommand(new Name("Amelia Tan"), new Phone("92345678")).execute(model);
        new AddGuardianCommand(new Name("Chloe Ng"), new Phone("94567890")).execute(model);
        new LinkCommand(new StudentId("S1"), new GuardianId("G1")).execute(model);
        new LinkCommand(new StudentId("S2"), new GuardianId("G1")).execute(model);
    }

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(List.of("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(List.of("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noContactFound() {
        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        CommandResult result = new FindCommand(predicate).execute(model);
        assertEquals(FindCommand.MESSAGE_NO_MATCHES, result.getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_legacyPersonsOnly_noContactFound() {
        Model legacyModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        CommandResult result = new FindCommand(preparePredicate("Kurz Elle Kunz")).execute(legacyModel);
        assertEquals(FindCommand.MESSAGE_NO_MATCHES, result.getFeedbackToUser());
        assertEquals(List.of(), legacyModel.getFilteredPersonList());
    }

    @Test
    public void execute_singleKeyword_findsStudentsAndGuardiansWithLinks() {
        CommandResult result = new FindCommand(preparePredicate("tan")).execute(model);

        String expected = "Found 1 student and 1 guardian.\n"
                + "Students:\n"
                + "S1 Alex Tan, 91234567 (guardians: G1)\n"
                + "Guardians:\n"
                + "G1 Amelia Tan, 92345678 (students: S1, S2)";
        assertEquals(expected, result.getFeedbackToUser());
        assertEquals(List.of("S1", "G1"), displayedIds());
    }

    @Test
    public void execute_multipleKeywords_findsAnyMatch() {
        CommandResult result = new FindCommand(preparePredicate("Ben chloe")).execute(model);

        String expected = "Found 1 student and 1 guardian.\n"
                + "Students:\n"
                + "S2 Ben Lim, 93456789 (guardians: G1)\n"
                + "Guardians:\n"
                + "G2 Chloe Ng, 94567890 (students: none)";
        assertEquals(expected, result.getFeedbackToUser());
        assertEquals(List.of("S2", "G2"), displayedIds());
    }

    @Test
    public void execute_partialWord_noContactFound() {
        CommandResult result = new FindCommand(preparePredicate("Ale")).execute(model);
        assertEquals(FindCommand.MESSAGE_NO_MATCHES, result.getFeedbackToUser());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    private List<String> displayedIds() {
        return model.getFilteredPersonList().stream()
                .map(person -> ContactOrder.getContactId(person).orElseThrow())
                .toList();
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(List.of(userInput.split("\\s+")));
    }
}
