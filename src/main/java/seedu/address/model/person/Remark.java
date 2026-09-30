package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's remark in the address book.
 * Guarantees: immutable; is always valid.
 *
 * <p>Unlike the other person fields, a remark has no constraints: any text is
 * acceptable, and an empty remark simply means the person has no remark.
 * That is why this class has no {@code VALIDATION_REGEX} or
 * {@code MESSAGE_CONSTRAINTS} counterpart to {@link Address}.
 */
public class Remark {

    public final String value;

    /**
     * Constructs a {@code Remark}.
     *
     * @param remark Any remark text; may be empty, but not null.
     */
    public Remark(String remark) {
        requireNonNull(remark);
        value = remark;
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

        // instanceof handles nulls
        if (!(other instanceof Remark otherRemark)) {
            return false;
        }

        return value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
