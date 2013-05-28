package smac.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import smac.exception.ConfigurationFileNotFoundException;
import smac.exception.DuplicateRuleException;
import smac.exception.InvalidConfigurationFileException;
import smac.exception.InvalidInputQueueConfigurationException;
import smac.exception.InvalidOutputQueueConfigurationException;
import smac.exception.InvalidQueueTypeNameException;
import smac.exception.InvalidRuleDefinitionException;
import smac.model.Config;
import smac.model.InputQueue;
import smac.model.OutputQueue;
import smac.model.Rule;

/**
 * The SmacConfigReader class reads and parses the SMAC module configuration file.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 0.1
 */
public class SmacConfigReader {

    private File configFile;
    private ArrayList<String> ignoreKeys;
    private ArrayList<InputQueue> inputQueues;
    private ArrayList<OutputQueue> outputQueues;
    private ArrayList<Rule> rules;

    /**
     * SmacConfigReader class constructor
     *
     * @param configFilename Path and name of the SMAC daemon configuration file
     * @throws ConfigurationFileNotFoundException
     */
    public SmacConfigReader(String configFilename) throws ConfigurationFileNotFoundException {

        // Initialise class attributes
        this.configFile = new File(configFilename);
        this.ignoreKeys = new ArrayList<>();
        this.inputQueues = new ArrayList<>();
        this.outputQueues = new ArrayList<>();
        this.rules = new ArrayList<>();

        // Set keys that will be ignored when reading the configuration for building the arrays of input and output
        // queues and rules
        this.ignoreKeys.add("log.level");
        this.ignoreKeys.add("log.out");

        // Check if the configuration file exists
        if (!this.configFile.exists()) {

            // File does not exist so we throw the appropriate exception
            throw new ConfigurationFileNotFoundException(configFilename);
        }
    }

    /**
     * Parse the SMAC daemon configuration file
     *
     * @return SMAC configuration object containing SMAC daemon configuration settings
     * @throws IOException
     */
    public Config parse() throws IOException, InvalidConfigurationFileException {

        // SMAC configuration properties
        Properties smacConfigurationProperties = new Properties();
        smacConfigurationProperties.load(new FileInputStream(this.configFile));

        // Check if the SMAC configuration properties file contains valid configuration
        if (smacConfigurationProperties.isEmpty()) {

            // SMAC configuration file appears to be empty
            throw new InvalidConfigurationFileException();

        } else {

            // Check if the SMAC log file name has been defined
            if (smacConfigurationProperties.containsKey("log.out")) {

                // Check the value assigned to the log filename
                if (smacConfigurationProperties.getProperty("log.out").trim().length() > 0) {

                    // Initialise the SMAC configuration object
                    Config config = new Config(smacConfigurationProperties.getProperty("log.out").trim(),
                            smacConfigurationProperties.getProperty("log.level", "ALL"));

                    // Generate SMAC input and output queues and rules objects from the configuration file
                    this.generateSmacConfig(smacConfigurationProperties);

                    // Sort the list of SMAC rules
                    Collections.sort(this.rules);

                    // Add the SMAC rules to the SMAC daemon configuration object
                    config.setRules(this.rules);

                    // Add the SMAC input queues to the SMAC daemon configuration object
                    config.setInputQueues(this.inputQueues);

                    // Add the SMAC output queues to the SMAC daemon configuration object
                    config.setOutputQueues(this.outputQueues);

                    return config;

                } else {

                    // The path and filename of the SMAC daemon log file is not valid
                    throw new InvalidConfigurationFileException("log.out",
                            smacConfigurationProperties.getProperty("log.out").trim());
                }

            } else {

                // SMAC configuration file does not contain the log file name and path property
                throw new InvalidConfigurationFileException("log.out");
            }
        }
    }

    /**
     * Add a SMAC rule to the list of rules
     *
     * @param rule SMAC rule that needs to be added to the list of SMAC rules
     * @throws DuplicateRuleException
     */
    private void addRule(Rule rule) throws DuplicateRuleException {

        // Check if the rule already exist in the list of rules
        if (this.rules.contains(rule)) {

            throw new DuplicateRuleException("rules" + rule.getRuleSet() + "." + rule.getOrder());

        } else {

            // Add the rule to the list of rules
            this.rules.add(rule);
        }
    }

    /**
     * Create instances for SMAC input queues, SMAC output queues and SMAC rules as per configured in the SMAC daemon
     * configuration file
     *
     * @param smacConfigurationProperties SMAC configuration properties
     */
    private void generateSmacConfig(Properties smacConfigurationProperties) {

        // Iterate through the list of property keys
        for (Iterator<String> it = smacConfigurationProperties.stringPropertyNames().iterator(); it.hasNext();) {

            // Get current key
            String currentKey = it.next();

            // Check if we do not need to ignore the current key
            if (!this.ignoreKeys.contains(currentKey)) {

                // Check the key type
                if (currentKey.startsWith("rules")) {

                    try {

                        // SMAC rule object for the current configuration file key
                        this.getRule(currentKey, smacConfigurationProperties.getProperty(currentKey));

                    } catch (InvalidRuleDefinitionException | InvalidQueueTypeNameException |
                            DuplicateRuleException e) {

                        // Display the error occured while creating the SMAC rule on terminal
                        System.out.println(e.getMessage());
                    }

                } else if (currentKey.startsWith("queue")) {

                    try {

                        // SMAC queue object for the current configuration file key
                        this.getInputQueue(currentKey, smacConfigurationProperties.getProperty(currentKey));

                    } catch (InvalidQueueTypeNameException | InvalidInputQueueConfigurationException e) {

                        // Display the error occured while creating the SMAC queue on terminal
                        System.out.println(e.getMessage());
                    }

                } else if (currentKey.startsWith("output")) {

                    try {

                        // SMAC output queue object for the current configuration file key
                        this.getOuputQueue(currentKey, smacConfigurationProperties.getProperty(currentKey));

                    } catch (InvalidQueueTypeNameException | InvalidOutputQueueConfigurationException e) {

                        // Display the error occured while creating the SMAC output queue on terminal
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Parse a queue definition configuration and build the list of queues for the SMAC daemon
     *
     * @param key SMAC queue key
     * @param value SMAC queue definition
     * @throws InvalidQueueTypeNameException
     * @throws InvalidInputQueueConfigurationException
     */
    private void getInputQueue(String key, String value) throws InvalidQueueTypeNameException,
            InvalidInputQueueConfigurationException {

        // Split the key
        String[] queueKeyDefs = key.split("\\.");

        // Check if the queue key is valid
        if (queueKeyDefs.length == 2 || queueKeyDefs.length == 3) {

            // Create instance of SMAC queue for the given queue type
            InputQueue inputQueue = new InputQueue(SmacQueueTypeUtils.getQueueType(queueKeyDefs[1]));

            // Check if we have a queue for the current queue type
            if (this.inputQueues.contains(inputQueue)) {

                // Get the already created instance of the the SMAC queue
                inputQueue = this.inputQueues.get(this.inputQueues.indexOf(inputQueue));

            } else {

                // Add the queue to the list of queues
                this.inputQueues.add(inputQueue);
            }

            // Check the number of parts in the queue key definition
            if (queueKeyDefs.length == 2) {

                // Definition of the active status of a queue
                if (value.toLowerCase().equals("active")) {
                    inputQueue.setIsActive(true);
                } else {
                    inputQueue.setIsActive(false);
                }

            } else {

                // Defition of the directory being used as the queue
                if (queueKeyDefs[2].equals("uri")) {

                    // Check if the value of the SMAC queue directory is valid
                    if (value.length() > 0 && value.startsWith("file://")) {

                        // Set the queue directory of the SMAC queue
                        inputQueue.setQueueDirectory(value);

                    } else {

                        // Invalid queue key definition
                        throw new InvalidInputQueueConfigurationException(key, value);
                    }

                } else {

                    // Invalid queue key definition
                    throw new InvalidInputQueueConfigurationException(key);
                }
            }

        } else {

            // Invalid queue key definition
            throw new InvalidInputQueueConfigurationException(key);
        }
    }

    /**
     * Parse an output definition configuration and build the list of output queues for the SMAC daemon
     *
     * @param key SMAC queue key
     * @param value SMAC queue definition
     * @throws InvalidQueueTypeNameException
     * @throws InvalidOutputQueueConfigurationException
     */
    private void getOuputQueue(String key, String value) throws InvalidQueueTypeNameException,
            InvalidOutputQueueConfigurationException {

        // Split the key
        String[] outputKeyDefs = key.split("\\.");

        // Check if the output key is valid
        if (outputKeyDefs.length == 2) {

            // Check the value of the output directory URL
            if (value.length() > 0 && value.startsWith("file://")) {

                // Create SMAC output object
                OutputQueue outputQueue = new OutputQueue(SmacQueueTypeUtils.getQueueType(outputKeyDefs[1]), value);

                // Add output object to list of SMAC output queues
                this.outputQueues.add(outputQueue);

            } else {

                // Invalid output key definition
                throw new InvalidOutputQueueConfigurationException(key, value);
            }

        } else {

            // Invalid output key definition
            throw new InvalidOutputQueueConfigurationException(key);
        }
    }

    /**
     * Parse a rule definition and create an instance of a SMAC rule based on the rule definition and add it to the list
     * of SMAC rules
     *
     * @param key SMAC rule key
     * @param value SMAC rule definition
     * @throws InvalidRuleDefinitionException
     * @throws InvalidQueueTypeNameException
     * @throws DuplicateRuleException
     */
    private void getRule(String key, String value) throws InvalidRuleDefinitionException, InvalidQueueTypeNameException,
            DuplicateRuleException {

        // Split the key to get the rule set and rule order
        String[] ruleSetAndOrder = key.split("\\.");

        // Check if two parts were obtained
        if (ruleSetAndOrder.length == 2) {

            try {

                // Get the rule set of the rule definition
                int ruleSet = Integer.parseInt(ruleSetAndOrder[0].replace("rules", ""), 10);

                // Get the order of the rule definition
                int order = Integer.parseInt(ruleSetAndOrder[1], 10);

                // Split the rule definition in parts
                String[] ruleParts = value.split(",");

                // Check the number of parts in the rule definition
                if (ruleParts.length == 2 || ruleParts.length == 4) {

                    // Check if we have a rule with input and output queues only
                    if (ruleParts.length == 2) {

                        // SMAC rule instance
                        Rule rule = new Rule(ruleSet, order,
                                SmacQueueTypeUtils.getQueueType(ruleParts[0].trim()),
                                SmacQueueTypeUtils.getQueueType(ruleParts[1].trim()));

                        // Add rule to list of rules
                        this.addRule(rule);

                    } else {

                        // SMAC rule instance for a rule with addresses and queues
                        Rule rule = new Rule(ruleSet, order, ruleParts[0].trim(), ruleParts[1].trim(),
                                SmacQueueTypeUtils.getQueueType(ruleParts[2].trim()),
                                SmacQueueTypeUtils.getQueueType(ruleParts[3].trim()));

                        // Add rule to list of rules
                        this.addRule(rule);
                    }

                } else {

                    // Invalid rule definition
                    throw new InvalidRuleDefinitionException("The definition " + value + " for the key " + key +
                            " does not appear to be valid.");
                }

            } catch (NumberFormatException e) {

                // Invalid rule definition
                throw new InvalidRuleDefinitionException(e.getMessage());
            }

        } else {

            // Invalid rule definition
            throw new InvalidRuleDefinitionException("The rule key " + key + " does not appear to be valid.");
        }
    }
}
