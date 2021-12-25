package me.hsgamer.mcserverupdater;

/**
 * The update status
 */
public final class UpdateStatus {
    /**
     * The project is not found or not supported
     */
    public static final UpdateStatus NO_PROJECT = new UpdateStatus(false, "No project found");
    /**
     * The version is not found
     */
    public static final UpdateStatus NO_VERSION = new UpdateStatus(false, "No version found");
    /**
     * The build is not found
     */
    public static final UpdateStatus NO_BUILD = new UpdateStatus(false, "No build found");
    /**
     * Failed to create the output file
     */
    public static final UpdateStatus FILE_FAILED = new UpdateStatus(false, "File failed to create");
    /**
     * The output file is up-to-date
     */
    public static final UpdateStatus UP_TO_DATE = new UpdateStatus(true, "Up-to-date version");
    /**
     * Update successfully
     */
    public static final UpdateStatus SUCCESS = new UpdateStatus(true, "Successfully updated");
    /**
     * Failed to update
     */
    public static final UpdateStatus FAILED = new UpdateStatus(false, "Failed");


    private final boolean isSuccessStatus;
    private final Throwable throwable;

    private UpdateStatus(boolean isSuccessStatus, Throwable throwable) {
        this.isSuccessStatus = isSuccessStatus;
        this.throwable = throwable;
    }

    private UpdateStatus(boolean isSuccessStatus, String message) {
        this(isSuccessStatus, new Exception(message));
    }

    /**
     * Create an unknown error status
     *
     * @param cause the cause of the error
     * @return the unknown error status
     */
    public static UpdateStatus unknownError(Throwable cause) {
        return new UpdateStatus(false, cause);
    }

    /**
     * Check if the update is successful
     *
     * @return true if the update is successful
     */
    public boolean isSuccessStatus() {
        return isSuccessStatus;
    }

    /**
     * Get the throwable
     *
     * @return the throwable, or null if there is no error
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Get the message
     *
     * @return the message, or null if there is no error
     */
    public String getMessage() {
        if (throwable == null) return null;
        return throwable.getMessage();
    }
}
