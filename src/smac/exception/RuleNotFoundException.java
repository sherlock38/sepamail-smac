package smac.exception;

/**
 * The RuleNotFoundException class is the exception raised when a rule for a SMAC daemon routing task could not be
 * found.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class RuleNotFoundException extends Exception {

    /**
     * RuleNotFoundException default constructor
     */
    public RuleNotFoundException() {

        // Initialise the parent class
        super("An appropriate routing rule could not be found.");
    }

    /**
     * RuleNotFoundException constructor
     *
     * @param file SEPAmail message container EML file
     * @param queueTypeName SMAC daemon input queue name
     */
    public RuleNotFoundException(String file, String queueTypeName) {

        // Initialise the parent class
        super("An appropriate routing rule for " + file + " in queue " + queueTypeName + " could not be found.");
    }
}
