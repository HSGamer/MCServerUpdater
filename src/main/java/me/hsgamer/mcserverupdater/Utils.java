package me.hsgamer.mcserverupdater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;

import java.io.File;
import java.io.IOException;

import static me.hsgamer.mcserverupdater.MCServerUpdater.LOGGER;

public class Utils {
    private Utils() {
        // EMPTY
    }

    public static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static boolean checkInternetConnection() {
        try {
            WebUtils.openConnection("https://www.google.com", UserAgent.CHROME);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFailedToCreateFile(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            LOGGER.severe("Could not create parent directory");
            return true;
        }
        if (!file.createNewFile()) {
            LOGGER.severe("Could not create output file");
            return true;
        }
        return false;
    }
}
