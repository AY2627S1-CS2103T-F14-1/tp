package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.guardian.GuardianId;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/** Adds a guardian contact to TutorConnect. */
public class AddGuardianCommand extends Command {
    public static final String COMMAND_WORD = "add-guardian";
    public static final String MESSAGE_DUPLICATE_GUARDIAN = "A guardian with this name already exists.";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a guardian contact. Parameters: n/NAME p/PHONE\n"
            + "Example: " + COMMAND_WORD + " n/Alex Tan p/91234567";

    private final Name name;
    private final Phone phone;

    /** Creates a command to add a guardian with the specified details. */
    public AddGuardianCommand(Name name, Phone phone) {
        this.name = requireNonNull(name);
        this.phone = requireNonNull(phone);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person candidate = new Person(name, phone, new Email("guardian@tutorconnect.com"),
                new Address("Not provided"), Set.of(new Tag("guardian")));
        if (model.getAddressBook().getPersonList().stream().anyMatch(candidate::isSimilarPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_GUARDIAN);
        }
        GuardianId guardianId = model.allocateGuardianId();
        Person guardian = new Person(name, phone, new Email("guardian@tutorconnect.com"),
                new Address("Not provided"), Set.of(new Tag("guardian"), new Tag(guardianId.toString())));
        model.addPerson(guardian);
        return new CommandResult("Added guardian " + guardianId + ": " + name);
    }
}
