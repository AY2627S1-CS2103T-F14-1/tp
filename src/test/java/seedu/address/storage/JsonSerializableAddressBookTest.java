package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");
    private static final String TUTOR_CONNECT_PERSONS = """
            [{"name":"Alex Tan","phone":"91234567","email":"a@example.com","address":"One","tags":["S1"]},
            {"name":"Amelia Tan","phone":"92345678","email":"b@example.com","address":"Two","tags":["G1"]}]
            """;

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateContactId_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = readTutorConnectData("""
                [{"name":"Alex Tan","phone":"91234567","email":"a@example.com","address":"One","tags":["S1"]},
                {"name":"Bella Lim","phone":"92345678","email":"b@example.com","address":"Two","tags":["S1"]}]
                """, "[]", 2, 1);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_CONTACT_ID,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_danglingLink_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = readTutorConnectData("""
                [{"name":"Alex Tan","phone":"91234567","email":"a@example.com","address":"One","tags":["S1"]}]
                """, """
                [{"studentId":"S1","guardianId":"G1"}]
                """, 2, 1);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DANGLING_LINK,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateLink_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = readTutorConnectData(TUTOR_CONNECT_PERSONS,
                """
                [{"studentId":"S1","guardianId":"G1"},{"studentId":"S1","guardianId":"G1"}]
                """, 2, 2);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_LINK,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_nextIdReused_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = readTutorConnectData(TUTOR_CONNECT_PERSONS,
                """
                [{"studentId":"S1","guardianId":"G1"}]
                """, 1, 2);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_INVALID_NEXT_ID,
                dataFromFile::toModelType);
    }

    private JsonSerializableAddressBook readTutorConnectData(String persons, String links, int nextStudentNumber,
            int nextGuardianNumber) throws Exception {
        String json = "{\"persons\":" + persons + ",\"guardianLinks\":" + links
                + ",\"nextStudentNumber\":" + nextStudentNumber
                + ",\"nextGuardianNumber\":" + nextGuardianNumber + "}";
        return JsonUtil.fromJsonString(json, JsonSerializableAddressBook.class);
    }

}
