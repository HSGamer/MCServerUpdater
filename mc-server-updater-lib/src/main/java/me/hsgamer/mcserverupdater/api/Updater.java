package me.hsgamer.mcserverupdater.api;

import java.io.File;

public interface Updater {
    boolean update(File file, String version, String build) throws Exception;

    String getDefaultVersion();
}
