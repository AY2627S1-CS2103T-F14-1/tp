package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddGuardianCommand;
import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteContactCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.LinkCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_legacyAdd_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () ->
                parser.parseCommand("add n/Alex Tan p/91234567 e/alex@example.com a/Home"));
    }

    @Test
    public void parseCommand_tutorConnectCommands() throws Exception {
        assertTrue(parser.parseCommand("add-student n/Alex Tan p/91234567") instanceof AddStudentCommand);
        assertTrue(parser.parseCommand("add-guardian n/Amelia Tan p/92345678") instanceof AddGuardianCommand);
        assertTrue(parser.parseCommand("link s/S1 g/G1") instanceof LinkCommand);
        assertTrue(parser.parseCommand("delete S1") instanceof DeleteContactCommand);
    }

    @Test
    public void parseCommand_invalidTutorConnectCommands_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parseCommand("add-student n/Alex Tan"));
        assertThrows(ParseException.class, () -> parser.parseCommand("add-guardian p/92345678"));
        assertThrows(ParseException.class, () -> parser.parseCommand("link s/G1 g/S1"));
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unknownPrefixAfterValue_throwsParseException() {
        String expected = String.format(Messages.MESSAGE_UNKNOWN_PARAMETER, "x/", AddStudentCommand.MESSAGE_USAGE);
        assertThrows(ParseException.class, expected, () ->
                parser.parseCommand("add-student n/Alex Tan p/91234567 x/foo"));
        assertThrows(ParseException.class, expected, () ->
                parser.parseCommand("add-student n/Alex Tan x/foo p/91234567"));
        assertThrows(ParseException.class, String.format(Messages.MESSAGE_UNKNOWN_PARAMETER, "x/",
                AddGuardianCommand.MESSAGE_USAGE), () ->
                parser.parseCommand("add-guardian n/Amelia Tan p/92345678 x/foo"));
        assertThrows(ParseException.class, String.format(Messages.MESSAGE_UNKNOWN_PARAMETER, "x/",
                EditCommand.MESSAGE_USAGE), () -> parser.parseCommand("edit S1 p/91234567 x/foo"));
    }

    @Test
    public void parseCommand_commandWordInUpperCase_success() throws Exception {
        assertTrue(parser.parseCommand("HELP") instanceof HelpCommand);
        assertTrue(parser.parseCommand("List") instanceof ListCommand);
        assertTrue(parser.parseCommand("EXIT") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
