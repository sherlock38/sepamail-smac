package smac.model;

import java.net.MalformedURLException;
import java.net.URL;
import smac.util.SmacQueueType;
import smac.util.SmacQueueTypeUtils;

/**
 * InputQueue defines a queue that is being monitored by the SMAC daemon.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class InputQueue {

    // Class attributes
    private boolean isActive;
    private String queueDirectory;
    private SmacQueueType queueType;

    /**
     * Get the active status of the SMAC queue
     *
     * @return Active status of the SMAC queue
     */
    public boolean getIsActive() {
        return this.isActive;
    }

    /**
     * Get the file system directory which has been defined as the queue
     *
     * @return File system directory which has been defined as the queue
     * @throws MalformedURLException
     */
    public String getQueueDirectory() throws MalformedURLException {

        // Convert queue directory URL to directory
        URL url = new URL(this.queueDirectory);

        return url.getFile();
    }

    /**
     * Get the SMAC daemon queue type
     *
     * @return SMAC daemon queue type
     */
    public SmacQueueType getQueueType() {
        return this.queueType;
    }

    /**
     * Set the active status of the SMAC queue
     *
     * @param isActive Active status of the SMAC queue
     */
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Set the file system directory which will be defined as the SMAC daemon queue
     *
     * @param queueDirectory File system directory path and name
     */
    public void setQueueDirectory(String queueDirectory) {
        this.queueDirectory = queueDirectory;
    }

    /**
     * Set the SMAC daemon queue type
     *
     * @param queueType SMAC daemon queue type
     */
    public void setQueueType(SmacQueueType queueType) {
        this.queueType = queueType;
    }

    /**
     * InputQueue constructor
     *
     * @param queueType SMAC daemon queue type
     */
    public InputQueue(SmacQueueType queueType) {

        // Initialise class attributes
        this.isActive = false;
        this.queueDirectory = "";
        this.queueType = queueType;
    }

    /**
     * InputQueue constructor
     *
     * @param queueDirectory File system directory path and name which will be defined as the SMAC daemon queue
     * @param queueType SMAC daemon queue type
     */
    public InputQueue(String queueDirectory, SmacQueueType queueType) {

        // Initialise class attributes
        this.isActive = false;
        this.queueDirectory = queueDirectory;
        this.queueType = queueType;
    }

    /**
     * Compare the current queue object instance to that of a given instance using the queue type
     *
     * @param anObject Given InputQueue object instance
     * @return Whether the two objects are equal
     */
    @Override
    public boolean equals(Object anObject) {

        // Check if object is being compared to its own instance
        if (this == anObject) {
            return true;
        }

        // Check if we are comparing objects of the same type
        if (!(anObject instanceof InputQueue)) {
            return false;
        }

        final InputQueue smacQueue = (InputQueue)anObject;

        // Compare the queue types of the SMAC queue objects
        if (this.queueType == smacQueue.queueType) {
            return true;
        }

        return false;
    }
    /**
     * Generate a hash code for a SMAC queue object based on its queue type
     *
     * @return Hash code for a InputQueue object
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (this.queueType != null ? SmacQueueTypeUtils.getQueueTypeName(this.queueType).hashCode() :
                0);
        return hash;
    }

    /**
     * String representation of a SMAC queue object
     *
     * @return String representation of a SMAC queue
     */
    @Override
    public String toString() {
        return "Queue type name: " + SmacQueueTypeUtils.getQueueTypeName(this.queueType) + ", Directory: "
                + this.queueDirectory + " [" + (this.isActive ? "Active" : "Not active") + "]";
    }
}
