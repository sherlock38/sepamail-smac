package smac.controller;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import smac.Smac;
import smac.exception.RuleNotFoundException;
import smac.model.OutputQueue;
import smac.model.RoutingTask;
import smac.model.Rule;
import smac.util.SmacEmlUtils;
import smac.util.SmacQueueType;
import smac.util.SmacQueueTypeUtils;

/**
 * The RoutingController class routes a SEPAmail message to its appropriate output queue based on the set of rules
 * defined in the SMAC daemon configuration file.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 0.5
 */
public class RoutingController {

    // Class attributes
    private final ArrayList<OutputQueue> outputQueues;
    private final ArrayList<Rule> rules;

    /**
     * RoutingController default constructor
     */
    public RoutingController() {

        // Initialise class attributes
        this.outputQueues = Smac.config.getOutputQueues();
        this.rules = Smac.config.getRules();
    }

    /**
     * Determine the output queue of a SEPAmail message
     *
     * @param routingTask SMAC daemon routing task which points to a related SEPAmail message
     * @throws RuleNotFoundException
     */
    public void route(RoutingTask routingTask) throws RuleNotFoundException {

        try {

            // Log SMAC routing task processing
            Smac.logController.log(Level.FINEST, RoutingController.class.getSimpleName(), "Processing " +
                    routingTask.getEmlFile().getAbsolutePath() + " in queue " +
                    SmacQueueTypeUtils.getQueueTypeName(routingTask.getInputQueueType()) + ".");

            // Read and parse the EML file associated with the SMAC routing task
            MimeMessage emlMessage = SmacEmlUtils.loadEml(routingTask.getEmlFile());

            // Check if we have the 'sepamail-mode' key in the SEPAmail message container file
            String[] sepamailHeader = emlMessage.getHeader("sepamail-mode");
            if (sepamailHeader != null) {
                if (sepamailHeader.length > 0) {

                    // Route messages based on its sepamail-mode key
                    switch (sepamailHeader[0]) {

                        case "canonical":

                            // Preliminary routing for canonical mode
                            this.doPreliminaryRouting(routingTask, "canonical");

                            break;

                        case "flash":

                            // Preliminary routing for flash mode
                            this.doPreliminaryRouting(routingTask, "flash");

                            break;

                        default:

                    }
                }
            }

            // Check if task has been routed with preliminary routing
            if (routingTask.getIsRouted()) {

                // Log the rule with which the current SEPAmail message will be routed
                Smac.logController.log(Level.FINEST, RoutingController.class.getSimpleName(), "Routing " +
                        routingTask.getEmlFile().getAbsolutePath() + " in queue " +
                        SmacQueueTypeUtils.getQueueTypeName(routingTask.getInputQueueType()) +
                        " with 'sepamail-mode' code to " +
                        SmacQueueTypeUtils.getQueueTypeName(routingTask.getOutputQueueType()) + ".");

                return;
            }

            // Get the 'from' address of the EML message
            String from = null;
            if (emlMessage.getFrom().length > 0) {
                from = emlMessage.getFrom()[0].toString().trim();
            }

            // Get the 'to' address of the EML message
            String to = null;
            if (emlMessage.getRecipients(Message.RecipientType.TO).length > 0) {
                to = emlMessage.getRecipients(Message.RecipientType.TO)[0].toString().trim();
            }

            // Check if the from and to addresses have been defined
            if (from != null && to != null) {

                // Try to obtain at least a rule using the 4-uplets
                ArrayList<Rule> matchingRules = this.getMatchingRules(from, to, routingTask.getInputQueueType());

                // Check if we have matching rules
                if (matchingRules.size() > 0) {

                    // Route the SEPAmail message container file with the first matching rule
                    routingTask.setOutputQueueType(matchingRules.get(0).getOutputQueueType());
                    routingTask.setIsRouted(true);

                    // Log the rule with which the current SEPAmail message will be routed
                    Smac.logController.log(Level.FINEST, RoutingController.class.getSimpleName(), "Routing " +
                            routingTask.getEmlFile().getAbsolutePath() + " in queue " +
                            SmacQueueTypeUtils.getQueueTypeName(routingTask.getInputQueueType()) + " with rule " +
                            matchingRules.get(0) + ".");

                    // Log warning if we have more than one matching 4-uplet rule
                    if (matchingRules.size() > 1) {

                        Smac.logController.log(Level.WARNING, RoutingController.class.getSimpleName(),
                                "There are more than one rule similar to " + matchingRules.get(0) + " by which " +
                                routingTask.getEmlFile().getAbsolutePath() + " can be routed.");
                    }

                    return;
                }
            }

            // Get a general rule to route the SEPAmail message
            Rule routingRule = this.getGeneralRule(routingTask.getInputQueueType());

            // Check if a rule was obtained
            if (routingRule != null) {

                // Log the rule with which the current SEPAmail message will be routed
                Smac.logController.log(Level.FINEST, RoutingController.class.getSimpleName(), "Routing " +
                        routingTask.getEmlFile().getAbsolutePath() + " in queue " +
                        SmacQueueTypeUtils.getQueueTypeName(routingTask.getInputQueueType()) + " with rule " +
                        routingRule + ".");

                // Set the SMAC routing task properties
                routingTask.setIsRouted(true);
                routingTask.setOutputQueueType(routingRule.getOutputQueueType());

            } else {

                // An appropriate routing rule could not be found
                throw new RuleNotFoundException(routingTask.getEmlFile().getAbsolutePath(),
                        SmacQueueTypeUtils.getQueueTypeName(routingTask.getInputQueueType()));
            }

        } catch (FileNotFoundException | MessagingException e) {

            // Log errors
            Smac.logController.log(Level.WARNING, RoutingController.class.getSimpleName(), e.getMessage());
        }
    }

    /**
     * Get the appropriate general rule by which will be used to route the given message to its output queue
     *
     * @param inputQueueType Routing task input queue type
     * @return The general rule that will be used to route the message if it has been defined
     */
    private Rule getGeneralRule(SmacQueueType inputQueueType) {

        // Scan the list of rules which has already been ordered by priority
        for (int i = 0; i < this.rules.size(); i++) {

            // Current rule
            Rule currentRule = this.rules.get(i);

            // Check the input queue of the rule
            if (currentRule.getInputQueueType() == inputQueueType && currentRule.getFrom() == null
                    && currentRule.getTo() == null) {

                // We have a matching rule based on input queue type
                return currentRule;
            }
        }

        return null;
    }

    /**
     * Get a list of rules which matches the given to and from addresses and input queue type
     *
     * @param from The SEPAmail message sender's email address
     * @param to The SEPAmail message recipient's email address
     * @param inputQueueType Routing task input queue type
     * @return List of rules which match the given set of criteria
     */
    private ArrayList<Rule> getMatchingRules(String from, String to, SmacQueueType inputQueueType) {

        // List of rules
        ArrayList<Rule> matchingRules = new ArrayList<>();

        // Scan the list of rules
        for (int i = 0; i < this.rules.size(); i++) {

            // Current rule
            Rule currentRule = this.rules.get(i);

            // Check the input queue of the rule
            if (currentRule.getInputQueueType() == inputQueueType && currentRule.getFrom() != null
                    && currentRule.getTo() != null) {

                // Check if the from email addresses are matching
                if (currentRule.getFrom().toLowerCase().equals(from.toLowerCase())) {

                    // Check if the to addresses are matching
                    if (currentRule.getTo().toLowerCase().equals(to.toLowerCase())) {

                        // We have a matching rule
                        matchingRules.add(currentRule);
                    }
                }
            }
        }

        return matchingRules;
    }

    /**
     * Route associated SEPAmail message container files based on the 'sepamail-mode' code
     *
     * @param routingTask SMAC daemon routing task
     * @param mode 'sepamail-mode' code as specified in the SEPAmail message container file
     */
    private void doPreliminaryRouting(RoutingTask routingTask, String mode) {

        // Output queue type as per the 'sepamail-mode' code
        SmacQueueType outputQueueType = mode.equals("canonical") ? SmacQueueType.CSO : SmacQueueType.FHO;

        // Verify that the required output queue exists
        if (this.outputQueues.indexOf(new OutputQueue(outputQueueType)) > -1) {

            // Route the task since the required output queue exists
            routingTask.setOutputQueueType(outputQueueType);
            routingTask.setIsRouted(true);
        }
    }
}
