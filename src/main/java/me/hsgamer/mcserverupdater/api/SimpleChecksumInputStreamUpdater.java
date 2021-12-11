package me.hsgamer.mcserverupdater.api;

import me.hsgamer.mcserverupdater.Utils;

import java.io.File;
import java.io.IOException;

public interface SimpleChecksumInputStreamUpdater extends SimpleChecksum, InputStreamUpdater {
    File getChecksumFile() throws IOException;

    @Override
    default String getFileChecksum(File file) throws Exception {
        File checksumFile = getChecksumFile();
        return Utils.getString(checksumFile);
    }

    @Override
    default boolean update(File file, String version, String build) throws IOException {
        boolean success = InputStreamUpdater.super.update(file, version, build);
        if (success) {
            File checksumFile = getChecksumFile();
            try {
                Utils.writeString(checksumFile, getChecksum(version, build));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }
}
