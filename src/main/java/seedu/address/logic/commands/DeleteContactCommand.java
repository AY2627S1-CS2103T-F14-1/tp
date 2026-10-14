package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/** Deletes a student or guardian contact by its TutorConnect identifier. */
public class DeleteContactCommand extends Command {
    private final String contactId;

    /** Creates a command that deletes the specified contact. */
    public DeleteContactCommand(String contactId) {
        this.contactId = requireNonNull(contactId);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.deleteContact(contactId)) {
            throw new CommandException("No contact exists with ID " + contactId + ".");
        }
        return new CommandResult("Deleted contact " + contactId + " and its relationships.");
    }
}
