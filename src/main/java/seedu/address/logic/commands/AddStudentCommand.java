package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.student.StudentId;
import seedu.address.model.tag.Tag;

/** Adds a student contact to TutorConnect. */
public class AddStudentCommand extends Command {
    public static final String COMMAND_WORD = "add-student";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a student contact. Parameters: n/NAME p/PHONE\n"
            + "Example: " + COMMAND_WORD + " n/Alex Tan p/91234567";

    private final Name name;
    private final Phone phone;

    /** Creates a command to add a student with the specified details. */
    public AddStudentCommand(Name name, Phone phone) {
        this.name = requireNonNull(name);
        this.phone = requireNonNull(phone);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person candidate = new Person(name, phone, new Email("student@tutorconnect.com"),
                new Address("Not provided"), Set.of());
        if (model.hasPerson(candidate)) {
            throw new CommandException("A contact with this name already exists.");
        }
        StudentId studentId = model.allocateStudentId();
        Person student = new Person(name, phone, new Email("student@tutorconnect.com"),
                new Address("Not provided"), Set.of(new Tag("student"), new Tag(studentId.toString())));
        model.addPerson(student);
        return new CommandResult("Added student " + studentId + ": " + name);
    }
}
