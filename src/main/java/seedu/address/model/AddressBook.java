package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.guardian.GuardianId;
import seedu.address.model.link.GuardianLink;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;
import seedu.address.model.student.StudentId;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons = new UniquePersonList();
    private final List<GuardianLink> guardianLinks = new ArrayList<>();
    private int nextStudentNumber = 1;
    private int nextGuardianNumber = 1;

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        guardianLinks.clear();
        guardianLinks.addAll(newData.getGuardianLinks());
        nextStudentNumber = newData.getNextStudentNumber();
        nextGuardianNumber = newData.getNextGuardianNumber();
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    /** Deletes a TutorConnect contact and its related guardian links. */
    public boolean deleteContact(String contactId) {
        Person contact = persons.asUnmodifiableObservableList().stream()
                .filter(person -> person.getTags().stream().anyMatch(tag -> tag.tagName.equals(contactId)))
                .findFirst().orElse(null);
        if (contact == null) {
            return false;
        }
        persons.remove(contact);
        if (StudentId.isValidStudentId(contactId)) {
            StudentId studentId = new StudentId(contactId);
            guardianLinks.removeIf(link -> link.involves(studentId));
        } else {
            GuardianId guardianId = new GuardianId(contactId);
            guardianLinks.removeIf(link -> link.involves(guardianId));
        }
        return true;
    }

    /** Allocates the next stable student identifier. */
    public StudentId allocateStudentId() {
        return new StudentId("S" + nextStudentNumber++);
    }

    /** Allocates the next stable guardian identifier. */
    public GuardianId allocateGuardianId() {
        return new GuardianId("G" + nextGuardianNumber++);
    }

    /** Adds a relationship after its contacts have been validated by the model. */
    public void addGuardianLink(GuardianLink link) {
        requireNonNull(link);
        if (guardianLinks.contains(link)) {
            throw new IllegalArgumentException("That guardian is already linked to that student.");
        }
        guardianLinks.add(link);
    }

    /** Restores the saved TutorConnect relationships and allocation counters. */
    public void restoreTutorConnectData(List<GuardianLink> links, int savedNextStudentNumber,
            int savedNextGuardianNumber) {
        requireNonNull(links);
        guardianLinks.clear();
        guardianLinks.addAll(links);
        nextStudentNumber = Math.max(1, savedNextStudentNumber);
        nextGuardianNumber = Math.max(1, savedNextGuardianNumber);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public List<GuardianLink> getGuardianLinks() {
        return List.copyOf(guardianLinks);
    }

    @Override
    public int getNextStudentNumber() {
        return nextStudentNumber;
    }

    @Override
    public int getNextGuardianNumber() {
        return nextGuardianNumber;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons);
    }

    @Override
    public int hashCode() {
        return persons.hashCode();
    }
}
