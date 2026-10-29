package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.ContactOrder;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;

/**
 * Finds and lists all TutorConnect students and guardians whose name, ID or phone number matches any of the
 * argument keywords, with their relationships. Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all students and guardians whose names, "
            + "IDs or phone numbers match any of the specified keywords (case-insensitive) and lists them "
            + "with their IDs.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " alex amelia S3 91234567";

    public static final String MESSAGE_NO_MATCHES = "No students or guardians match your search.";
    public static final String MESSAGE_SUMMARY = "Found %1$s and %2$s.";

    private static final Logger logger = LogsCenter.getLogger(FindCommand.class);

    private final NameContainsKeywordsPredicate predicate;

    /**
     * Creates a FindCommand that finds students and guardians whose names match {@code predicate}.
     */
    public FindCommand(NameContainsKeywordsPredicate predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        Predicate<Person> isContact = person -> ContactOrder.isStudent(person) || ContactOrder.isGuardian(person);
        model.updateFilteredPersonList(isContact.and(predicate));

        List<Person> matches = model.getFilteredPersonList();
        List<Person> students = matches.stream().filter(ContactOrder::isStudent).toList();
        List<Person> guardians = matches.stream().filter(ContactOrder::isGuardian).toList();
        assert students.size() + guardians.size() == matches.size()
                : "Filtered list should only contain students and guardians";
        logger.fine("Found " + students.size() + " students and " + guardians.size() + " guardians for "
                + predicate);
        if (students.isEmpty() && guardians.isEmpty()) {
            return new CommandResult(MESSAGE_NO_MATCHES);
        }

        return new CommandResult(String.format(MESSAGE_SUMMARY,
                Messages.pluralize(students.size(), "student"), Messages.pluralize(guardians.size(), "guardian"))
                + Messages.formatContacts(students, guardians, model.getGuardianLinks()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
