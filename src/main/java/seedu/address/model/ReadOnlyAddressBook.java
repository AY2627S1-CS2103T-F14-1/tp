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
    List<GuardianLink> getGuardianLinks();

    /** Returns the next student number to allocate. */
    int getNextStudentNumber();

    /** Returns the next guardian number to allocate. */
    int getNextGuardianNumber();

}
