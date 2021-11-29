package me.hsgamer.mcserverupdater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;

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
}
