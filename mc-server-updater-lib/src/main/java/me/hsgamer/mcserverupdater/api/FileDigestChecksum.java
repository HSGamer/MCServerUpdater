package me.hsgamer.mcserverupdater.api;

import me.hsgamer.mcserverupdater.util.Utils;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;

public interface FileDigestChecksum extends SimpleChecksum {
    MessageDigest getMessageDigest() throws Exception;

    @Override
    default String getFileChecksum(File file) throws Exception {
        MessageDigest messageDigest = getMessageDigest();
        messageDigest.update(Files.readAllBytes(file.toPath()));
        byte[] checksumValue = messageDigest.digest();
        return Utils.toHex(checksumValue);
    }
}
