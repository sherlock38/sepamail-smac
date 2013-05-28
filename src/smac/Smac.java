package smac;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import smac.controller.InputQueueController;
import smac.controller.LogController;
import smac.exception.ConfigurationFileNotFoundException;
import smac.exception.InvalidConfigurationFileException;
import smac.model.Config;
import smac.model.InputQueue;
import smac.util.SmacConfigReader;
import smac.util.SmacQueueTypeUtils;

/**
 * The Smac class provides an entry point to the SMAC daemon.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 0.5
 */
public class Smac {

    // SMAC daemon constants
    public static final String CONFIG_FILENAME = "conf" + System.getProperty("file.separator") + "smac.properties";

    // SMAC static attributes
    public static LogController logController;
    public static Config config;

    /**
     * SMAC daemon entry point
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {

        try {

            // Initialise configuration file reader and parser
            SmacConfigReader smacConfigReader = new SmacConfigReader(CONFIG_FILENAME);

            // Parse the configuration file
            config = smacConfigReader.parse();

            // Get instance of the log controller
            logController = LogController.getLogController();

            // List of input queue controllers
            ArrayList<Thread> threads = new ArrayList<>();

            // Start an input queue controller instance for each SMAC active queue
            for (int i = 0; i < config.getInputQueues().size(); i++) {

                // Current SMAC input queue
                InputQueue smacQueue = config.getInputQueues().get(i);

                // Check if the current SMAC input queue is active
                if (smacQueue.getIsActive()) {

                    try {

                        // Create thread for current active SMAC input queue
                        Runnable task = new InputQueueController(smacQueue);
                        Thread worker = new Thread(task);

                        // Set the name of the input queue thread
                        worker.setName(SmacQueueTypeUtils.getQueueTypeName(smacQueue.getQueueType()) +
                                " input queue thread");

                        // Start the thread for the current SMAC queue
                        worker.start();

                        // Add the current input queue thread to the list of threads
                        threads.add(worker);

                    } catch (MalformedURLException e) {

                        // Log errors
                        Smac.logController.log(Level.SEVERE, InputQueueController.class.getSimpleName(),
                                e.getMessage());

                    }
                }
            }

        } catch (ConfigurationFileNotFoundException | MalformedURLException e) {

            // Display error on console
            System.out.println(e.getMessage());

        } catch (IOException | InvalidConfigurationFileException  e) {

            // Display error on console
            System.out.println(e.getMessage());
        }
    }
}
