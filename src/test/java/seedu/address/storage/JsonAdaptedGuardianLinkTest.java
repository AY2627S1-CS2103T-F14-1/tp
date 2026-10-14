package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.guardian.GuardianId;
import seedu.address.model.link.GuardianLink;
import seedu.address.model.student.StudentId;

public class JsonAdaptedGuardianLinkTest {

    @Test
    public void toModelType_validIds_returnsLink() throws Exception {
        GuardianLink expectedLink = new GuardianLink(new StudentId("S1"), new GuardianId("G1"));

        assertEquals(expectedLink, new JsonAdaptedGuardianLink("S1", "G1").toModelType());
    }

    @Test
    public void toModelType_missingOrInvalidIds_throwsIllegalValueException() {
        assertInvalidLink(null, "G1");
        assertInvalidLink("S1", null);
        assertInvalidLink("student", "G1");
        assertInvalidLink("S1", "guardian");
    }

    private void assertInvalidLink(String studentId, String guardianId) {
        String message = "Guardian link contains an invalid contact ID.";
        JsonAdaptedGuardianLink link = new JsonAdaptedGuardianLink(studentId, guardianId);
        assertThrows(IllegalValueException.class, message, link::toModelType);
    }
}
