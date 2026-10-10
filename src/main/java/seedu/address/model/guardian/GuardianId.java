package seedu.address.model.guardian;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a guardian's stable identifier in TutorConnect.
 * Guarantees: immutable; is valid as declared in {@link #isValidGuardianId(String)}.
 */
public class GuardianId {

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid ID: expected G followed by a number, e.g. G1.";
    public static final String VALIDATION_REGEX = "G[1-9][0-9]*";

    private final String value;

    /**
     * Constructs a {@code GuardianId}.
     *
     * @param guardianId A valid guardian ID.
     */
    public GuardianId(String guardianId) {
        requireNonNull(guardianId);
        checkArgument(isValidGuardianId(guardianId), MESSAGE_CONSTRAINTS);
        value = guardianId;
    }

    /**
     * Returns true if the given string is a valid guardian ID.
     */
    public static boolean isValidGuardianId(String test) {
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

        if (!(other instanceof GuardianId otherGuardianId)) {
            return false;
        }

        return value.equals(otherGuardianId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
