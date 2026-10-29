package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT;

import seedu.address.logic.commands.LinkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.guardian.GuardianId;
import seedu.address.model.student.StudentId;

/** Parses input arguments and creates a {@link LinkCommand}. */
public class LinkCommandParser implements Parser<LinkCommand> {
    @Override
    public LinkCommand parse(String args) throws ParseException {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_STUDENT, PREFIX_GUARDIAN);
        if (arguments.getPreamble().isEmpty() && arguments.getValue(PREFIX_STUDENT).isPresent()
                && arguments.getValue(PREFIX_GUARDIAN).isPresent()) {
            arguments.verifyNoDuplicatePrefixesFor(PREFIX_STUDENT, PREFIX_GUARDIAN);
            String studentValue = ParserUtil.normalizeContactId(arguments.getValue(PREFIX_STUDENT).get());
            String guardianValue = ParserUtil.normalizeContactId(arguments.getValue(PREFIX_GUARDIAN).get());
            if (StudentId.isValidStudentId(studentValue) && GuardianId.isValidGuardianId(guardianValue)) {
                return new LinkCommand(new StudentId(studentValue), new GuardianId(guardianValue));
            }
        }
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, LinkCommand.MESSAGE_USAGE));
    }
}
