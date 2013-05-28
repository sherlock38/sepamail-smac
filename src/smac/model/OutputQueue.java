package smac.model;

import java.net.MalformedURLException;
import java.net.URL;
import smac.util.SmacQueueType;
import smac.util.SmacQueueTypeUtils;

/**
 * OutputQueue defines a queue that is being used as an output channel by the SMAC daemon.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class OutputQueue {

    // Class attributes
    private String queueDirectory;
    private SmacQueueType queueType;

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
     * OutputQueue constructor
     *
     * @param queueType SMAC daemon queue type
     */
    public OutputQueue(SmacQueueType queueType) {

        // Initialise class attributes
        this.queueDirectory = "";
        this.queueType = queueType;
    }

    /**
     * OutputQueue constructor
     *
     * @param queueType SMAC daemon queue type
     * @param queueDirectory File system directory path and name which will be defined as the SMAC daemon queue
     */
    public OutputQueue(SmacQueueType queueType, String queueDirectory) {

        // Initialise class attributes
        this.queueDirectory = queueDirectory;
        this.queueType = queueType;
    }

    /**
     * Compare the current output queue object instance to that of a given instance using the queue type
     *
     * @param anObject Given OutputQueue object instance
     * @return Whether the two objects are equal
     */
    @Override
    public boolean equals(Object anObject) {

        // Check if object is being compared to its own instance
        if (this == anObject) {
            return true;
        }

        // Check if we are comparing objects of the same type
        if (!(anObject instanceof OutputQueue)) {
            return false;
        }

        final OutputQueue outputQueue = (OutputQueue)anObject;

        // Compare the queue type of the output queue objects
        if (this.queueType == outputQueue.queueType) {
            return true;
        }

        return false;
    }

    /**
     * Generate a hash code for a SMAC output queue object based on its queue type
     *
     * @return Hash code for an output queue object
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.queueType != null ? this.queueType.hashCode() : 0);
        return hash;
    }

    /**
     * String representation of a SMAC output queue object
     *
     * @return String representation of a SMAC output queue
     */
    @Override
    public String toString() {
        return "Queue type name: " + SmacQueueTypeUtils.getQueueTypeName(this.queueType) + ", Directory: "
                + this.queueDirectory;
    }
}
