package smac.util;

import smac.exception.InvalidQueueTypeNameException;

/**
 * SmacQueueTypeUtils provides statics functions for mapping queue type names to their SmacQueueType counterparts.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public class SmacQueueTypeUtils {

    /**
     * Get the SmacQueueType equivalent of a queue type name
     *
     * @param queueTypeName Queue type string
     * @return SmacQueueType equivalent of a queue type string
     * @throws InvalidQueueTypeNameException
     */
    public static SmacQueueType getQueueType(String queueTypeName) throws InvalidQueueTypeNameException {

        // Convert the queue type name to lower case
        queueTypeName = queueTypeName.toLowerCase();

        // Get the equivalent SmacQueueType of the queue type name
        switch (queueTypeName) {

            case "csi" :

                // Canonical Simple Input
                return SmacQueueType.CSI;

            case "cso" :

                // Canonical Simple Output
                return SmacQueueType.CSO;

            case "fhi" :

                // Flash Heavy Input
                return SmacQueueType.FHI;

            case "fho" :

                // Flash Heavy Output
                return SmacQueueType.FHO;

            case "fli" :

                // Flash Light Input
                return SmacQueueType.FLI;

            case "flo" :

                // Flash Light Output
                return SmacQueueType.FLO;

            default :

                // The queue type name equivalent could not be determined
                throw new InvalidQueueTypeNameException(queueTypeName);
        }
    }

    /**
     * Get the SmacQueueType equivalent of a queue type name
     *
     * @param queueType Queue type
     * @return SMAC queue type name
     */
    public static String getQueueTypeName(SmacQueueType queueType) {

        // Get the equivalent queue type name for a queue type
        switch (queueType) {

            case CSI :

                // Canonical Simple Input
                return "CSI";

            case CSO :

                // Canonical Simple Output
                return "CSO";

            case FHI :

                // Flash Heavy Input
                return "FHI";

            case FHO :

                // Flash Heavy Output
                return "FHO";

            case FLI :

                // Flash Light Input
                return "FLI";

            case FLO :

                // Flash Light Output
                return "FLO";

        }

        return null;
    }
}
