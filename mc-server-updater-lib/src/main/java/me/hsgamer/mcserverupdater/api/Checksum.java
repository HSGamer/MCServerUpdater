package me.hsgamer.mcserverupdater.api;

import java.io.File;

public interface Checksum {
    boolean checksum(File file, String version) throws Exception;

    default void setChecksum(File file, String version) throws Exception {
        // EMPTY
    }
}
