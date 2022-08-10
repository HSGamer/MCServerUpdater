package me.hsgamer.mcserverupdater.api;

import java.io.File;

public interface SimpleChecksum extends Checksum {
    String getChecksum(String version, String build);

    String getCurrentChecksum(File file) throws Exception;

    @Override
    default boolean checksum(File file, String version, String build) throws Exception {
        String checksum = getChecksum(version, build);
        if (checksum == null) {
            return false;
        }
        String currentChecksum = getCurrentChecksum(file);
        return currentChecksum.equals(checksum);
    }
}
