package seedu.address.model.link;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.guardian.GuardianId;
import seedu.address.model.student.StudentId;

/**
 * Represents a link between a guardian and a student in TutorConnect,
 * e.g. guardian {@code G1} is the parent of student {@code S1}.
 * Guarantees: immutable; both IDs are present and not null.
 *
 * Two links are equal if they connect the same student and guardian, which lets
 * TutorConnect detect a duplicate link before recording it.
 */
public class GuardianLink {

    private final StudentId studentId;
    private final GuardianId guardianId;

    /**
     * Constructs a {@code GuardianLink} between the given student and guardian.
     *
     * @param studentId ID of the linked student.
     * @param guardianId ID of the linked guardian.
     */
    public GuardianLink(StudentId studentId, GuardianId guardianId) {
        requireAllNonNull(studentId, guardianId);
        this.studentId = studentId;
        this.guardianId = guardianId;
    }

    public StudentId getStudentId() {
        return studentId;
    }

    public GuardianId getGuardianId() {
        return guardianId;
    }

    /**
     * Returns true if this link involves the given student.
     * Useful for finding links to remove when a student is deleted.
     */
    public boolean involves(StudentId otherStudentId) {
        return studentId.equals(otherStudentId);
    }

    /**
     * Returns true if this link involves the given guardian.
     * Useful for finding links to remove when a guardian is deleted.
     */
    public boolean involves(GuardianId otherGuardianId) {
        return guardianId.equals(otherGuardianId);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof GuardianLink otherLink)) {
            return false;
        }

        return studentId.equals(otherLink.studentId)
                && guardianId.equals(otherLink.guardianId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, guardianId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("studentId", studentId)
                .add("guardianId", guardianId)
                .toString();
    }
}
