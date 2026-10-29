package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person} matches any of the keywords given. A keyword matches if it is a word of the
 * person's {@code Name}, the person's student or guardian ID, or the person's whole phone number.
 * Matching ignores case.
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public NameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        return keywords.stream().anyMatch(keyword -> matchesName(person, keyword)
                || matchesContactId(person, keyword) || matchesPhone(person, keyword));
    }

    private static boolean matchesName(Person person, String keyword) {
        return StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword);
    }

    private static boolean matchesContactId(Person person, String keyword) {
        return ContactOrder.getContactId(person).filter(keyword::equalsIgnoreCase).isPresent();
    }

    private static boolean matchesPhone(Person person, String keyword) {
        return person.getPhone().value.equals(keyword);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsKeywordsPredicate otherNameContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherNameContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
