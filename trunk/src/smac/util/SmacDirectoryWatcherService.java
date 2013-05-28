package smac.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import smac.Smac;
import smac.exception.DuplicateLockException;
import smac.model.InputQueue;
import smac.model.RoutingTask;

/**
 * The SmacDirectoryWatcherService class watches for changes that occur to a SMAC input queue directory and creates the
 * required SMAC daemon task based on the changes observed.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class SmacDirectoryWatcherService implements Runnable {

    // Class attributes
    private Path inputQueuePath;
    private final PriorityBlockingQueue<RoutingTask> processQueue;
    private InputQueue smacQueue;
    private WatchService watchService;

    /**
     * SmacDirectoryWatcherService constructor
     *
     * @param processQueue Priority queue being used to register SMAC daemon tasks
     * @param smacQueue SMAC daemon input queue for which the directory watcher service is being instantiated
     * @throws MalformedURLException
     * @throws IOException
     */
    public SmacDirectoryWatcherService(PriorityBlockingQueue<RoutingTask> processQueue, InputQueue smacQueue)
            throws MalformedURLException, IOException {

        // Initialise class attributes
        this.inputQueuePath = Paths.get(smacQueue.getQueueDirectory());
        this.processQueue = processQueue;
        this.smacQueue = smacQueue;
        this.watchService = FileSystems.getDefault().newWatchService();

        // Register the directory watch service for the SMAC queue
        this.inputQueuePath.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE);
    }

    /**
     * Start the concurrent monitoring of the SMAC daemon input queue directory
     */
    @Override @SuppressWarnings("unchecked")
    public void run() {

        // Poll for events on the SMAC input queue directory
        for (;;) {

            // Wait for key to be signaled
            WatchKey key;
            try {

                // Get a queued key or wait for one if none is available
                key = this.watchService.take();

            } catch (InterruptedException e) {

                // Log errors
                Smac.logController.log(Level.SEVERE, SmacDirectoryWatcherService.class.getSimpleName(), e.getMessage());

                return;
            }

            // Poll all events queued for the key
            for (WatchEvent<?> event: key.pollEvents()) {

                // Get the type of the event obtained
                WatchEvent.Kind<?> kind = event.kind();

                // An OVERFLOW event can occur even if this kind of event has not been registered with the service
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                // Get the name of the file
                WatchEvent<Path> watchEvent = (WatchEvent<Path>)event;
                Path filePath = watchEvent.context();

                // File object based on SMAC input queue directory and name of file which triggered the event
                File file = new File(this.inputQueuePath.toString() + System.getProperty("file.separator") +
                        filePath.toString());

                // Check if the file object actually refers to a file and that the file could be assumed to be a
                // SEPAmail message container file
                if (file.isFile() && file.getAbsolutePath().endsWith(".eml")) {

                    try {

                        // Create a SMAC daemon routing task instance for the file
                        RoutingTask smacRoutingTask = new RoutingTask(file, this.smacQueue.getQueueType());

                        // Add the task to the process queue
                        this.processQueue.offer(smacRoutingTask);

                        // Indicate that the new message was added to the process queue
                        Smac.logController.log(Level.INFO, SmacDirectoryWatcherService.class.getSimpleName(),
                                "The file " + smacRoutingTask.getEmlFile().getAbsolutePath() + " was added to the " +
                                SmacQueueTypeUtils.getQueueTypeName(this.smacQueue.getQueueType()) + " queue.");

                    } catch (IOException e) {

                        // Log errors
                        Smac.logController.log(Level.SEVERE, RoutingTask.class.getSimpleName(), e.getMessage());

                    } catch (DuplicateLockException e) {

                        // Log errors
                        Smac.logController.log(Level.WARNING, RoutingTask.class.getSimpleName(), e.getMessage());
                    }
                }
            }

            // Reset the key to receive further watch events
            boolean valid = key.reset();

            // Exit the directory watch service event processing loop since the key is no longer valid
            if (!valid) {
                break;
            }
        }
    }
}
