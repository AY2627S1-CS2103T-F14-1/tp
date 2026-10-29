package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.ContactOrder;

/**
 * Clears the address book, but only once the user has confirmed with {@code clear confirm}.
 * Without the confirmation, nothing is deleted and the user is told what would be lost.
 * Student and guardian ID counters are kept so that IDs are never reused.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String CONFIRMATION_KEYWORD = "confirm";
    public static final String MESSAGE_SUCCESS = "All contacts and links have been cleared.";
    public static final String MESSAGE_CONFIRMATION_REQUIRED = "This will delete %1$s and %2$s and cannot be undone.\n"
            + "To go ahead, type: " + COMMAND_WORD + " " + CONFIRMATION_KEYWORD;

    private final boolean isConfirmed;

    /**
     * Creates a command that clears the address book if {@code isConfirmed} is true, and otherwise only asks for
     * confirmation.
     */
    public ClearCommand(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        ReadOnlyAddressBook current = model.getAddressBook();
        if (!isConfirmed && !current.getPersonList().isEmpty()) {
            long students = current.getPersonList().stream().filter(ContactOrder::isStudent).count();
            long guardians = current.getPersonList().stream().filter(ContactOrder::isGuardian).count();
            return new CommandResult(String.format(MESSAGE_CONFIRMATION_REQUIRED,
                    Messages.pluralize((int) students, "student"), Messages.pluralize((int) guardians, "guardian")));
        }
        AddressBook cleared = new AddressBook();
        cleared.restoreTutorConnectData(List.of(), current.getNextStudentNumber(),
                current.getNextGuardianNumber());
        model.setAddressBook(cleared);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof ClearCommand otherClearCommand && isConfirmed == otherClearCommand.isConfirmed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isConfirmed);
    }
}
