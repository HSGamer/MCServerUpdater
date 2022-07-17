package me.hsgamer.mcserverupdater.api;

import me.hsgamer.mcserverupdater.util.ChecksumUtils;

import java.io.File;

public interface SimpleChecksum extends Checksum {
    String getChecksum(String version, String build);

    default String getCurrentChecksum(File file) throws Exception {
        return ChecksumUtils.getChecksumSupplier().get();
    }

    @Override
    default boolean checksum(File file, String version, String build) throws Exception {
        String checksum = getChecksum(version, build);
        if (checksum == null) {
            return false;
        }
        String currentChecksum = getCurrentChecksum(file);
        return currentChecksum.equals(checksum);
    }

    @Override
    default void setChecksum(File file, String version, String build) throws Exception {
        ChecksumUtils.getChecksumConsumer().accept(getChecksum(version, build));
    }
}
