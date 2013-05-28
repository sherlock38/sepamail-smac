package smac.exception;

/**
 * The InvalidInputQueueConfigurationException class is the exception thrown when the given queue configuration could
 * not be parsed.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class InvalidInputQueueConfigurationException extends Exception {

    /**
     * InvalidInputQueueConfigurationException default constructor
     */
    public InvalidInputQueueConfigurationException() {

        // Initialise the parent class
        super("The given queue configuration does not appear to be valid.");
    }

    /**
     * InvalidInputQueueConfigurationException constructor
     *
     * @param queueConfigurationKey Queue configuration key
     */
    public InvalidInputQueueConfigurationException(String queueConfigurationKey) {

        // Initialise the parent class
        super("The given queue configuration key " + queueConfigurationKey + " does not appear to be valid.");
    }

    /**
     * InvalidInputQueueConfigurationException constructor
     *
     * @param queueConfigurationKey Queue configuration key
     * @param queueConfigurationValue Queue configuration value
     */
    public InvalidInputQueueConfigurationException(String queueConfigurationKey, String queueConfigurationValue) {

        // Initialise the parent class
        super("The given value '" + queueConfigurationValue + "' for the queue configuration key " +
                queueConfigurationKey + " does not appear to be valid.");
    }
}
