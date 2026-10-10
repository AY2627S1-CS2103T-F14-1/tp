package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class StudentIdTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudentId(null));
    }

    @Test
    public void constructor_invalidStudentId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new StudentId("S0"));
    }

    @Test
    public void isValidStudentId() {
        assertThrows(NullPointerException.class, () -> StudentId.isValidStudentId(null));

        assertFalse(StudentId.isValidStudentId(""));
        assertFalse(StudentId.isValidStudentId("S"));
        assertFalse(StudentId.isValidStudentId("S0"));
        assertFalse(StudentId.isValidStudentId("S01"));
        assertFalse(StudentId.isValidStudentId("s1"));
        assertFalse(StudentId.isValidStudentId("G1"));
        assertFalse(StudentId.isValidStudentId("S1 "));

        assertTrue(StudentId.isValidStudentId("S1"));
        assertTrue(StudentId.isValidStudentId("S12"));
        assertTrue(StudentId.isValidStudentId("S999999"));
    }

    @Test
    public void toString_returnsStudentId() {
        assertEquals("S12", new StudentId("S12").toString());
    }

    @Test
    public void equals() {
        StudentId studentId = new StudentId("S1");

        assertTrue(studentId.equals(new StudentId("S1")));
        assertTrue(studentId.equals(studentId));
        assertFalse(studentId.equals(null));
        assertFalse(studentId.equals("S1"));
        assertFalse(studentId.equals(new StudentId("S2")));
    }
}
