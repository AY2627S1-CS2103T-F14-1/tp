package seedu.address.logic.parser;

import seedu.address.logic.commands.ClearCommand;

/** Parses input arguments and creates a {@link ClearCommand}. */
public class ClearCommandParser implements Parser<ClearCommand> {
    /**
     * Returns a {@code ClearCommand} that clears the data only if {@code args} is the confirmation keyword.
     * Any other arguments are ignored and leave the data untouched, so that a stray {@code clear} is safe.
     */
    @Override
    public ClearCommand parse(String args) {
        boolean isConfirmed = args.trim().equalsIgnoreCase(ClearCommand.CONFIRMATION_KEYWORD);
        return new ClearCommand(isConfirmed);
    }
}
