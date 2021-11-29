package me.hsgamer.mcserverupdater.api;

import me.hsgamer.mcserverupdater.Utils;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;

import static me.hsgamer.mcserverupdater.MCServerUpdater.LOGGER;

public interface DigestChecksum extends Checksum {
    String getChecksum(String version, String build);

    MessageDigest getMessageDigest() throws Exception;

    @Override
    default boolean checksum(File file, String version, String build) throws Exception {
        String checksumCode = getChecksum(version, build);
        if (checksumCode == null) {
            LOGGER.warning("Checksum not found");
            return false;
        }
        MessageDigest messageDigest = getMessageDigest();
        messageDigest.update(Files.readAllBytes(file.toPath()));
        byte[] checksumValue = messageDigest.digest();
        String checksumString = Utils.toHex(checksumValue);
        LOGGER.info(() -> "Checksum: " + checksumString);
        LOGGER.info(() -> "Expected: " + checksumCode);
        return checksumString.equals(checksumCode);
    }
}
