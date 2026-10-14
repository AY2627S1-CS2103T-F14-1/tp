package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Optional;

import seedu.address.model.guardian.GuardianId;
import seedu.address.model.student.StudentId;

/**
 * Identifies TutorConnect contacts by their stable ID and defines their display order:
 * students first, then guardians, each by ID number, then any contacts without an ID.
 */
public class ContactOrder {

    /**
     * Orders contact IDs such as {@code S2} and {@code G10}: students before guardians, then by ID number.
     * IDs have no leading zeros, so comparing by length and then by text matches numeric order without overflow.
     */
    public static final Comparator<String> ID_COMPARATOR = Comparator
            .comparingInt(ContactOrder::groupOf)
            .thenComparingInt(String::length)
            .thenComparing(Comparator.naturalOrder());

    /** Orders contacts deterministically; contacts without an ID compare equal and keep their order. */
    public static final Comparator<Person> COMPARATOR = Comparator.comparing(
            ContactOrder::getContactId, (first, second) -> first.isEmpty() || second.isEmpty()
                    ? Boolean.compare(first.isEmpty(), second.isEmpty())
                    : ID_COMPARATOR.compare(first.get(), second.get()));

    private ContactOrder() {}

    /**
     * Returns the student or guardian ID of {@code person}, or an empty Optional if it has none.
     */
    public static Optional<String> getContactId(Person person) {
        requireNonNull(person);
        return person.getTags().stream()
                .map(tag -> tag.tagName)
                .filter(tagName -> StudentId.isValidStudentId(tagName) || GuardianId.isValidGuardianId(tagName))
                .findFirst();
    }

    /** Returns true if {@code person} is a student. */
    public static boolean isStudent(Person person) {
        return getContactId(person).filter(StudentId::isValidStudentId).isPresent();
    }

    /** Returns true if {@code person} is a guardian. */
    public static boolean isGuardian(Person person) {
        return getContactId(person).filter(GuardianId::isValidGuardianId).isPresent();
    }

    private static int groupOf(String contactId) {
        return StudentId.isValidStudentId(contactId) ? 0 : 1;
    }
}
