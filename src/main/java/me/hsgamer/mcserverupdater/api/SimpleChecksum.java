package me.hsgamer.mcserverupdater.api;

import java.io.File;

import static me.hsgamer.mcserverupdater.MCServerUpdater.LOGGER;

public interface SimpleChecksum extends Checksum {
    String getChecksum(String version, String build);

    String getFileChecksum(File file) throws Exception;

    @Override
    default boolean checksum(File file, String version, String build) throws Exception {
        String checksumCode = getChecksum(version, build);
        if (checksumCode == null) {
            LOGGER.warning("Checksum not found");
            return false;
        }
        String checksumString = getFileChecksum(file);
        LOGGER.info(() -> "Checksum: " + checksumString);
        LOGGER.info(() -> "Expected: " + checksumCode);
        return checksumString.equals(checksumCode);
    }
}
