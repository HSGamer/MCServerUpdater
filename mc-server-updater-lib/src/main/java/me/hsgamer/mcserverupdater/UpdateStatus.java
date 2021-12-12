package me.hsgamer.mcserverupdater;

/**
 * The update status
 */
public enum UpdateStatus {
    /**
     * The project is not found or not supported
     */
    NO_PROJECT,
    /**
     * The version is not found
     */
    NO_VERSION,
    /**
     * The build is not found
     */
    NO_BUILD,
    /**
     * Failed to create the output file
     */
    FILE_FAILED,
    /**
     * The output file is up-to-date
     */
    UP_TO_DATE,
    /**
     * Update successfully
     */
    SUCCESS,
    /**
     * Failed to update
     */
    FAILED
}
