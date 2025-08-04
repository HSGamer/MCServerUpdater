package io.github.projectunified.mcserverupdater.api;

import java.io.File;

public interface Checksum {
    boolean checksum(File file) throws Exception;

    default void setChecksum(File file) throws Exception {
        // EMPTY
    }
}
