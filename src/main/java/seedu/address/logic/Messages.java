package seedu.address.logic;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.link.GuardianLink;
import seedu.address.model.person.ContactOrder;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d person(s) listed!";
    public static final String MESSAGE_UNKNOWN_PARAMETER = "Unrecognised parameter \"%1$s\".\n%2$s";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_CONTACT = "%1$s %2$s, %3$s (%4$s: %5$s)";
    public static final String NO_LINKS = "none";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

    /**
     * Formats the students and guardians sections shown below a summary line, each contact with its linked IDs.
     * Sections with no contacts are left out.
     */
    public static String formatContacts(List<Person> students, List<Person> guardians, List<GuardianLink> links) {
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

    /**
     * Returns {@code count} followed by {@code noun}, adding "s" unless the count is 1, e.g. {@code 2 students}.
     */
    public static String pluralize(int count, String noun) {
        return count + " " + noun + (count == 1 ? "" : "s");
    }

}
