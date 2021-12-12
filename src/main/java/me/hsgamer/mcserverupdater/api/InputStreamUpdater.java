package me.hsgamer.mcserverupdater.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public interface InputStreamUpdater extends Updater {
    InputStream getInputStream(String version, String build);

    @Override
    default boolean update(File file, String version, String build) throws IOException {
        try (InputStream inputStream = getInputStream(version, build)) {
            if (inputStream == null) {
                return false;
            }
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
    }
}
