package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.link.GuardianLink;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedGuardianLink> guardianLinks = new ArrayList<>();
    private final int nextStudentNumber;
    private final int nextGuardianNumber;

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("guardianLinks") List<JsonAdaptedGuardianLink> guardianLinks,
            @JsonProperty("nextStudentNumber") Integer nextStudentNumber,
            @JsonProperty("nextGuardianNumber") Integer nextGuardianNumber) {
        this.persons.addAll(persons);
        if (guardianLinks != null) {
            this.guardianLinks.addAll(guardianLinks);
        }
        this.nextStudentNumber = nextStudentNumber == null ? 1 : nextStudentNumber;
        this.nextGuardianNumber = nextGuardianNumber == null ? 1 : nextGuardianNumber;
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        guardianLinks.addAll(source.getGuardianLinks().stream()
                .map(JsonAdaptedGuardianLink::new).collect(Collectors.toList()));
        nextStudentNumber = source.getNextStudentNumber();
        nextGuardianNumber = source.getNextGuardianNumber();
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }
        List<GuardianLink> links = new ArrayList<>();
        for (JsonAdaptedGuardianLink jsonLink : guardianLinks) {
            links.add(jsonLink.toModelType());
        }
        addressBook.restoreTutorConnectData(links, nextStudentNumber, nextGuardianNumber);
        return addressBook;
    }

}
