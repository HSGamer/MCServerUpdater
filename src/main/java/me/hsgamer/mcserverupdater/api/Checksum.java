package me.hsgamer.mcserverupdater.api;

import java.io.File;

public interface Checksum {
    boolean checksum(File file, String version, String build) throws Exception;
}
