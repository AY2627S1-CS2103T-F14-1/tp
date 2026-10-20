package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.model.Model;
import seedu.address.model.link.GuardianLink;
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
    public static final String MESSAGE_CONTACT = "%1$s %2$s, %3$s (%4$s: %5$s)";
    public static final String NO_LINKS = "none";

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
                pluralize(students.size(), "student"), pluralize(guardians.size(), "guardian"))
                + formatContacts(students, guardians, model.getGuardianLinks()));
    }

    /**
     * Formats the students and guardians sections shown below a summary line, each contact with its linked IDs.
     * Sections with no contacts are left out.
     */
    static String formatContacts(List<Person> students, List<Person> guardians, List<GuardianLink> links) {
        StringBuilder message = new StringBuilder();
        if (!students.isEmpty()) {
            message.append("\nStudents:");
            for (Person student : students) {
                String studentId = ContactOrder.getContactId(student).orElseThrow();
                Stream<String> guardianIds = links.stream()
                        .filter(link -> link.getStudentId().toString().equals(studentId))
                        .map(link -> link.getGuardianId().toString());
                message.append('\n').append(formatContact(student, studentId, "guardians", guardianIds));
            }
        }
        if (!guardians.isEmpty()) {
            message.append("\nGuardians:");
            for (Person guardian : guardians) {
                String guardianId = ContactOrder.getContactId(guardian).orElseThrow();
                Stream<String> studentIds = links.stream()
                        .filter(link -> link.getGuardianId().toString().equals(guardianId))
                        .map(link -> link.getStudentId().toString());
                message.append('\n').append(formatContact(guardian, guardianId, "students", studentIds));
            }
        }
        return message.toString();
    }

    /**
     * Formats one contact line, e.g. {@code S1 Alex Tan, 91234567 (guardians: G1, G2)}.
     */
    private static String formatContact(Person contact, String contactId, String linkedLabel,
            Stream<String> linkedIds) {
        String linked = linkedIds.sorted(ContactOrder.ID_COMPARATOR).collect(Collectors.joining(", "));
        return String.format(MESSAGE_CONTACT, contactId, contact.getName(), contact.getPhone(), linkedLabel,
                linked.isEmpty() ? NO_LINKS : linked);
    }

    static String pluralize(int count, String noun) {
        return count + " " + noun + (count == 1 ? "" : "s");
    }
}
