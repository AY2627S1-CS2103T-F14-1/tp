package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.LinkCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class LinkCommandParserTest {

    private final LinkCommandParser parser = new LinkCommandParser();

    @Test
    public void parse_swappedIds_explainsWhichPrefixTakesWhichId() {
        ParseException exception = assertThrows(ParseException.class, () -> parser.parse(" s/G1 g/S1"));
        assertEquals(LinkCommand.MESSAGE_WRONG_ID_TYPES + "\n" + LinkCommand.MESSAGE_USAGE, exception.getMessage());
    }

    @Test
    public void parse_missingPrefix_showsUsage() {
        ParseException exception = assertThrows(ParseException.class, () -> parser.parse(" s/S1"));
        assertEquals(String.format(MESSAGE_INVALID_COMMAND_FORMAT, LinkCommand.MESSAGE_USAGE), exception.getMessage());
    }
}
