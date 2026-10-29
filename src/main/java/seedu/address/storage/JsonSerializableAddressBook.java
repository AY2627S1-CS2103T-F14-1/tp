package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.guardian.GuardianId;
import seedu.address.model.link.GuardianLink;
import seedu.address.model.person.Person;
import seedu.address.model.student.StudentId;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_CONTACT_ID = "Persons list contains duplicate contact ID(s).";
    public static final String MESSAGE_INVALID_CONTACT_ID = "A person has more than one TutorConnect contact ID.";
    public static final String MESSAGE_ROLE_MISMATCH =
            "A person's student or guardian tag does not match its TutorConnect contact ID.";
    public static final String MESSAGE_DUPLICATE_LINK = "Guardian links contain duplicate link(s).";
    public static final String MESSAGE_DANGLING_LINK = "Guardian link refers to a missing contact.";
    public static final String MESSAGE_INVALID_NEXT_ID = "The next contact ID must be greater than all saved IDs.";

    private static final String STUDENT_ROLE_TAG = "student";
    private static final String GUARDIAN_ROLE_TAG = "guardian";

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
        validateTutorConnectData(addressBook.getPersonList(), links);
        assert links.size() == guardianLinks.size() : "Every saved guardian link should be restored";
        addressBook.restoreTutorConnectData(links, nextStudentNumber, nextGuardianNumber);
        return addressBook;
    }

    /**
     * Validates saved TutorConnect IDs, relationships, and allocation counters before loading them.
     */
    private void validateTutorConnectData(List<Person> persons, List<GuardianLink> links)
            throws IllegalValueException {
        Set<String> studentIds = new HashSet<>();
        Set<String> guardianIds = new HashSet<>();
        for (Person person : persons) {
            List<String> contactIds = person.getTags().stream()
                    .map(tag -> tag.tagName)
                    .filter(tagName -> StudentId.isValidStudentId(tagName)
                            || GuardianId.isValidGuardianId(tagName))
                    .toList();
            if (contactIds.size() > 1) {
                throw new IllegalValueException(MESSAGE_INVALID_CONTACT_ID);
            }
            if (contactIds.isEmpty()) {
                continue;
            }
            String contactId = contactIds.get(0);
            String conflictingRoleTag = StudentId.isValidStudentId(contactId) ? GUARDIAN_ROLE_TAG : STUDENT_ROLE_TAG;
            if (person.getTags().stream().anyMatch(tag -> tag.tagName.equals(conflictingRoleTag))) {
                throw new IllegalValueException(MESSAGE_ROLE_MISMATCH);
            }
            Set<String> contactIdsForRole = StudentId.isValidStudentId(contactId) ? studentIds : guardianIds;
            if (!contactIdsForRole.add(contactId)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_CONTACT_ID);
            }
        }
        if (new HashSet<>(links).size() != links.size()) {
            throw new IllegalValueException(MESSAGE_DUPLICATE_LINK);
        }
        if (links.stream().anyMatch(link -> !studentIds.contains(link.getStudentId().toString())
                || !guardianIds.contains(link.getGuardianId().toString()))) {
            throw new IllegalValueException(MESSAGE_DANGLING_LINK);
        }
        if (!isNextIdValid(nextStudentNumber, studentIds) || !isNextIdValid(nextGuardianNumber, guardianIds)) {
            throw new IllegalValueException(MESSAGE_INVALID_NEXT_ID);
        }
    }

    /** Returns whether {@code nextNumber} is usable and greater than every ID number in {@code contactIds}. */
    private boolean isNextIdValid(int nextNumber, Set<String> contactIds) {
        if (nextNumber < 1) {
            return false;
        }
        assert contactIds.stream().allMatch(contactId -> contactId.length() > 1)
                : "Contact IDs should have already been validated as a prefix followed by a number";
        String nextNumberText = String.valueOf(nextNumber);
        return contactIds.stream()
                .noneMatch(contactId -> compareIdNumbers(contactId.substring(1), nextNumberText) >= 0);
    }

    /** Compares two positive ID-number strings without overflowing an {@code int}. */
    private int compareIdNumbers(String first, String second) {
        int lengthComparison = Integer.compare(first.length(), second.length());
        return lengthComparison != 0 ? lengthComparison : first.compareTo(second);
    }

}
