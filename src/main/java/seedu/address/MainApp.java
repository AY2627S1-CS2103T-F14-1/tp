package seedu.address;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.storage.DataFileLock;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V1.6";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = Paths.get("preferences.json");
    private static final Path ADDRESS_BOOK_FILE_PATH = Paths.get("data", "tutorconnect.json");

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;

    private DataFileLock dataFileLock;
    private boolean isAnotherInstanceRunning;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing TutorConnect ]==========================");
        super.init();

        if (!lockDataFile(ADDRESS_BOOK_FILE_PATH)) {
            isAnotherInstanceRunning = true;
            logger.warning("Another TutorConnect is already using " + ADDRESS_BOOK_FILE_PATH + ". Exiting.");
            return;
        }

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(USER_PREFS_FILE_PATH);
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(ADDRESS_BOOK_FILE_PATH);
        storage = new StorageManager(addressBookStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic, storage.getAddressBookFilePath());
    }

    /**
     * Locks {@code dataFile} for this TutorConnect so that a second one started on the same file cannot save
     * over this one's changes.
     *
     * @return false if another TutorConnect already holds the lock. True if the lock was taken, or if it could not
     *         be taken because of a file error, in which case TutorConnect starts without the protection.
     */
    private boolean lockDataFile(Path dataFile) {
        try {
            Optional<DataFileLock> lock = DataFileLock.tryAcquire(dataFile);
            lock.ifPresent(acquired -> dataFileLock = acquired);
            return lock.isPresent();
        } catch (IOException e) {
            logger.warning("Could not lock the data file, so a second TutorConnect will not be detected: "
                    + StringUtil.getDetails(e));
            return true;
        }
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s address book and {@code userPrefs}. <br>
     * An empty TutorConnect workspace is used if {@code storage}'s data file is not found or cannot be read.
     */
    Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getAddressBookFilePath());

        Optional<ReadOnlyAddressBook> addressBookOptional;
        ReadOnlyAddressBook initialData;
        try {
            addressBookOptional = storage.readAddressBook();
            if (addressBookOptional.isEmpty()) {
                logger.info("Creating a new data file " + storage.getAddressBookFilePath()
                        + " with an empty TutorConnect workspace.");
            }
            initialData = getInitialData(addressBookOptional);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getAddressBookFilePath() + " could not be loaded."
                    + " Will be starting with an empty TutorConnect workspace.");
            backUpUnreadableDataFile(storage.getAddressBookFilePath());
            initialData = new AddressBook();
        }

        return new ModelManager(initialData, userPrefs);
    }

    /**
     * Copies a data file that could not be loaded to a file beside it with {@code .bak} added to its name
     * (or {@code .bak1}, {@code .bak2} and so on if that name holds different content), so that the next save
     * does not destroy the user's data. An existing backup with identical content is reused.
     *
     * @return the backup file, or empty if there is no data file to back up or the copy failed
     */
    static Optional<Path> backUpUnreadableDataFile(Path dataFile) {
        if (!Files.isRegularFile(dataFile)) {
            return Optional.empty();
        }
        try {
            Path backup = dataFile.resolveSibling(dataFile.getFileName() + ".bak");
            for (int attempt = 1; Files.exists(backup); attempt++) {
                if (Files.mismatch(backup, dataFile) == -1) {
                    return Optional.of(backup);
                }
                backup = dataFile.resolveSibling(dataFile.getFileName() + ".bak" + attempt);
            }
            Files.copy(dataFile, backup);
            logger.warning("Saved a copy of the unreadable data file as " + backup);
            return Optional.of(backup);
        } catch (IOException e) {
            logger.warning("Could not back up the unreadable data file " + dataFile + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /** Returns saved data when available, otherwise an empty TutorConnect workspace. */
    static ReadOnlyAddressBook getInitialData(Optional<ReadOnlyAddressBook> addressBookOptional) {
        return addressBookOptional.orElseGet(AddressBook::new);
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        if (isAnotherInstanceRunning) {
            showAlreadyRunningDialog();
            Platform.exit();
            return;
        }
        logger.info("Starting TutorConnect " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    private void showAlreadyRunningDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("TutorConnect is already running");
        alert.setHeaderText("TutorConnect is already open on this data file.");
        alert.setContentText("Two windows saving to the same file would overwrite each other's changes. "
                + "Use the window that is already open, or close it and start TutorConnect again.");
        alert.showAndWait();
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping TutorConnect ] =============================");
        if (model != null) {
            try {
                storage.saveUserPrefs(model.getUserPrefs());
            } catch (IOException e) {
                logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
            }
        }
        if (dataFileLock != null) {
            try {
                dataFileLock.close();
            } catch (IOException e) {
                logger.warning("Failed to release the data file lock " + StringUtil.getDetails(e));
            }
        }
    }
}
