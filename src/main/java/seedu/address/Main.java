package seedu.address;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import javafx.application.Application;
import seedu.address.commons.core.LogsCenter;

/**
 * The main entry point to the application.
 *
 * This is a workaround for the following error when MainApp is made the
 * entry point of the application:
 *
 *     Error: JavaFX runtime components are missing, and are required to run this application
 *
 * The reason is that MainApp extends Application. In that case, the
 * LauncherHelper will check for the javafx.graphics module to be present
 * as a named module. We don't use JavaFX via the module system so it can't
 * find the javafx.graphics module, and so the launch is aborted.
 *
 * By having a separate main class (Main) that doesn't extend Application
 * to be the entry point of the application, we avoid this issue.
 */
public class Main {
    private static Logger logger = LogsCenter.getLogger(Main.class);

    public static void main(String[] args) {

        useBundledMacNatives();
        logger.warning("The warnings about a 'restricted method in java.lang.System' "
            + "and 'enabling native access' appearing below (if any) can be ignored.");
        Application.launch(MainApp.class, args);
    }

    /**
     * On macOS, points JavaFX at the native libraries that match this machine's CPU.
     *
     * The jar bundles the Intel and Apple Silicon libraries in separate folders, because they share
     * file names and cannot both sit at the jar root. They are copied out to a folder in the user's
     * home directory, which is then added to java.library.path, where JavaFX looks for them.
     * Does nothing on other systems or when not running from a jar.
     */
    private static void useBundledMacNatives() {
        if (!System.getProperty("os.name").startsWith("Mac")) {
            return;
        }
        String arch = "aarch64".equals(System.getProperty("os.arch")) ? "mac-aarch64" : "mac";
        String prefix = "natives/" + arch + "/";
        try {
            Path jar = Path.of(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (!Files.isRegularFile(jar)) {
                return;
            }
            Path dir = Path.of(System.getProperty("user.home"), ".openjfx", "tutorconnect", arch);
            Files.createDirectories(dir);
            try (JarFile jarFile = new JarFile(jar.toFile())) {
                for (JarEntry entry : Collections.list(jarFile.entries())) {
                    if (entry.isDirectory() || !entry.getName().startsWith(prefix)) {
                        continue;
                    }
                    Path target = dir.resolve(Path.of(entry.getName()).getFileName());
                    if (Files.exists(target) && Files.size(target) == entry.getSize()) {
                        continue;
                    }
                    try (InputStream in = jarFile.getInputStream(entry)) {
                        Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
            String libraryPath = System.getProperty("java.library.path", "");
            System.setProperty("java.library.path", dir + File.pathSeparator + libraryPath);
        } catch (IOException | URISyntaxException e) {
            logger.warning("Could not unpack the bundled JavaFX libraries: " + e);
        }
    }
}
