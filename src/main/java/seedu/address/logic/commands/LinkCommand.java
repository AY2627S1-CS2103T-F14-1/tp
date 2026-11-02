package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.guardian.GuardianId;
import seedu.address.model.link.GuardianLink;
import seedu.address.model.student.StudentId;

/**
 * Links an existing guardian to an existing student.
 * Like the other commands that change a contact, it clears any filter left by a previous {@code find}.
 */
public class LinkCommand extends Command {
    public static final String COMMAND_WORD = "link";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Links a guardian to a student. Parameters: s/STUDENT_ID g/GUARDIAN_ID\n"
            + "Example: " + COMMAND_WORD + " s/S1 g/G1";
    public static final String MESSAGE_WRONG_ID_TYPES =
            "s/ takes a student ID (e.g. S1) and g/ takes a guardian ID (e.g. G1).";
    public static final String MESSAGE_MISSING_CONTACT = "No contact exists with ID %1$s.";

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
        for (String contactId : new String[] {studentId.toString(), guardianId.toString()}) {
            if (!model.hasContact(contactId)) {
                throw new CommandException(String.format(MESSAGE_MISSING_CONTACT, contactId));
            }
        }
        try {
            model.addGuardianLink(new GuardianLink(studentId, guardianId));
        } catch (IllegalArgumentException exception) {
            throw new CommandException(exception.getMessage());
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult("Linked " + guardianId + " to " + studentId + ".");
    }
}
