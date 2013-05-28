package smac.exception;

/**
 * The InvalidConfigurationFileException class is the exception raised when the configuration required for the SMAC
 * daemon does not appear to be valid.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class InvalidConfigurationFileException extends Exception {

    /**
     * InvalidConfigurationFileException default constructor
     */
    public InvalidConfigurationFileException() {

        // Initialise the parent class
        super("The SMAC configuration file does not appear to be valid.");
    }

    /**
     * InvalidConfigurationFileException constructor for undefined configuration key
     *
     * @param key Configuration key which has not been defined
     */
    public InvalidConfigurationFileException(String key) {

        // Initialise the parent class
        super("The SMAC configuration file does not contain the " + key + " property.");
    }

    /**
     * InvalidConfigurationFileException constructor for key with an invalid value
     *
     * @param key Configuration key
     * @param value Configuration key value
     */
    public InvalidConfigurationFileException(String key, String value) {

        // Initialise the parent class
        super("The value '" + value + "' assigned to the key " + key
                + " in the SMAC configuration file, is not valid.");
    }
}
