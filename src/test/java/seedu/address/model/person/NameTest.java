package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains non-alphanumeric characters
        assertFalse(Name.isValidName("陈小明*")); // non-ASCII letters with a symbol
        assertFalse(Name.isValidName("a\u00A0b")); // non-breaking space is not a plain space
        assertFalse(Name.isValidName("a".repeat(101))); // more than 100 characters
        assertFalse(Name.isValidName("-Alex")); // starts with a hyphen
        assertFalse(Name.isValidName("'Alex")); // starts with an apostrophe
        assertFalse(Name.isValidName("Alex, Tan")); // contains a comma

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("a".repeat(100))); // exactly 100 characters
        assertTrue(Name.isValidName("陈小明")); // Chinese characters
        assertTrue(Name.isValidName("Zoë Müller")); // accented letters
        assertTrue(Name.isValidName("José")); // accented letter at the end
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        assertTrue(Name.isValidName("O'Brien")); // apostrophe
        assertTrue(Name.isValidName("O\u2019Brien")); // typographic apostrophe
        assertTrue(Name.isValidName("Mary-Jane")); // hyphen
        assertTrue(Name.isValidName("Tan Wei Ming s/o Tan Ah Kow")); // slash
        assertTrue(Name.isValidName("Robert Downey Jr.")); // full stop
        assertTrue(Name.isValidName("Zoë D'Souza-Núñez")); // accented letters with punctuation
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
