package seedu.address.model.link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.guardian.GuardianId;
import seedu.address.model.student.StudentId;

public class GuardianLinkTest {

    private static final StudentId STUDENT_1 = new StudentId("S1");
    private static final StudentId STUDENT_2 = new StudentId("S2");
    private static final GuardianId GUARDIAN_1 = new GuardianId("G1");
    private static final GuardianId GUARDIAN_2 = new GuardianId("G2");

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GuardianLink(null, GUARDIAN_1));
        assertThrows(NullPointerException.class, () -> new GuardianLink(STUDENT_1, null));
    }

    @Test
    public void getters_returnLinkedIds() {
        GuardianLink link = new GuardianLink(STUDENT_1, GUARDIAN_1);
        assertEquals(STUDENT_1, link.getStudentId());
        assertEquals(GUARDIAN_1, link.getGuardianId());
    }

    @Test
    public void involves_student() {
        GuardianLink link = new GuardianLink(STUDENT_1, GUARDIAN_1);
        assertTrue(link.involves(new StudentId("S1")));
        assertFalse(link.involves(STUDENT_2));
    }

    @Test
    public void involves_guardian() {
        GuardianLink link = new GuardianLink(STUDENT_1, GUARDIAN_1);
        assertTrue(link.involves(new GuardianId("G1")));
        assertFalse(link.involves(GUARDIAN_2));
    }

    @Test
    public void equals() {
        GuardianLink link = new GuardianLink(STUDENT_1, GUARDIAN_1);

        // same student and guardian -> equal, i.e. a duplicate link
        assertTrue(link.equals(new GuardianLink(new StudentId("S1"), new GuardianId("G1"))));
        assertTrue(link.equals(link));

        assertFalse(link.equals(null));
        assertFalse(link.equals("S1-G1"));
        assertFalse(link.equals(new GuardianLink(STUDENT_2, GUARDIAN_1)));
        assertFalse(link.equals(new GuardianLink(STUDENT_1, GUARDIAN_2)));
    }

    @Test
    public void hashCode_equalLinks_sameHashCode() {
        assertEquals(new GuardianLink(STUDENT_1, GUARDIAN_1).hashCode(),
                new GuardianLink(new StudentId("S1"), new GuardianId("G1")).hashCode());
    }

    @Test
    public void toStringMethod() {
        GuardianLink link = new GuardianLink(STUDENT_1, GUARDIAN_1);
        String expected = GuardianLink.class.getCanonicalName() + "{studentId=S1, guardianId=G1}";
        assertEquals(expected, link.toString());
    }
}
