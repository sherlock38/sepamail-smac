package smac.util;

/**
 * SmacQueueType defines the various queue types for either input or output which are available in the SMAC daemon.
 *
 * @author Bishan Kumar Madhoo <bishan.madhoo@idsoft.mu>
 * @version 1.0
 */
public enum SmacQueueType {

    /**
     * Canonical Simple Input
     */
    CSI,

    /**
     * Canonical Simple Output
     */
    CSO,

    /**
     * Flash Heavy Output
     */
    FHI,

    /**
     * Flash Light Input
     */
    FHO,

    /**
     * Flash Light Input
     */
    FLI,

    /**
     * Flash Light Output
     */
    FLO
}
