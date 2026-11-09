package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.regex.Pattern;

/**
 * The slash-separated connectors that are part of some names, such as the {@code s/o} in
 * {@code Tan Wei Ming s/o Tan Ah Kow}. They look like command parameters ({@code a/ADDRESS}) but are not.
 *
 * <p>The connectors are {@code s/o} (son of), {@code d/o} (daughter of), {@code a/l} (anak lelaki),
 * {@code a/p} (anak perempuan), {@code w/o} (wife of) and {@code c/o} (care of), in any letter case.
 * A connector must be a word of its own, so {@code a/l Kumar} is a connector but {@code a/lane} is not.
 */
final class NameConnectors {

    /** Matches one connector at the current position, as long as it is followed by whitespace or the end. */
    static final String REGEX = "(?:[sSdDwWcC]/[oO]|[aA]/[lLpP])(?=\\s|$)";

    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private NameConnectors() {}

    /**
     * Returns true if a name connector starts at {@code index} of {@code text}.
     */
    static boolean startsAt(String text, int index) {
        requireNonNull(text);
        return PATTERN.matcher(text).region(index, text.length()).lookingAt();
    }
}
