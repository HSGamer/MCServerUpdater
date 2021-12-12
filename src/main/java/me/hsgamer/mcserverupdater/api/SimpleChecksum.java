package me.hsgamer.mcserverupdater.api;

import java.io.File;

public interface SimpleChecksum extends Checksum {
    String getChecksum(String version, String build);

    String getFileChecksum(File file) throws Exception;

    @Override
    default boolean checksum(File file, String version, String build) throws Exception {
        String checksumCode = getChecksum(version, build);
        if (checksumCode == null) {
            return false;
        }
        String checksumString = getFileChecksum(file);
        return checksumString.equals(checksumCode);
    }
}
