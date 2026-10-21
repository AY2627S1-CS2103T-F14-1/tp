package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.ContactOrder;
import seedu.address.model.person.Person;

/**
 * Lists all TutorConnect students and guardians, with their relationships, to the user.
 * Contacts are shown in {@link ContactOrder} order: students, then guardians, each by ID number.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_NO_CONTACTS = "No students or guardians yet.";
    public static final String MESSAGE_SUMMARY = "Listed %1$s and %2$s.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        List<Person> students = model.getFilteredPersonList().stream().filter(ContactOrder::isStudent).toList();
        List<Person> guardians = model.getFilteredPersonList().stream().filter(ContactOrder::isGuardian).toList();
        if (students.isEmpty() && guardians.isEmpty()) {
            return new CommandResult(MESSAGE_NO_CONTACTS);
        }

        return new CommandResult(String.format(MESSAGE_SUMMARY,
                Messages.pluralize(students.size(), "student"), Messages.pluralize(guardians.size(), "guardian"))
                + Messages.formatContacts(students, guardians, model.getGuardianLinks()));
    }

}
