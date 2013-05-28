package smac.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Config defines the configuration settings of the SMAC daemon.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 0.1
 */
public class Config {

    // Class attributes
    private String logFilename;
    private String logLevel;
    private ArrayList<InputQueue> inputQueues;
    private ArrayList<OutputQueue> outputQueues;
    private ArrayList<Rule> rules;

    /**
     * Get the name and path of the SMAC daemon log file
     *
     * @return Name and path of the SMAC daemon log file
     * @throws MalformedURLException
     */
    public String getLogFilename() throws MalformedURLException {

        // Convert log file URL to file
        URL url = new URL(this.logFilename);

        return url.getFile();
    }

    /**
     * Get the list of input queues configured for the SMAC daemon
     *
     * @return List of input queues configured for the SMAC daemon
     */
    public ArrayList<InputQueue> getInputQueues() {
        return this.inputQueues;
    }

    /**
     * Get the SMAC daemon log level
     *
     * @return SMAC daemon log level
     */
    public Level getLogLevel() {

        // Check if the log level has properly been defined
        if (this.logLevel.trim().length() > 0) {
            return Level.parse(this.logLevel);
        } else {
            return Level.ALL;
        }
    }

    /**
     * Get the list of output queues configured for the SMAC daemon
     *
     * @return List of output queues configured for the SMAC daemon
     */
    public ArrayList<OutputQueue> getOutputQueues() {
        return this.outputQueues;
    }

    /**
     * Get the rule set for the SMAC daemon message routing
     *
     * @return Rule set for the SMAC daemon message routing
     */
    public ArrayList<Rule> getRules() {
        return this.rules;
    }

    /**
     * Set the list of input queues configured for the SMAC daemon
     *
     * @param inputQueues List of input queues configured for the SMAC daemon
     */
    public void setInputQueues(ArrayList<InputQueue> inputQueues) {
        this.inputQueues = inputQueues;
    }

    /**
     * Set the list of output queues configured for the SMAC daemon
     *
     * @param outputQueues List of output queues configured for the SMAC daemon
     */
    public void setOutputQueues(ArrayList<OutputQueue> outputQueues) {
        this.outputQueues = outputQueues;
    }

    /**
     * Set the rule set for the SMAC daemon message routing
     *
     * @param rules Rule set for the SMAC daemon message routing
     */
    public void setRules(ArrayList<Rule> rules) {
        this.rules = rules;
    }

    /**
     * Config constructor
     *
     * @param logFilename Name and path of the SMAC daemon log file
     * @param logLevel SMAC log level
     */
    public Config(String logFilename, String logLevel) {

        // Initialise class attributes
        this.inputQueues = new ArrayList<>();
        this.logFilename = logFilename;
        this.logLevel = logLevel;
        this.outputQueues = new ArrayList<>();
        this.rules = new ArrayList<>();
    }

    /**
     * Add an input queue to the list of queues being observed by SMAC
     *
     * @param queue SMAC input queue queue object
     */
    public void addInputQueue(InputQueue queue) {
        this.inputQueues.add(queue);
    }

    /**
     * Add an output queue to the list of output queues being used SMAC
     *
     * @param output SMAC output queue object
     */
    public void addOutputQueue(OutputQueue output) {
        this.outputQueues.add(output);
    }

    /**
     * Add a SMAC rule to the list of the SMAC daemon routing rules
     *
     * @param rule SMAC daemon routing rule
     */
    public void addRule(Rule rule) {
        this.rules.add(rule);
    }

    /**
     * String representation of SMAC daemon configuration object
     *
     * @return String representation of SMAC daemon configuration object
     */
    @Override
    public String toString() {

        String lineSeparator = System.getProperty("line.separator");

        // Configuration object string representation
        String strConfig = "";

        // Build string representation of configuration class
        strConfig += "Log level: " + this.logLevel + lineSeparator;
        strConfig += "Log filename: " + this.logFilename + lineSeparator + lineSeparator;

        // Add rules
        strConfig += "Rules:" + lineSeparator;
        for (int i = 0; i < this.rules.size(); i++) {
            strConfig += this.rules.get(i) + lineSeparator;
        }

        // Add input queues
        strConfig += lineSeparator + "Input queues:" + lineSeparator;
        for (int i = 0; i < this.inputQueues.size(); i++) {
            strConfig += this.inputQueues.get(i) + lineSeparator;
        }

        // Add output queues
        strConfig += lineSeparator + "Output queues:" + lineSeparator;
        for (int i = 0; i < this.outputQueues.size(); i++) {
            strConfig += this.outputQueues.get(i) + lineSeparator;
        }

        return strConfig;
    }
}
