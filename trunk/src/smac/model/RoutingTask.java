package smac.model;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import smac.Smac;
import smac.exception.DuplicateLockException;
import smac.util.SmacQueueType;
import smac.util.SmacQueueTypeUtils;

/**
 * RoutingTask defines the details of a task that must be carried out by the SMAC daemon.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 0.5
 */
public class RoutingTask implements Comparable<RoutingTask> {

    // Class attributes
    private File emlFile;
    private SmacQueueType inputQueueType;
    private boolean isRouted;
    private File lockFile;
    private SmacQueueType outputQueueType;

    /**
     * Get the SMAC daemon task input file
     *
     * @return SMAC daemon task input file
     */
    public File getEmlFile() {
        return this.emlFile;
    }

    /**
     * Get the SMAC daemon input queue type of the current SMAC daemon task
     *
     * @return SMAC daemon input queue type of the current SMAC daemon task
     */
    public SmacQueueType getInputQueueType() {
        return this.inputQueueType;
    }

    /**
     * Get the routed status of a SMAC daemon task - when a task is routed, it means that the task has been processed
     *
     * @return Routed status of a SMAC daemon task
     */
    public boolean getIsRouted() {
        return this.isRouted;
    }

    /**
     * Get the lock file of the SMAC daemon task
     *
     * @return Lock file of the SMAC daemon task
     */
    public File getLockFile() {
        return this.lockFile;
    }

    /**
     * Get the SMAC queue type to which the file associated with the current task must be sent
     *
     * @return SMAC queue type to which the file associated with the current task must be sent
     */
    public SmacQueueType getOutputQueueType() {
        return this.outputQueueType;
    }

    /**
     * Set the routed status of a SMAC daemon task - when a task is routed, it means that the task has been processed
     *
     * @param isRouted Routed status of a SMAC daemon task
     */
    public void setIsRouted(boolean isRouted) {
        this.isRouted = isRouted;
    }

    /**
     * Set the SMAC queue type to which the file associated with the current task must be sent
     *
     * @param outputQueueType SMAC queue type to which the file associated with the current task must be sent
     */
    public void setOutputQueueType(SmacQueueType outputQueueType) {
        this.outputQueueType = outputQueueType;
    }

    /**
     * RoutingTask constructor
     *
     * @param emlFile SMAC daemon task EML file
     * @param inputQueueType SMAC daemon input queue type of the current SMAC daemon task
     * @throws IOException
     * @throws DuplicateLockException
     */
    public RoutingTask(File emlFile, SmacQueueType inputQueueType) throws IOException, DuplicateLockException {

        // Initialise class attributes
        this.emlFile = emlFile;
        this.inputQueueType = inputQueueType;
        this.isRouted = false;
        this.outputQueueType = null;

        // Create lock file for the current SMAC daemon task
        this.createLockFile();
    }

    /**
     * Compare two instances of the RoutingTask to allow for the ordering of SMAC daemon routing tasks
     *
     * @param o Second RoutingTask class instance to which the current instance will be compared
     * @return Whether the current instance is less or greater than or equal to the current instance
     */
    @Override
    public int compareTo(RoutingTask o) {

        // Compare the date and time at which the file associated to the current task was created
        return (int)(this.emlFile.lastModified() - o.emlFile.lastModified());
    }

    /**
     * Generate a string representation of a SMAC routing task
     *
     * @return String representation of a SMAC routing task
     */
    @Override
    public String toString() {

        String lineSeparator = System.getProperty("line.separator");

        // SMAC routing task string representation
        String task = "";

        // SMAC daemon routing task details
        task += "Input queue: " + SmacQueueTypeUtils.getQueueTypeName(this.inputQueueType) + lineSeparator;
        task += "SEPAmail message container file: " + this.emlFile.getAbsolutePath() + lineSeparator;
        task += "Lock file: " + this.lockFile.getAbsolutePath() + lineSeparator;
        task += "Routed: " + (this.isRouted ? "Yes" : "No") + lineSeparator;

        // Check if the task has been routed
        if (this.isRouted) {
            task += "Output queue: " + SmacQueueTypeUtils.getQueueTypeName(this.outputQueueType) + lineSeparator;
        }

        return task;
    }

    /**
     * Create a lock file for the file associated to the current SMAC daemon routing task
     *
     * @throws IOException
     * @throws DuplicateLockException
     */
    private void createLockFile() throws IOException, DuplicateLockException {

        // Lock file
        this.lockFile = new File(this.emlFile.getAbsoluteFile() + ".lock");

        // Create the lock file
        if (this.lockFile.createNewFile()) {

            // Lock file has been created
            Smac.logController.log(Level.FINEST, RoutingTask.class.getSimpleName(), "The lock " +
                    this.lockFile.getAbsoluteFile() + " has been created.");

        } else {

            // Trying to lock a file that is already locked
            throw new DuplicateLockException(this.emlFile.getAbsolutePath(), this.lockFile.getAbsolutePath());
        }
    }
}
