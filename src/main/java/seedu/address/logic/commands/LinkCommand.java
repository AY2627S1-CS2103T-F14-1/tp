package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.guardian.GuardianId;
import seedu.address.model.link.GuardianLink;
import seedu.address.model.student.StudentId;

/** Links an existing guardian to an existing student. */
public class LinkCommand extends Command {
    public static final String COMMAND_WORD = "link";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Links a guardian to a student. Parameters: s/STUDENT_ID g/GUARDIAN_ID\n"
            + "Example: " + COMMAND_WORD + " s/S1 g/G1";

    private final StudentId studentId;
    private final GuardianId guardianId;

    /** Creates a command that links the specified IDs. */
    public LinkCommand(StudentId studentId, GuardianId guardianId) {
        this.studentId = requireNonNull(studentId);
        this.guardianId = requireNonNull(guardianId);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.hasContact(studentId.toString()) || !model.hasContact(guardianId.toString())) {
            throw new CommandException("Both the student and guardian IDs must exist.");
        }
        try {
            model.addGuardianLink(new GuardianLink(studentId, guardianId));
        } catch (IllegalArgumentException exception) {
            throw new CommandException(exception.getMessage());
        }
        return new CommandResult("Linked " + guardianId + " to " + studentId + ".");
    }
}
