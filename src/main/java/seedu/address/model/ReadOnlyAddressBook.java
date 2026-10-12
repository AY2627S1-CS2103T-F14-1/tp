package seedu.address.model;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.model.link.GuardianLink;
import seedu.address.model.person.Person;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

    /** Returns the recorded guardian-student relationships. */
    default List<GuardianLink> getGuardianLinks() {
        return List.of();
    }

    /** Returns the next student number to allocate. */
    default int getNextStudentNumber() {
        return 1;
    }

    /** Returns the next guardian number to allocate. */
    default int getNextGuardianNumber() {
        return 1;
    }

}
