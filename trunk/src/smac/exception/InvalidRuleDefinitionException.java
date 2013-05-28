package smac.exception;

/**
 * The InvalidRuleDefinitionException class is the exception raised when a rule has not been properly defined in the
 * SMAC daemon configuration file.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class InvalidRuleDefinitionException extends Exception {

    /**
     * InvalidRuleDefinitionException constructor
     *
     * @param message Error message
     */
    public InvalidRuleDefinitionException(String message) {

        // Initialise the parent class
        super(message);
    }
}
