package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.guardian.GuardianId;
import seedu.address.model.link.GuardianLink;
import seedu.address.model.student.StudentId;

/** Jackson-friendly version of a {@link GuardianLink}. */
class JsonAdaptedGuardianLink {
    private final String studentId;
    private final String guardianId;

    @JsonCreator
    JsonAdaptedGuardianLink(@JsonProperty("studentId") String studentId,
            @JsonProperty("guardianId") String guardianId) {
        this.studentId = studentId;
        this.guardianId = guardianId;
    }

    JsonAdaptedGuardianLink(GuardianLink source) {
        studentId = source.getStudentId().toString();
        guardianId = source.getGuardianId().toString();
    }

    GuardianLink toModelType() throws IllegalValueException {
        if (studentId == null || guardianId == null
                || !StudentId.isValidStudentId(studentId) || !GuardianId.isValidGuardianId(guardianId)) {
            throw new IllegalValueException("Guardian link contains an invalid contact ID.");
        }
        return new GuardianLink(new StudentId(studentId), new GuardianId(guardianId));
    }
}
