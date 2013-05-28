package smac.exception;

/**
 * The DuplicateRuleException class is the exception raised when a rule with the same rule set and order has been
 * defined more than once in the SMAC daemon configuration file.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class DuplicateRuleException extends Exception {

    /**
     * DuplicateRuleException constructor
     *
     * @param key Duplicate rule key
     */
    public DuplicateRuleException(String key) {

        // Initialise the parent class
        super("The rule " + key + " has already been defined.");
    }
}
