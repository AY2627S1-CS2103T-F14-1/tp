package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class ContactOrderTest {

    private static final Person STUDENT_2 = new PersonBuilder().withName("Student Two")
            .withTags("student", "S2").build();
    private static final Person STUDENT_10 = new PersonBuilder().withName("Student Ten").withTags("S10").build();
    private static final Person GUARDIAN_1 = new PersonBuilder().withName("Guardian One").withTags("G1").build();
    private static final Person NO_ID_A = new PersonBuilder().withName("No Id A").withTags("friends").build();
    private static final Person NO_ID_B = new PersonBuilder().withName("No Id B").build();

    @Test
    public void getContactId_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ContactOrder.getContactId(null));
    }

    @Test
    public void getContactId() {
        assertEquals(Optional.of("S2"), ContactOrder.getContactId(STUDENT_2));
        assertEquals(Optional.of("G1"), ContactOrder.getContactId(GUARDIAN_1));
        assertEquals(Optional.empty(), ContactOrder.getContactId(NO_ID_A));
        assertEquals(Optional.empty(), ContactOrder.getContactId(NO_ID_B));
    }

    @Test
    public void isStudentAndIsGuardian() {
        assertTrue(ContactOrder.isStudent(STUDENT_2));
        assertFalse(ContactOrder.isGuardian(STUDENT_2));
        assertTrue(ContactOrder.isGuardian(GUARDIAN_1));
        assertFalse(ContactOrder.isStudent(GUARDIAN_1));
        assertFalse(ContactOrder.isStudent(NO_ID_A));
        assertFalse(ContactOrder.isGuardian(NO_ID_A));
    }

    @Test
    public void comparator_sortsStudentsThenGuardiansThenOthersByIdNumber() {
        List<Person> contacts = new ArrayList<>(List.of(NO_ID_A, GUARDIAN_1, STUDENT_10, NO_ID_B, STUDENT_2));
        contacts.sort(ContactOrder.COMPARATOR);
        assertEquals(List.of(STUDENT_2, STUDENT_10, GUARDIAN_1, NO_ID_A, NO_ID_B), contacts);
    }

    @Test
    public void idComparator_comparesNumericallyWithoutOverflow() {
        List<String> ids = new ArrayList<>(List.of("G2", "S10", "S99999999999999999999", "G1", "S2"));
        ids.sort(ContactOrder.ID_COMPARATOR);
        assertEquals(List.of("S2", "S10", "S99999999999999999999", "G1", "G2"), ids);
    }
}
