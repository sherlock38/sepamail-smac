package smac.exception;

/**
 * The InvalidQueueTypeNameException class is the exception thrown when the given queue type name could not be matched
 * to a SMAC queue type.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class InvalidQueueTypeNameException extends Exception {

    /**
     * InvalidQueueTypeNameException default constructor
     */
    public InvalidQueueTypeNameException() {

        // Initialise the parent class
        super("The given queue type name does not appear to be valid.");
    }

    /**
     * InvalidQueueTypeNameException constructor
     *
     * @param queueTypeName Queue type name
     */
    public InvalidQueueTypeNameException(String queueTypeName) {

        // Initialise the parent class
        super("The given queue type name, " + queueTypeName + ", does not appear to be valid.");
    }
}
