package me.hsgamer.mcserverupdater.api;

import java.io.File;

public interface SimpleChecksum extends Checksum {
    String getChecksum(String version);

    String getCurrentChecksum(File file) throws Exception;

    @Override
    default boolean checksum(File file, String version) throws Exception {
        String checksum = getChecksum(version);
        if (this instanceof Updater) {
            ((Updater) this).getUpdateBuilder().debug("Checksum: " + checksum);
        }
        if (checksum == null) {
            return false;
        }
        String currentChecksum = getCurrentChecksum(file);
        if (this instanceof Updater) {
            ((Updater) this).getUpdateBuilder().debug("Current checksum: " + currentChecksum);
        }
        return currentChecksum.equals(checksum);
    }
}
