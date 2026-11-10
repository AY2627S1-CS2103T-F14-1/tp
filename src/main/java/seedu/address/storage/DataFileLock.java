package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

/**
 * An exclusive lock on a data file, held by a running TutorConnect so that a second TutorConnect
 * started on the same data file does not save over the first one's changes.
 *
 * <p>The lock is taken on a small companion file placed beside the data file (the data file's name with
 * {@code .lock} added), not on the data file itself, because the data file is rewritten on every save.
 * The operating system releases the lock when the process ends, even if it crashes, so a leftover
 * {@code .lock} file never blocks the next run.
 */
public class DataFileLock implements AutoCloseable {

    private final FileChannel channel;
    private final FileLock lock;

    private DataFileLock(FileChannel channel, FileLock lock) {
        this.channel = channel;
        this.lock = lock;
    }

    /**
     * Returns the path of the companion file that is locked for {@code dataFile}.
     */
    public static Path lockFileFor(Path dataFile) {
        requireNonNull(dataFile);
        return dataFile.resolveSibling(dataFile.getFileName() + ".lock");
    }

    /**
     * Tries to lock {@code dataFile} for this process, creating its folder and companion lock file if needed.
     *
     * @return the lock, or empty if another TutorConnect already holds it
     * @throws IOException if the lock file cannot be created or locked, for example in a write-protected folder
     */
    public static Optional<DataFileLock> tryAcquire(Path dataFile) throws IOException {
        Path lockFile = lockFileFor(dataFile);
        Path folder = lockFile.toAbsolutePath().getParent();
        if (folder != null) {
            Files.createDirectories(folder);
        }

        FileChannel channel = FileChannel.open(lockFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        try {
            FileLock lock = channel.tryLock();
            if (lock == null) {
                channel.close();
                return Optional.empty();
            }
            return Optional.of(new DataFileLock(channel, lock));
        } catch (OverlappingFileLockException e) {
            // already held by this process
            channel.close();
            return Optional.empty();
        } catch (IOException | RuntimeException e) {
            channel.close();
            throw e;
        }
    }

    /**
     * Releases the lock. Does nothing if it was already released.
     */
    @Override
    public void close() throws IOException {
        try {
            if (lock.isValid()) {
                lock.release();
            }
        } finally {
            channel.close();
        }
    }
}
