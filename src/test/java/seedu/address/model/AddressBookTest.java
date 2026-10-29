package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.guardian.GuardianId;
import seedu.address.model.link.GuardianLink;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.student.StudentId;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getPersonList());
    }

    @Test
    public void setPersons_contactsOutOfOrder_sortsByContactId() {
        Person guardian = new PersonBuilder().withName("Amelia Tan").withTags("guardian", "G1").build();
        Person student10 = new PersonBuilder().withName("Ben Lim").withTags("student", "S10").build();
        Person student2 = new PersonBuilder().withName("Alex Tan").withTags("student", "S2").build();
        addressBook.setPersons(List.of(BENSON, guardian, ALICE, student10, student2));
        assertEquals(List.of(student2, student10, guardian, BENSON, ALICE), addressBook.getPersonList());
    }

    @Test
    public void setPerson_contactWithoutId_keepsPosition() {
        addressBook.setPersons(List.of(ALICE, BENSON, CARL));
        Person editedBenson = new PersonBuilder(BENSON).withTags(VALID_TAG_HUSBAND).build();
        addressBook.setPerson(BENSON, editedBenson);
        assertEquals(List.of(ALICE, editedBenson, CARL), addressBook.getPersonList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newPersons = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void removePerson_linkedStudent_removesOnlyItsLinks() {
        Person student1 = new PersonBuilder().withName("Alex Tan").withTags("student", "S1").build();
        Person student2 = new PersonBuilder().withName("Ben Lim").withTags("student", "S2").build();
        Person guardian = new PersonBuilder().withName("Amelia Tan").withTags("guardian", "G1").build();
        addressBook.setPersons(List.of(student1, student2, guardian));
        GuardianLink keptLink = new GuardianLink(new StudentId("S2"), new GuardianId("G1"));
        addressBook.addGuardianLink(new GuardianLink(new StudentId("S1"), new GuardianId("G1")));
        addressBook.addGuardianLink(keptLink);

        addressBook.removePerson(student1);

        assertEquals(List.of(keptLink), addressBook.getGuardianLinks());
    }

    @Test
    public void removePerson_linkedGuardian_removesItsLinks() {
        Person student = new PersonBuilder().withName("Alex Tan").withTags("student", "S1").build();
        Person guardian = new PersonBuilder().withName("Amelia Tan").withTags("guardian", "G1").build();
        addressBook.setPersons(List.of(student, guardian));
        addressBook.addGuardianLink(new GuardianLink(new StudentId("S1"), new GuardianId("G1")));

        addressBook.removePerson(guardian);

        assertEquals(List.of(), addressBook.getGuardianLinks());
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{persons=" + addressBook.getPersonList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public java.util.List<seedu.address.model.link.GuardianLink> getGuardianLinks() {
            return java.util.List.of();
        }

        @Override
        public int getNextStudentNumber() {
            return 1;
        }

        @Override
        public int getNextGuardianNumber() {
            return 1;
        }
    }

}
