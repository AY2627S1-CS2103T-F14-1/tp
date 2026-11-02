package seedu.address.ui;

import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.link.GuardianLink;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    @FXML
    private ListView<Person> personListView;

    private final Supplier<List<GuardianLink>> guardianLinks;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList},
     * reading guardian links from {@code guardianLinks} whenever a card is drawn.
     */
    public PersonListPanel(ObservableList<Person> personList, Supplier<List<GuardianLink>> guardianLinks) {
        super(FXML);
        this.guardianLinks = guardianLinks;
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
    }

    /** Redraws every card, e.g. after a command changes guardian links but not the contacts themselves. */
    public void refresh() {
        personListView.refresh();
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1, guardianLinks.get()).getRoot());
            }
        }
    }

}
