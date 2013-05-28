package smac.model;

import smac.util.SmacQueueType;
import smac.util.SmacQueueTypeUtils;

/**
 * Rule class defines a rule which is used by the SMAC daemon to route an incoming message to an outgoing queue.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class Rule implements Comparable<Rule> {

    // Class attributes
    private String from;
    private SmacQueueType inputQueueType;
    private int order;
    private SmacQueueType outputQueueType;
    private int ruleSet;
    private String to;

    /**
     * Get the sender email address of the SMAC rule
     *
     * @return The from email address of the SMAC rule
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * Get the input queue type of the SMAC rule
     *
     * @return The input queue type of the SMAC rule
     */
    public SmacQueueType getInputQueueType() {
        return this.inputQueueType;
    }

    /**
     * Get the order of the SMAC rule with its rule set
     *
     * @return Order of the SMAC rule with its rule set
     */
    public int getOrder() {
        return this.order;
    }

    /**
     * Get the output queue type of the SMAC rule
     *
     * @return The output queue type of the SMAC rule
     */
    public SmacQueueType getOutputQueueType() {
        return this.outputQueueType;
    }

    /**
     * Get the rule set of the current SMAC rule
     *
     * @return Rule set of the current SMAC rule
     */
    public int getRuleSet() {
        return this.ruleSet;
    }

    /**
     * Get the recipient email address of the SMAC rule
     *
     * @return The to email address of the SMAC rule
     */
    public String getTo() {
        return this.to;
    }

    /**
     * Set the sender email address of the SMAC rule
     *
     * @param from The from email address of the SMAC rule
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Set the input queue type of the SMAC rule
     *
     * @param inputQueueType The input queue type of the SMAC rule
     */
    public void setInputQueueType(SmacQueueType inputQueueType) {
        this.inputQueueType = inputQueueType;
    }

    /**
     * Set the order of the SMAC rule within its rule set
     *
     * @param order The order of the SMAC rule within its rule set
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Set the output queue of the SMAC rule
     *
     * @param outputQueueType The output queue of the SMAC rule
     */
    public void setOutputQueue(SmacQueueType outputQueueType) {
        this.outputQueueType = outputQueueType;
    }

    /**
     * Set the rule set to which a rule belongs
     *
     * @param ruleSet The rule set to which a rule belongs
     */
    public void setRuleSet(int ruleSet) {
        this.ruleSet = ruleSet;
    }

    /**
     * Set the recipient email address of the SMAC rule
     *
     * @param to The recipient email address of the SMAC rule
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Rule constructor for SMAC rule having input and output queues only
     *
     * @param ruleSet The set to which the rule belongs to
     * @param order The order of the rule within its set
     * @param inputQueueType The input queue type of the SMAC rule
     * @param outputQueueType The output queue of the SMAC rule
     */
    public Rule(int ruleSet, int order, SmacQueueType inputQueueType, SmacQueueType outputQueueType) {

        // Initialise class attributes
        this.from = null;
        this.inputQueueType = inputQueueType;
        this.order = order;
        this.outputQueueType = outputQueueType;
        this.ruleSet = ruleSet;
        this.to = null;
    }

    /**
     * Rule constructor for SMAC rule having input and output queues and the email addresses of the sender and
     * receiver
     *
     * @param ruleSet The set to which the rule belongs to
     * @param order The order of the rule within its set
     * @param from The sender email address of the SMAC rule
     * @param to The recipient email address of the SMAC rule
     * @param inputQueueType The input queue type of the SMAC rule
     * @param outputQueueType The output queue of the SMAC rule
     */
    public Rule(int ruleSet, int order, String from, String to, SmacQueueType inputQueueType,
            SmacQueueType outputQueueType) {

        // Initialise class attributes
        this.from = from;
        this.inputQueueType = inputQueueType;
        this.order = order;
        this.outputQueueType = outputQueueType;
        this.ruleSet = ruleSet;
        this.to = to;
    }

    /**
     * Compare the order of the class to that of the given instance
     *
     * @param t Given class instance
     * @return Whether the current instance is greater than the given instance
     */
    @Override
    public int compareTo(Rule t) {

        // Check the rule set of the given instance
        if (this.ruleSet > t.ruleSet) {
            return 1;
        } else if (this.ruleSet < t.ruleSet) {
            return -1;
        } else {

            // Since the rule set are equal, we check the order of the rule
            if (this.order > t.order) {
                return 1;
            } else if (this.order < t.order) {
                return -1;
            }
        }

        // The rules have identical rule sets and orders
        return 0;
    }

    /**
     * Compare the current rule object instance to that of a given instance using the rule set and order
     *
     * @param anObject Given Rule object instance
     * @return Whether the two objects are equal
     */
    @Override
    public boolean equals(Object anObject) {

        // Check if object is being compared to its own instance
        if (this == anObject) {
            return true;
        }

        // Check if we are comparing objects of the same type
        if (!(anObject instanceof Rule)) {
            return false;
        }

        final Rule smacRule = (Rule)anObject;

        // Compare the rule set and order of the SMAC rule objects
        if (this.ruleSet == smacRule.ruleSet && this.order == smacRule.order) {
            return true;
        }

        return false;
    }

    /**
     * Generate a hash code for a SMAC rule object based on its rule set and order
     *
     * @return Hash code for a Rule object
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + this.order;
        hash = 71 * hash + this.ruleSet;
        return hash;
    }

    /**
     * Get the string representation of the SMAC rule
     *
     * @return The string representation of the SMAC rule
     */
    @Override
    public String toString() {

        String representation = "Rule " + this.ruleSet + "." + this.order + " : ";

        // Build the string representation of the SMAC rule
        if (this.from != null) {
            representation += "From: " + this.from + ", ";
        }

        if (this.to != null) {
            representation += "To: " + this.to + ", ";
        }

        representation += SmacQueueTypeUtils.getQueueTypeName(this.inputQueueType) + ", " +
                SmacQueueTypeUtils.getQueueTypeName(this.outputQueueType);

        return representation;
    }
}
