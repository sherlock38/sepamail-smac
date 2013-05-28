package smac.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import smac.Smac;
import smac.exception.DuplicateLockException;
import smac.exception.RuleNotFoundException;
import smac.exception.UndefinedOutputQueueException;
import smac.model.InputQueue;
import smac.model.RoutingTask;
import smac.util.SmacDirectoryUtils;
import smac.util.SmacDirectoryWatcherService;
import smac.util.SmacQueueTypeUtils;

/**
 * InputQueueController monitors a SMAC input queue directory and carries out message routing based on the predefined
 * rules whenever changes occur on the input queue.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 0.1
 */
public class InputQueueController implements Runnable {

    // Class attributes
    private File inputQueueDir;
    private OutputController outputController;
    private PriorityBlockingQueue<RoutingTask> processQueue;
    private Thread queueWatcherThread;
    private RoutingController routingController;
    private InputQueue smacQueue;

    /**
     * InputQueueController constructor
     *
     * @param smacQueue SMAC queue for which an instance of the input queue controller is being instantiated
     * @throws MalformedURLException
     */
    public InputQueueController(InputQueue smacQueue) throws MalformedURLException {

        // Initialise class attributes
        this.inputQueueDir = new File(smacQueue.getQueueDirectory());
        this.outputController = new OutputController();
        this.processQueue = new PriorityBlockingQueue<>();
        this.routingController  = new RoutingController();
        this.smacQueue = smacQueue;
    }

    /**
     * Start the concurrent monitoring of the SMAC daemon input queue
     */
    @Override
    public void run() {

        // Log the starting of a thread for monitoring the current SMAC daemon input queue
        Smac.logController.log(Level.INFO, InputQueueController.class.getSimpleName(),
                "Starting monitoring for SMAC queue " +
                SmacQueueTypeUtils.getQueueTypeName(this.smacQueue.getQueueType()) + ".");

        // Check if a SMAC input queue directory already exists and create the directory if required
        if (!this.createQueueDirectory()) {

            // Remove locks for files for which the routing transaction could not be completed
            this.clearLocks();

            // Add existing SEPAmail messages to the process queue
            this.queueBacklogs();
        }

        try {

            // Start the queue directory watcher service
            Runnable task = new SmacDirectoryWatcherService(this.processQueue, this.smacQueue);

            // Create a thread for the SMAC input queue directory watch service
            this.queueWatcherThread = new Thread(task);

            // Set the name of the queue watch service thread
            this.queueWatcherThread.setName(SmacQueueTypeUtils.getQueueTypeName(this.smacQueue.getQueueType()) +
                    " watch service thread");

            // Start the queue watch service thread
            this.queueWatcherThread.start();

        } catch (MalformedURLException e) {

            // Log errors
            Smac.logController.log(Level.SEVERE, SmacDirectoryWatcherService.class.getSimpleName(), e.getMessage());

        } catch (IOException e) {

            // Log errors
            Smac.logController.log(Level.SEVERE, SmacDirectoryWatcherService.class.getSimpleName(), e.getMessage());

        }

        // Process SMAC daemon routing tasks
        for (;;) {

            // Get task that needs to be processed or wait for a task if not available
            RoutingTask routingTask;
            try {

                routingTask = this.processQueue.take();

            } catch (InterruptedException e) {

                // Log errors
                Smac.logController.log(Level.SEVERE, InputQueueController.class.getSimpleName(), e.getMessage());

                return;
            }

            try {

                // Route the SEPAmail message
                this.routingController.route(routingTask);

                // Move the EML file associated with the task to its output directory
                this.outputController.move(routingTask);

                // SEPAmail message associated to the current task has been moved
                Smac.logController.log(Level.INFO, InputQueueController.class.getSimpleName(), "The file " +
                        routingTask.getEmlFile().getAbsolutePath() + " was sent to the " +
                        SmacQueueTypeUtils.getQueueTypeName(routingTask.getOutputQueueType()) + " queue.");

            } catch (RuleNotFoundException e) {

                // Log errors
                Smac.logController.log(Level.WARNING, RoutingController.class.getSimpleName(), e.getMessage());

            } catch (UndefinedOutputQueueException | MalformedURLException e) {

                // Log errors
                Smac.logController.log(Level.WARNING, OutputController.class.getSimpleName(), e.getMessage());

            } finally {

                // Remove the lock for the processed SEPAmail message container file
                if (routingTask.getLockFile().delete()) {

                    // Lock was successfully deleted
                    Smac.logController.log(Level.FINEST, InputQueueController.class.getSimpleName(), "The lock " +
                            routingTask.getLockFile().getAbsolutePath() + " for " +
                            routingTask.getEmlFile().getAbsolutePath() + " was removed.");

                } else {

                    // Lock could not be removed
                    Smac.logController.log(Level.WARNING, InputQueueController.class.getSimpleName(), "The lock " +
                            routingTask.getLockFile().getAbsolutePath() + " for " +
                            routingTask.getEmlFile().getAbsolutePath() + " could not be removed.");
                }
            }
        }
    }

    /**
     * Remove locks for any pending routing transactions in a SMAC input queue directory
     */
    private void clearLocks() {

        // Array of file locks
        String[] locks = this.inputQueueDir.list(new FilenameFilter() {

            /**
             * Filter for lock files in the SMAC input queue directory
             */
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".lock");
            }
        });

        // Remove file locks
        for (int i = 0; i < locks.length; i++) {

            // Delete the lock
            File lockFile = new File(this.inputQueueDir.getAbsolutePath() + System.getProperty("file.separator") +
                    locks[i]);

            // Delete the lock
            if (lockFile.delete()) {

                // The file lock has been deleted
                Smac.logController.log(Level.FINEST, InputQueueController.class.getSimpleName(),
                        "The lock " + lockFile.getAbsolutePath() + " has been removed.");

            } else {

                // The file lock could not be deleted
                Smac.logController.log(Level.WARNING, InputQueueController.class.getSimpleName(),
                        "The lock " + lockFile.getAbsolutePath() + " could not be removed.");
            }
        }
    }

    /**
     * Check if the directory of a SMAC input queue exists otherwise create the required directory
     *
     * @return Whether the directory already existed (false) or was newly created (true)
     */
    private boolean createQueueDirectory() {

        // Check if the directory exists
        if (this.inputQueueDir.exists() && this.inputQueueDir.isDirectory()) {

            // Log the path and name of the directory which is being used as the SMAC daemon input queue
            Smac.logController.log(Level.INFO, InputQueueController.class.getSimpleName(),
                    "The SMAC daemon " + SmacQueueTypeUtils.getQueueTypeName(this.smacQueue.getQueueType()) +
                    " input queue defined at the path " + this.inputQueueDir.getAbsolutePath() + " already exists.");

            // Input queue folder already exists
            return false;

        } else {

            // Create the SMAC daemon input queue directory
            SmacDirectoryUtils.createFolderIfNotExist(this.inputQueueDir.getAbsolutePath());

            // Indicate that the directory for the SMAC input queue has been created
            Smac.logController.log(Level.INFO, InputQueueController.class.getSimpleName(),
                    "The folder at " + this.inputQueueDir.getAbsolutePath() +
                    " has just been created for the SMAC daemon " +
                    SmacQueueTypeUtils.getQueueTypeName(this.smacQueue.getQueueType()) + " input queue.");

            // Input queue folder has just been created
            return true;
        }
    }

    /**
     * Add the list of existing SEPAmail message files to the SMAC daemon process queue
     */
    private void queueBacklogs() {

        // Array of SEPAmail message files
        String[] messageFiles = this.inputQueueDir.list(new FilenameFilter() {

            /**
             * Filter for SEPAmail message files in the SMAC input queue directory
             */
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".eml");
            }
        });

        // Check if we have existing message files
        if (messageFiles.length > 0) {

            // Indicate that the directory for the SMAC input contains pending messages
            if (messageFiles.length == 1) {

                Smac.logController.log(Level.INFO, InputQueueController.class.getSimpleName(),
                        "The " + SmacQueueTypeUtils.getQueueTypeName(this.smacQueue.getQueueType()) +
                        " contains 1 pending SEPAmail message which will be added to the process queue.");

            } else {

                Smac.logController.log(Level.INFO, InputQueueController.class.getSimpleName(),
                        "The " + SmacQueueTypeUtils.getQueueTypeName(this.smacQueue.getQueueType()) +
                        " contains " + messageFiles.length + " pending SEPAmail messages which will be added to the "
                        + "process queue.");
            }

            // Add pending messages to the process queue
            for (int i = 0; i < messageFiles.length; i++) {

                try {

                    // Instance of SMAC routing task for current SEPAmail message file
                    RoutingTask routingTask = new RoutingTask(new File(this.inputQueueDir.getAbsolutePath()
                            + System.getProperty("file.separator") + messageFiles[i]), this.smacQueue.getQueueType());

                    // Add the task to the process queue
                    this.processQueue.add(routingTask);

                    // Indicate that the pending message was added to the process queue
                    Smac.logController.log(Level.INFO, InputQueueController.class.getSimpleName(), "The file " +
                            routingTask.getEmlFile().getAbsolutePath() + " was added to the " +
                            SmacQueueTypeUtils.getQueueTypeName(this.smacQueue.getQueueType()) + " queue.");

                } catch (IOException e) {

                    // Log errors
                    Smac.logController.log(Level.SEVERE, RoutingTask.class.getSimpleName(), e.getMessage());

                } catch (DuplicateLockException e) {

                    // Log errors
                    Smac.logController.log(Level.WARNING, RoutingTask.class.getSimpleName(), e.getMessage());
                }
            }

        } else {

            // Indicate that the directory for the SMAC input does not contain pending messages
            Smac.logController.log(Level.INFO, InputQueueController.class.getSimpleName(),
                    "The " + SmacQueueTypeUtils.getQueueTypeName(this.smacQueue.getQueueType()) +
                    " does not contain any pending SEPAmail message.");
        }
    }
}
