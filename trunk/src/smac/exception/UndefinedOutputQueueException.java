package smac.exception;

/**
 * The UndefinedOutputQueueException class is the exception thrown when an output queue has not been defined.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class UndefinedOutputQueueException extends Exception {

    /**
     * UndefinedOutputQueueException default constructor
     */
    public UndefinedOutputQueueException() {

        // Initialise the parent class
        super("The output queue has not been defined.");
    }

    /**
     * UndefinedOutputQueueException constructor
     *
     * @param queueTypeName Output queue type name
     */
    public UndefinedOutputQueueException(String queueTypeName) {

        // Initialise the parent class
        super("The output queue " + queueTypeName + " has not been defined.");
    }
}
