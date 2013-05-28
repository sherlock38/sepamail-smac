package smac.exception;

/**
 * The DuplicateLockException class is the exception raised when a lock for a SMAC daemon task input file already exist.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class DuplicateLockException extends Exception {

    /**
     * DuplicateLockException constructor
     *
     * @param inputFile Absolute file name of the input file
     * @param lock Absolute file name of the lock
     */
    public DuplicateLockException(String inputFile, String lock) {

        // Initialise the parent class
        super("The file at " + inputFile + " has already been locked by " + lock + ".");
    }
}
