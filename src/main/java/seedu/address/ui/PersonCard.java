package seedu.address.ui;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.link.GuardianLink;
import seedu.address.model.person.ContactOrder;
import seedu.address.model.person.Person;
import seedu.address.model.student.StudentId;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label links;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code PersonCard} with the given {@code Person}, index and guardian links to display.
     * Placeholder values the user never entered and the ID tag (already shown before the name) are hidden.
     */
    public PersonCard(Person person, int displayedIndex, List<GuardianLink> guardianLinks) {
        super(FXML);
        this.person = person;
        Optional<String> contactId = ContactOrder.getContactId(person);
        id.setText(contactId.orElse(String.valueOf(displayedIndex)) + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        showUnlessPlaceholder(address, person.getAddress().value);
        showUnlessPlaceholder(email, person.getEmail().value);
        person.getTags().stream()
                .map(tag -> tag.tagName)
                .filter(tagName -> !contactId.equals(Optional.of(tagName)))
                .sorted()
                .forEach(tagName -> tags.getChildren().add(new Label(tagName)));
        contactId.ifPresentOrElse(contact -> links.setText(formatLinks(contact, guardianLinks)), () -> hide(links));
    }

    /** Returns e.g. {@code Guardians: G1, G2} for a student or {@code Students: none} for a guardian. */
    private static String formatLinks(String contactId, List<GuardianLink> guardianLinks) {
        boolean isStudent = StudentId.isValidStudentId(contactId);
        String linked = guardianLinks.stream()
                .filter(link -> (isStudent ? link.getStudentId() : link.getGuardianId()).toString().equals(contactId))
                .map(link -> (isStudent ? link.getGuardianId() : link.getStudentId()).toString())
                .sorted(ContactOrder.ID_COMPARATOR)
                .collect(Collectors.joining(", "));
        return (isStudent ? "Guardians: " : "Students: ") + (linked.isEmpty() ? "none" : linked);
    }

    private static void showUnlessPlaceholder(Label label, String value) {
        if (ContactOrder.isPlaceholder(value)) {
            hide(label);
        } else {
            label.setText(value);
        }
    }

    private static void hide(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }
}
