package me.hsgamer.mcserverupdater.api;

import me.hsgamer.mcserverupdater.UpdateBuilder;

import java.io.File;

public interface Updater {
    boolean update(File file, String version) throws Exception;

    String getDefaultVersion();

    UpdateBuilder getUpdateBuilder();
}
