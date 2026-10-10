package seedu.address.model.guardian;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GuardianIdTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GuardianId(null));
    }

    @Test
    public void constructor_invalidGuardianId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new GuardianId("G0"));
    }

    @Test
    public void isValidGuardianId() {
        assertThrows(NullPointerException.class, () -> GuardianId.isValidGuardianId(null));

        assertFalse(GuardianId.isValidGuardianId(""));
        assertFalse(GuardianId.isValidGuardianId("G"));
        assertFalse(GuardianId.isValidGuardianId("G0"));
        assertFalse(GuardianId.isValidGuardianId("G01"));
        assertFalse(GuardianId.isValidGuardianId("g1"));
        assertFalse(GuardianId.isValidGuardianId("S1"));
        assertFalse(GuardianId.isValidGuardianId("G1 "));

        assertTrue(GuardianId.isValidGuardianId("G1"));
        assertTrue(GuardianId.isValidGuardianId("G12"));
        assertTrue(GuardianId.isValidGuardianId("G999999"));
    }

    @Test
    public void toString_returnsGuardianId() {
        assertEquals("G12", new GuardianId("G12").toString());
    }

    @Test
    public void equals() {
        GuardianId guardianId = new GuardianId("G1");

        assertTrue(guardianId.equals(new GuardianId("G1")));
        assertTrue(guardianId.equals(guardianId));
        assertFalse(guardianId.equals(null));
        assertFalse(guardianId.equals("G1"));
        assertFalse(guardianId.equals(new GuardianId("G2")));
    }

    @Test
    public void hashCode_equalIds_sameHashCode() {
        assertEquals(new GuardianId("G1").hashCode(), new GuardianId("G1").hashCode());
    }
}
