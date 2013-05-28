package smac.exception;

/**
 * The InvalidOutputQueueConfigurationException class is the exception thrown when the given output queue configuration
 * could not be parsed.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class InvalidOutputQueueConfigurationException extends Exception {

    /**
     * InvalidOutputQueueConfigurationException default constructor
     */
    public InvalidOutputQueueConfigurationException() {

        // Initialise the parent class
        super("The given output queue configuration does not appear to be valid.");
    }

    /**
     * InvalidOutputQueueConfigurationException constructor
     *
     * @param ouputConfigurationKey Output queue configuration key
     */
    public InvalidOutputQueueConfigurationException(String ouputConfigurationKey) {

        // Initialise the parent class
        super("The given output queue configuration key " + ouputConfigurationKey + " does not appear to be valid.");
    }

    /**
     * InvalidOutputQueueConfigurationException constructor
     *
     * @param outputConfigurationKey Output queue configuration key
     * @param outputConfigurationValue Output queue configuration value
     */
    public InvalidOutputQueueConfigurationException(String outputConfigurationKey, String outputConfigurationValue) {

        // Initialise the parent class
        super("The given value '" + outputConfigurationValue + "' for the output queue configuration key " +
                outputConfigurationKey + " does not appear to be valid.");
    }
}
