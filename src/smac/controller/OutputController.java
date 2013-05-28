package smac.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import smac.Smac;
import smac.exception.UndefinedOutputQueueException;
import smac.model.OutputQueue;
import smac.model.RoutingTask;
import smac.util.SmacDirectoryUtils;
import smac.util.SmacFileUtils;
import smac.util.SmacQueueType;
import smac.util.SmacQueueTypeUtils;

/**
 * OutputController moves a SEPAmail message to its routed SMAC output queue.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class OutputController {

    // Class attributes
    private final ArrayList<OutputQueue> outputQueues;

    /**
     * OutputController default constructor
     */
    public OutputController() {

        // Initialise class attributes
        this.outputQueues = Smac.config.getOutputQueues();
    }

    /**
     * Move a SEPAmail message EML container file to its routed output directory
     *
     * @param routingTask SMAC daemon routing task instance with routing details
     * @throws UndefinedOutputQueueException
     * @throws MalformedURLException
     */
    public void move(RoutingTask routingTask) throws UndefinedOutputQueueException, MalformedURLException {

        // Get the output queue which corresponds to the output destination of the file associated to the current task
        OutputQueue outputQueue = this.getOuputQueue(routingTask.getOutputQueueType());

        // Check if output queue was obtained
        if (outputQueue != null) {

            // Output queue directory
            File outputQueueDir = new File(outputQueue.getQueueDirectory());

            // Check if the output queue directory exists
            if (!(outputQueueDir.exists() && outputQueueDir.isDirectory())) {

                // Create the output queue directory
                SmacDirectoryUtils.createFolderIfNotExist(outputQueueDir.getAbsolutePath());

                // Indicate that the output directory has been created
                Smac.logController.log(Level.INFO, OutputController.class.getSimpleName(), "The directory " +
                        outputQueueDir.getAbsolutePath() + " for the output queue " +
                        SmacQueueTypeUtils.getQueueTypeName(outputQueue.getQueueType()) + " has been created.");
            }

            // Move the EML file
            SmacFileUtils.moveFile(routingTask.getEmlFile().getAbsolutePath(), outputQueueDir.getAbsolutePath());

        } else {

            // Output queue type
            SmacQueueType outputQueueType = routingTask.getOutputQueueType();

            // The output queue was not found
            throw new UndefinedOutputQueueException(SmacQueueTypeUtils.getQueueTypeName(outputQueueType));
        }
    }

    /**
     * Get the output queue object which corresponds to the given queue type
     *
     * @param queueType Type of queue
     * @return OutputQueue object which corresponds to the given queue type
     */
    private OutputQueue getOuputQueue(SmacQueueType queueType) {

        // Scan the list of output queues
        for (int i = 0; i < this.outputQueues.size(); i++) {

            // Current output queue
            OutputQueue currentQueue = this.outputQueues.get(i);

            // Check the queue type of the current output queue
            if (currentQueue.getQueueType() == queueType) {
                return currentQueue;
            }
        }

        return null;
    }
}
