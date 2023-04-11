package me.hsgamer.mcserverupdater.api;

import me.hsgamer.mcserverupdater.UpdateBuilder;

import java.io.File;

public interface Updater {
    boolean update(File file) throws Exception;

    UpdateBuilder getUpdateBuilder();
}
