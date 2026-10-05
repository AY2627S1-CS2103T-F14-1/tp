package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a student's stable identifier in TutorConnect.
 * Guarantees: immutable; is valid as declared in {@link #isValidStudentId(String)}.
 */
public class StudentId {

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid ID: expected S followed by a number, e.g. S1.";
    public static final String VALIDATION_REGEX = "S[1-9][0-9]*";

    private final String value;

    /**
     * Constructs a {@code StudentId}.
     *
     * @param studentId A valid student ID.
     */
    public StudentId(String studentId) {
        requireNonNull(studentId);
        checkArgument(isValidStudentId(studentId), MESSAGE_CONSTRAINTS);
        value = studentId;
    }

    /**
     * Returns true if the given string is a valid student ID.
     */
    public static boolean isValidStudentId(String test) {
        requireNonNull(test);
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof StudentId otherStudentId)) {
            return false;
        }

        return value.equals(otherStudentId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
