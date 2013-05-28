package smac.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import smac.Smac;
import smac.util.SmacLogFormatter;

/**
 * LogController allows the application to log all error messages to a log in Syslog format.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class LogController {

    private static final Logger LOGGER = Logger.getLogger(Smac.class.getName());
    private static LogController logController;

    /**
     * LogController default constructor
     *
     * @throws IOException
     * @throws MalformedURLException
     */
    private LogController() throws IOException, MalformedURLException {

        // File formatter
        SmacLogFormatter fileFormatter = new SmacLogFormatter();

        // Logger file handler
        FileHandler logFileHandler = new FileHandler(Smac.config.getLogFilename(), true);

        // File handler properties
        logFileHandler.setFormatter(fileFormatter);

        // Create logger
        Logger logger = Logger.getLogger(Smac.class.getName());

        // Set the properties of the logger
        logger.setLevel(Smac.config.getLogLevel());
        logger.addHandler(logFileHandler);
    }

    /**
     * Get an instance of the LogController class
     *
     * @return LogController class instance
     * @throws IOException
     */
    public static synchronized LogController getLogController() throws IOException {

        // Check if an instance of the controller has already been declared
        if (logController == null) {
            logController = new LogController();
        }

        return logController;
    }

    /**
     * Write application messages to the application log file
     *
     * @param level Log level
     * @param source Log message source class
     * @param message Message that needs to be written to the application log file
     */
    public synchronized void log(Level level, String source, String message) {

        // Write message to log file
        LOGGER.logp(level, source, "", message);
    }

    /**
     * Override the clone method to prevent cloning of the class
     *
     * @return void
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
