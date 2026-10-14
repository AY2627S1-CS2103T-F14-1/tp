package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import seedu.address.logic.commands.AddGuardianCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

/** Parses input arguments and creates an {@link AddGuardianCommand}. */
public class AddGuardianCommandParser implements Parser<AddGuardianCommand> {
    @Override
    public AddGuardianCommand parse(String args) throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE);
        if (arguments.getPreamble().isEmpty() && arguments.getValue(PREFIX_NAME).isPresent()
                && arguments.getValue(PREFIX_PHONE).isPresent()) {
            arguments.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE);
            Name name = ParserUtil.parseName(arguments.getValue(PREFIX_NAME).get());
            Phone phone = ParserUtil.parsePhone(arguments.getValue(PREFIX_PHONE).get());
            return new AddGuardianCommand(name, phone);
        }
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddGuardianCommand.MESSAGE_USAGE));
    }
}
