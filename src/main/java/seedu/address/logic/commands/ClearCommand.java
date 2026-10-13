package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * Clears the address book.
 * Student and guardian ID counters are kept so that IDs are never reused.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        ReadOnlyAddressBook current = model.getAddressBook();
        AddressBook cleared = new AddressBook();
        cleared.restoreTutorConnectData(List.of(), current.getNextStudentNumber(),
                current.getNextGuardianNumber());
        model.setAddressBook(cleared);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
