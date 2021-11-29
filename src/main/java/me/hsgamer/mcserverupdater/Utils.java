package me.hsgamer.mcserverupdater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {
    private Utils() {
        // EMPTY
    }

    public static String getContent(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder urlString = new StringBuilder();
        String current;
        while ((current = reader.readLine()) != null) {
            urlString.append(current);
        }
        return urlString.toString();
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
